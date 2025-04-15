import type {SidebarOptions} from '@vuepress/theme-default'

export const sidebarZh: SidebarOptions = {
    '/guide/': [
        {
            text: '指南',
            children: [
                '/guide/introduction.md',
                '/guide/getting-started.md',
                '/guide/user-guide.md',
                '/guide/spel.md',
                '/guide/custom.md',
                '/guide/i18n.md',
                '/guide/principle.md',
                '/guide/FAQ.md',
            ],
        },
    ],
}
