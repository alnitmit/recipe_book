import { Alert, Snackbar } from '@mui/material';
import { createContext, useCallback, useContext, useMemo, useState, type PropsWithChildren } from 'react';

type SnackbarSeverity = 'success' | 'error' | 'info' | 'warning';

interface SnackbarState {
  open: boolean;
  message: string;
  severity: SnackbarSeverity;
}

interface SnackbarContextValue {
  showSnackbar: (message: string, severity?: SnackbarSeverity) => void;
}

const AppSnackbarContext = createContext<SnackbarContextValue | null>(null);

export function AppSnackbarProvider({ children }: PropsWithChildren) {
  const [state, setState] = useState<SnackbarState>({
    open: false,
    message: '',
    severity: 'info',
  });

  const showSnackbar = useCallback((message: string, severity: SnackbarSeverity = 'info') => {
    setState({
      open: true,
      message,
      severity,
    });
  }, []);

  const value = useMemo(
    () => ({
      showSnackbar,
    }),
    [showSnackbar],
  );

  return (
    <AppSnackbarContext.Provider value={value}>
      {children}
      <Snackbar
        open={state.open}
        autoHideDuration={3500}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        onClose={() => setState((previous) => ({ ...previous, open: false }))}
      >
        <Alert
          variant="filled"
          severity={state.severity}
          onClose={() => setState((previous) => ({ ...previous, open: false }))}
        >
          {state.message}
        </Alert>
      </Snackbar>
    </AppSnackbarContext.Provider>
  );
}

export function useAppSnackbar() {
  const context = useContext(AppSnackbarContext);

  if (!context) {
    throw new Error('useAppSnackbar must be used within AppSnackbarProvider');
  }

  return context;
}
