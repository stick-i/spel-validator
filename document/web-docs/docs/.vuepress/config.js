import {defaultTheme} from '@vuepress/theme-default'
import {defineUserConfig} from 'vuepress/cli'
import {viteBundler} from '@vuepress/bundler-vite'

export default defineUserConfig({
    lang: 'zh-CN',

    title: 'SpEL Validator',
    description: '一个强大的 Java 参数校验包，基于 SpEL 实现，扩展自 javax.validation 包，几乎支持所有场景下的参数校验。',

    theme: defaultTheme({
        logo: 'https://vuejs.press/images/hero.png',
        navbar: ['/', '/get-started'],
    }),

    bundler: viteBundler(),
})
