/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}"
    ],
    darkMode: "class",  // Use the 'class' strategy for dark mode
    theme: {
        extend: {
            colors: {
                "primary": "#4CAF50",
                "background-light": "#F8F9FA",
                "background-dark": "#181a1b",
                "text-light": "#343A40",
                "text-dark": "#e8e6e3",
                "card-light": "#FFFFFF",
                "card-dark": "#2d2d2d",
                "border-light": "#dee2e6",
                "border-dark": "#444444",
                "text-secondary-light": "#6c757d",
                "text-secondary-dark": "#adb5bd",
                "accent-warm": "#FFD54F"
            },
            fontFamily: {
                display: ["Inter", "Noto Sans KR", "sans-serif"]
            },
            borderRadius: {
                DEFAULT: "0.25rem",
                lg: "0.5rem",
                xl: "0.75rem",
                full: "9999px"
            }
        }
    },
    plugins: [
        require('@tailwindcss/forms'),
        require('@tailwindcss/container-queries')  // Remove this if using Tailwind CSS v4+
    ]
}
