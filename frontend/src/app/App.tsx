import { BrowserRouter } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { Provider } from 'react-redux';

import { selectThemeMode } from '@/app/app-slice.ts';
import { store } from '@/app/store.ts';
import { ErrorSnackbar } from '@/common/components/index.ts';
import { useAppSelector } from '@/common/hooks/index.ts';
import { Routing } from '@/common/routing/index.ts';
import { getTheme } from '@/common/theme/index.ts';
import { AppSnackbarProvider } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';

const AppContent = () => {
  const themeMode = useAppSelector(selectThemeMode);
  const theme = getTheme(themeMode);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppSnackbarProvider>
        <BrowserRouter>
          <Routing />
          <ErrorSnackbar />
        </BrowserRouter>
      </AppSnackbarProvider>
    </ThemeProvider>
  );
};

export const App = () => {
  return (
    <Provider store={store}>
      <AppContent />
    </Provider>
  );
};
