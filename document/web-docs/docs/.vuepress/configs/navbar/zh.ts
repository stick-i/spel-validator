import {version} from '../meta'
import type {NavbarOptions} from "@vuepress/theme-default";

export const navbarZh: NavbarOptions = [
    {
        text: '指南',
        children: [
            '/guide/introduction.md',
            '/guide/getting-started.md',
        ],
    },
    {
        text: `v${version}`,
        children: [
            {
                text: '更新日志',
                link: 'https://github.com/stick-i/spel-validator/releases',
            },
        ],
    },
]
