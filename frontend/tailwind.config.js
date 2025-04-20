/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#0047AB', // Telecom blue
        secondary: '#FF6B00', // Orange for CTAs
        accent: '#FFD700', // Gold for discounts
      },
    },
  },
  plugins: [],
} 