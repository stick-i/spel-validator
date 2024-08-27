import type {SidebarOptions} from '@vuepress/theme-default'

export const sidebarZh: SidebarOptions = {
    '/guide/': [
        {
            text: '指南',
            children: [
                '/guide/introduction.md',
                '/guide/getting-started.md',
            ],
        },
    ],
    '/advanced/': [
        {
            text: '深入',
            children: [
                '/advanced/architecture.md',
                '/advanced/plugin.md',
                '/advanced/theme.md',
            ],
        },
        {
            text: 'Cookbook',
            children: [
                '/advanced/cookbook/README.md',
                '/advanced/cookbook/usage-of-client-config.md',
                '/advanced/cookbook/adding-extra-pages.md',
                '/advanced/cookbook/making-a-theme-extendable.md',
                '/advanced/cookbook/passing-data-to-client-code.md',
                '/advanced/cookbook/markdown-and-vue-sfc.md',
                '/advanced/cookbook/resolving-routes.md',
            ],
        },
    ],
}
