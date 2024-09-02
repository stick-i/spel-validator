import type {HeadConfig} from 'vuepress/core'

export const head: HeadConfig[] = [
    [
        'link',
        {
            rel: 'icon',
            type: 'image/png',
            sizes: '16x16',
            href: `/images/logo.png`,
        },
    ],
    [
        'link',
        {
            rel: 'icon',
            type: 'image/png',
            sizes: '32x32',
            href: `/images/logo.png`,
        },
    ],
    ['link', {rel: 'manifest', href: '/manifest.webmanifest'}],
    ['meta', {name: 'application-name', content: 'SpEL Validator'}],
    ['meta', {name: 'apple-mobile-web-app-title', content: 'SpEL Validator'}],
    ['meta', {name: 'apple-mobile-web-app-status-bar-style', content: 'black'}],
    [
        'link',
        {rel: 'apple-touch-icon', href: `/images/logo.png`},
    ],
    [
        'link',
        {
            rel: 'mask-icon',
            href: '/images/logo.png',
            color: '#3eaf7c',
        },
    ],
    ['meta', {name: 'msapplication-TileColor', content: '#3eaf7c'}],
    ['meta', {name: 'theme-color', content: '#3eaf7c'}],
]
