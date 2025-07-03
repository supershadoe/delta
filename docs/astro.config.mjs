// @ts-check

import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import tailwindcss from '@tailwindcss/vite';

const desc = 'Advanced hotspot settings for Pixel-like devices';
const site = 'https://delta.shadoe.dev';
const ogUrl = new URL('og.webp?v=1', site).href;
const ogImageAlt = desc;

export default defineConfig({
    site,
    image: { domains: ['raw.githubusercontent.com', 'gitlab.com'] },
    integrations: [
        starlight({
            title: 'Delta',
            description: desc,
            customCss: ['/src/styles/global.css'],
            editLink: {
                baseUrl: 'https://github.com/supershadoe/delta/edit/main/docs/',
            },
            head: [
                {
                    tag: 'meta',
                    attrs: { property: 'og:image', content: ogUrl },
                },
                {
                    tag: 'meta',
                    attrs: { property: 'og:image:alt', content: ogImageAlt },
                },
                {
                    tag: 'meta',
                    attrs: { property: 'twitter:image', content: ogUrl },
                },
            ],
            lastUpdated: true,
            logo: {
                src: '/src/assets/logo.svg',
            },
            social: [
                {
                  icon: 'github',
                  label: 'GitHub',
                  href: 'https://github.com/supershadoe/delta',
                },
            ],
        }),
    ],
    trailingSlash: 'always',
    vite: { plugins: [tailwindcss()] },
});
