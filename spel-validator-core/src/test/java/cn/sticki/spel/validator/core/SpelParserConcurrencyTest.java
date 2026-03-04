package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.exception.SpelParserException;
import cn.sticki.spel.validator.core.parse.SpelParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SpelParser 并发安全回归测试
 * <p>
 * 验证 SpelParser.parse() 每次调用都使用独立的 EvaluationContext，
 * 不会因 context 共享导致变量赋值在线程间互相干扰。
 * <p>
 * 核心原理：SpEL 赋值表达式 {@code #temp = #this.id} 会调用
 * {@code EvaluationContext.setVariable("temp", value)} 写入 context。
 * 如果 context 是共享的，多线程写入同一个 #temp 变量会产生竞态条件；
 * 如果 context 是每次新建的（正确行为），变量写入不会跨调用泄漏。
 *
 * @author 阿杆
 * @since 2026/3/3
 */
@Isolated
public class SpelParserConcurrencyTest {

    /**
     * 并发线程数
     */
    private static final int THREAD_COUNT = 16;

    /**
     * 每个线程重复执行的轮次
     */
    private static final int ITERATIONS_PER_THREAD = 1000;

    /**
     * future.get() 超时时间（秒）
     */
    private static final int FUTURE_TIMEOUT_SECONDS = 30;

    /**
     * 用于并发测试的 rootObject
     */
    public static class RootObject {

        private final int id;
        private final String name;

        public RootObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    // ==================== 单线程 context 隔离验证 ====================

    /**
     * 验证每次 parse() 调用使用独立的 EvaluationContext。
     * <p>
     * 先通过赋值表达式 {@code #temp = 42} 在 context 中写入变量，
     * 再通过 {@code #temp} 尝试读取。由于每次 parse() 创建新的 context，
     * 第二次调用中 #temp 不存在，应该抛出 {@link SpelParserException}。
     */
    @Test
    void testContextIsolationBetweenParseCalls() {
        RootObject obj = new RootObject(1, "test");

        // 赋值表达式会在 context 中设置 #temp 并返回赋值的值
        Integer assignResult = SpelParser.parse("#temp = 42", obj, Integer.class);
        Assertions.assertEquals(42, assignResult);

        // 如果 context 是每次新建的，#temp 在新 context 中不存在，应抛异常
        Assertions.assertThrows(SpelParserException.class,
                () -> SpelParser.parse("#temp", obj, Integer.class),
                "第二次 parse() 应因 #temp 不存在而抛出异常，说明 context 是独立的");
    }

    // ==================== 多线程并发验证 ====================

    /**
     * 多线程并发执行赋值表达式 {@code #temp = #this.id}，
     * 验证每个线程的返回值严格等于自己 rootObject 的 id。
     * <p>
     * 赋值表达式会调用 {@code EvaluationContext.setVariable("temp", value)}。
     * 如果 context 是共享的，并发的 setVariable 写入可能引起竞态。
     * 修复后每次 parse() 创建独立 context，赋值操作互不干扰。
     */
    @RepeatedTest(5)
    void testConcurrentVariableAssignment() throws Exception {
        runConcurrentTest((threadId, rootObject, failureCount) -> {
            Integer result = SpelParser.parse("#temp = #this.id", rootObject, Integer.class);
            if (result != threadId) {
                failureCount.incrementAndGet();
            }
        });
    }

    /**
     * 多线程并发执行包含字符串拼接和属性访问的复合表达式，
     * 验证每个线程的结果与自己的 rootObject 一致。
     */
    @RepeatedTest(5)
    void testConcurrentComplexExpression() throws Exception {
        runConcurrentTest((threadId, rootObject, failureCount) -> {
            String result = SpelParser.parse(
                    "'id=' + #this.id + ',name=' + #this.name", rootObject, String.class);
            String expected = "id=" + threadId + ",name=thread-" + threadId;
            if (!expected.equals(result)) {
                failureCount.incrementAndGet();
            }
        });
    }

    /**
     * 多线程并发执行多个不同的赋值表达式，验证变量不会跨线程泄漏。
     * <p>
     * 每个线程先赋值 {@code #a = #this.id}，再赋值 {@code #b = #this.name}，
     * 验证两次赋值的返回值都与自己的 rootObject 一致。
     */
    @RepeatedTest(5)
    void testConcurrentMultipleVariableAssignments() throws Exception {
        runConcurrentTest((threadId, rootObject, failureCount) -> {
            Integer idResult = SpelParser.parse("#a = #this.id", rootObject, Integer.class);
            String nameResult = SpelParser.parse("#b = #this.name", rootObject, String.class);

            if (idResult != threadId || !("thread-" + threadId).equals(nameResult)) {
                failureCount.incrementAndGet();
            }
        });
    }

    // ==================== 公共并发测试框架 ====================

    /**
     * 线程任务接口，每个线程在每轮迭代中执行的逻辑
     */
    @FunctionalInterface
    private interface ThreadTask {

        /**
         * @param threadId     线程编号
         * @param rootObject   该线程专属的 rootObject
         * @param failureCount 共享的失败计数器，检测到错误时调用 incrementAndGet()
         */
        void execute(int threadId, RootObject rootObject, AtomicInteger failureCount);

    }

    /**
     * 并发测试公共方法：创建多个线程并发执行指定任务，收集并断言结果。
     * <p>
     * 使用 CountDownLatch 保证所有线程同时开始，最大化竞争窗口。
     * ExecutorService 在 finally 中关闭，future.get() 设有超时保护。
     *
     * @param task 每个线程在每轮迭代中执行的任务
     */
    private void runConcurrentTest(ThreadTask task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            CountDownLatch startLatch = new CountDownLatch(1);
            AtomicInteger failureCount = new AtomicInteger(0);
            List<Future<?>> futures = new ArrayList<>();

            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadId = t;
                final RootObject rootObject = new RootObject(threadId, "thread-" + threadId);

                futures.add(executor.submit(() -> {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                        task.execute(threadId, rootObject, failureCount);
                    }
                }));
            }

            // 释放所有线程，同时开始并发执行
            startLatch.countDown();

            // 等待所有线程完成，设置超时保护
            for (Future<?> future : futures) {
                future.get(FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }

            Assertions.assertEquals(0, failureCount.get(), "存在线程间 EvaluationContext 变量干扰");
        } finally {
            executor.shutdownNow();
        }
    }

}
