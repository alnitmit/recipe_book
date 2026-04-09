import { CssBaseline, ThemeProvider } from '@mui/material';
import { Provider } from 'react-redux';
import { RouterProvider } from 'react-router-dom';

import { router } from '@/app/router.tsx';
import { store } from '@/app/store.ts';
import { theme } from '@/app/theme.ts';
import { AppSnackbarProvider } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';

export function AppProviders() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AppSnackbarProvider>
          <RouterProvider router={router} />
        </AppSnackbarProvider>
      </ThemeProvider>
    </Provider>
  );
}
