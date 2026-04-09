import { Outlet } from 'react-router-dom';

import { AppShell } from '@/widgets/app-shell/AppShell.tsx';

export function AppLayout() {
  return (
    <AppShell>
      <Outlet />
    </AppShell>
  );
}
