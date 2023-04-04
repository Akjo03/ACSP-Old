const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  content: [
    "./components/**/*.{js,vue,ts}",
    "./layouts/**/*.vue",
    "./pages/**/*.vue",
    "./plugins/**/*.{js,ts}",
    "./nuxt.config.{js,ts}",
    "./app.vue",
  ],
  theme: {
    extend: {
      screens: {
        "sm": "480px",
        "md": "768px",
        "lg": "968px",
        "xl": "1440px",
      },
      fontSize: {
        'xs': '0.75rem',
        'sm': '0.875rem',
        'base': '1rem',
        'lg': '1.125rem',
        'xl': '1.25rem',
        '2xl': '1.5rem',
        '3xl': '1.875rem',
        '4xl': '2.25rem',
        '5xl': '3rem',
        '6xl': '3.75rem',
        '7xl': '4.5rem',
        '8xl': '6rem',
        '9xl': '8rem',
      },
      fontFamily: {
        sans: ['Inter var', ...defaultTheme.fontFamily.sans],
      },
      colors: {
        themeBackgroundGradientStart: 'var(--backgroundGradientStart)',
        themeBackgroundGradientEnd: 'var(--backgroundGradientEnd)',

        themeText: 'var(--text)',

        themeButtonText: 'var(--buttonText)',
        themeButtonPrimary: 'var(--buttonPrimary)',
        themeButtonPrimaryHover: 'var(--buttonPrimaryHover)',
        themeButtonPrimaryActive: 'var(--buttonPrimaryActive)',
        themeButtonSecondary: 'var(--buttonSecondary)',
        themeButtonSecondaryHover: 'var(--buttonSecondaryHover)',
        themeButtonSecondaryActive: 'var(--buttonSecondaryActive)',
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/aspect-ratio'),
    require('tailwindcss-elevation'),
  ],
}
