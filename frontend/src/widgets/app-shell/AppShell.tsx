import type { PropsWithChildren } from 'react';

import { AppHeader } from '@/widgets/app-header/AppHeader.tsx';
import { Sidebar } from '@/widgets/sidebar/Sidebar.tsx';
import styles from '@/widgets/app-shell/AppShell.module.css';

export function AppShell({ children }: PropsWithChildren) {
  return (
    <div className={styles.shell}>
      <Sidebar />
      <div className={styles.main}>
        <AppHeader />
        <main className={styles.content}>{children}</main>
      </div>
    </div>
  );
}
