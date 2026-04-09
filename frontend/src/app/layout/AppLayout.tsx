import { Outlet } from 'react-router-dom';

import { AppShell } from '@/widgets/app-shell/AppShell.tsx';

export const AppLayout = () => {
  return (
    <AppShell>
      <Outlet />
    </AppShell>
  );
};
