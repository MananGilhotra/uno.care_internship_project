# ConfigVault Frontend

ConfigVault UI is a modern, enterprise-grade React application for securely managing configuration properties. It features a beautiful glassmorphism design, role-based access control, and dynamic animations.

## Tech Stack
- **React 18** with **Vite**
- **TypeScript** for type safety
- **Tailwind CSS v3** for utility-first styling and glassmorphism
- **React Router v6** for protected routing
- **TanStack React Query v5** for robust data fetching and caching
- **Framer Motion** for fluid animations
- **Lucide React** for crisp, scalable icons
- **Recharts** for beautiful dashboard analytics
- **React Hot Toast** for seamless notifications

## Key Features

- 🛡️ **Role-Based UI**: UI intelligently adapts based on `ROLE_ADMIN` vs `ROLE_VIEWER`.
- 🌙 **Dark/Light Mode**: Full theme support with system preference detection.
- 🎨 **Glassmorphism Aesthetic**: Premium UI design with frosted glass panels and smooth transitions.
- 📊 **Interactive Dashboard**: Real-time mock analytics built with Recharts.
- 🔍 **Filtering & Search**: Instantly find configurations across categories.

## Getting Started

### Prerequisites
Make sure you have Node.js 18+ installed.

### Installation

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```

3. Open `http://localhost:5173` in your browser.

## Default Credentials
The backend seeds the database with the following test accounts:
- **Admin**: `admin` / `password` (Full access to Create, Update, Delete)
- **Viewer**: `viewer` / `password` (Read-only access)

## Deployment (Production)
To build the application for production:

```bash
npm run build
```

This will generate a `dist` folder containing the optimized static assets, which can be deployed to NGINX, Vercel, Netlify, or any static hosting provider.
