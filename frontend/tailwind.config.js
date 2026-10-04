/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        forgeBg: '#090D16',
        forgeSurface: '#111827',
        forgeSurfaceVariant: '#1E293B',
        forgePrimary: '#818CF8',
        forgeSecondary: '#FBBF24',
        forgeTertiary: '#38BDF8',
        forgeSuccess: '#10B981',
        forgeWarning: '#F59E0B',
        forgeError: '#EF4444',
        forgeBorder: '#334155',
      }
    },
  },
  plugins: [],
}
