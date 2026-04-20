import { useEffect } from 'react';

import { selectAppError, setAppErrorAC } from '@/app/app-slice.ts';
import { useAppDispatch, useAppSelector } from '@/common/hooks/index.ts';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';

export const ErrorSnackbar = () => {
  const error = useAppSelector(selectAppError);
  const dispatch = useAppDispatch();
  const { showSnackbar } = useAppSnackbar();

  useEffect(() => {
    if (!error) {
      return;
    }

    showSnackbar(error, 'error');
    dispatch(setAppErrorAC({ error: null }));
  }, [dispatch, error, showSnackbar]);

  return null;
};
