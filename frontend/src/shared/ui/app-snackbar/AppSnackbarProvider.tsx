import { Alert, Snackbar } from '@mui/material';
import { useCallback, useMemo, useState, type PropsWithChildren } from 'react';

import { AppSnackbarContext, type SnackbarSeverity, type SnackbarState } from './AppSnackbarContext.ts';

export const AppSnackbarProvider = ({ children }: PropsWithChildren) => {
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
};
