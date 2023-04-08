const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  content: [
    "./components/**/*.{js,vue,ts}",
    "./layouts/**/*.vue",
    "./pages/**/*.vue",
    "./plugins/**/*.{js,ts}",
    "./nuxt.config.{js,ts}",
    "./app.vue",
    "./error.vue",
  ],
  theme: {
    extend: {
      screens: {
        "sm": "480px",
        "md": "768px",
        "lg": "968px",
        "xl": "1280px",
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
        themeTextSubtle: 'var(--textSubtle)',
        themeTextDisabled: 'var(--textDisabled)',
        themeTextLink: 'var(--textLink)',
        themeTextLinkHover: 'var(--textLinkHover)',
        themeTextLinkDanger: 'var(--textLinkDanger)',
        themeTextLinkDangerHover: 'var(--textLinkDangerHover)',

        /* Buttons */

        themeButtonText: 'var(--buttonText)',
        themeButtonTextDisabled: 'var(--buttonTextDisabled)',

        themeButtonPrimary: 'var(--buttonPrimary)',
        themeButtonPrimaryHover: 'var(--buttonPrimaryHover)',
        themeButtonPrimaryActive: 'var(--buttonPrimaryActive)',
        themeButtonPrimaryDisabled: 'var(--buttonPrimaryDisabled)',

        themeButtonSecondary: 'var(--buttonSecondary)',
        themeButtonSecondaryHover: 'var(--buttonSecondaryHover)',
        themeButtonSecondaryActive: 'var(--buttonSecondaryActive)',
        themeButtonSecondaryDisabled: 'var(--buttonSecondaryDisabled)',

        themeButtonSuccess: 'var(--buttonSuccess)',
        themeButtonSuccessHover: 'var(--buttonSuccessHover)',
        themeButtonSuccessActive: 'var(--buttonSuccessActive)',
        themeButtonSuccessDisabled: 'var(--buttonSuccessDisabled)',

        themeButtonDanger: 'var(--buttonDanger)',
        themeButtonDangerHover: 'var(--buttonDangerHover)',
        themeButtonDangerActive: 'var(--buttonDangerActive)',
        themeButtonDangerDisabled: 'var(--buttonDangerDisabled)',

        themeButtonDiscord: 'var(--buttonDiscord)',
        themeButtonDiscordHover: 'var(--buttonDiscordHover)',
        themeButtonDiscordActive: 'var(--buttonDiscordActive)',
        themeButtonDiscordDisabled: 'var(--buttonDiscordDisabled)',

        themeButtonBorder: 'var(--buttonBorder)',
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
