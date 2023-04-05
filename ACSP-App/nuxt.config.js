export default {
    app: {
        head: {
            title: 'ACSP-App',
            meta: [
                { name: 'description', content: 'The main web application for Akjo\'s Computer Science Program.' }
            ],
            link: [
                { rel: 'stylesheet', href: 'https://rsms.me/inter/inter.css' }
            ]
        }
    },
    css: [
        "@/assets/css/styles.css",
        "@fortawesome/fontawesome-svg-core/styles.css"
    ],
    modules: [
        '@nuxt/image-edge'
    ],
    postcss: {
        plugins: {
            tailwindcss: {},
            autoprefixer: {}
        }
    },
    runtimeConfig: {
        public: {
            baseUrl: '',
            backendUrl: '',

            discordServerLink: ''
        }
    }
}