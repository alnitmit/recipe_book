import { createTheme } from '@mui/material';

import type { ThemeMode } from '@/app/app-slice.ts';

export const getTheme = (mode: ThemeMode) =>
  createTheme({
    palette: {
      mode,
      primary: {
        main: '#1f6feb',
      },
      secondary: {
        main: '#ff7a59',
      },
      background: {
        default: mode === 'dark' ? '#0f172a' : '#f4f7fb',
        paper: mode === 'dark' ? '#111827' : '#ffffff',
      },
    },
    shape: {
      borderRadius: 16,
    },
    typography: {
      fontFamily: '"Segoe UI", "Arial", sans-serif',
    },
    components: {
      MuiPaper: {
        styleOverrides: {
          root: {
            boxShadow: mode === 'dark' ? '0 18px 40px rgba(0, 0, 0, 0.24)' : '0 18px 40px rgba(15, 23, 42, 0.08)',
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 600,
          },
        },
      },
    },
  });
