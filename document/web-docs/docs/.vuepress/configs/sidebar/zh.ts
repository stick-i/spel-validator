import type { SidebarOptions } from '@vuepress/theme-default'

export const sidebarZh: SidebarOptions = {
  '/guide/': [
    {
      text: '基础',
      children: [
        '/guide/introduction.md',
        '/guide/getting-started.md',
        '/guide/user-guide.md',
        '/guide/spel.md',
        '/guide/i18n.md',
      ],
    },
    {
      text: '进阶',
      children: ['/guide/principle.md', '/guide/custom.md'],
    },
    {
      text: '其他',
      children: ['/guide/annotation-guide.md', '/guide/idea-plugin.md', '/guide/changelog.md', '/guide/FAQ.md'],
    },
  ],
}
