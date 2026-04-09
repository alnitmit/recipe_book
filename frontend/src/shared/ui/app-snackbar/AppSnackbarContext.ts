import { createContext, useContext } from 'react';

export type SnackbarSeverity = 'success' | 'error' | 'info' | 'warning';

export type SnackbarState = {
  open: boolean;
  message: string;
  severity: SnackbarSeverity;
};

export type SnackbarContextValue = {
  showSnackbar: (message: string, severity?: SnackbarSeverity) => void;
};

export const AppSnackbarContext = createContext<SnackbarContextValue | null>(null);

export const useAppSnackbar = () => {
  const context = useContext(AppSnackbarContext);

  if (!context) {
    throw new Error('useAppSnackbar must be used within AppSnackbarProvider');
  }

  return context;
};
