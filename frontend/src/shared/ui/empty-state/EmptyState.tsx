import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { Paper, Typography } from '@mui/material';

import styles from '@/shared/ui/empty-state/EmptyState.module.css';

type EmptyStateProps = {
  title: string;
  description: string;
};

export const EmptyState = ({ title, description }: EmptyStateProps) => {
  return (
    <Paper className={styles.state}>
      <InfoOutlinedIcon color="disabled" fontSize="large" />
      <Typography variant="h6">{title}</Typography>
      <Typography color="textSecondary">{description}</Typography>
    </Paper>
  );
};
