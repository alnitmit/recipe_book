import { Paper, Skeleton, Stack } from '@mui/material';

export const PageSkeleton = () => {
  return (
    <Stack spacing={2}>
      <Skeleton variant="text" width="32%" height={56} />
      <Paper sx={{ p: 3 }}>
        <Stack spacing={2}>
          <Skeleton variant="rounded" height={52} />
          <Skeleton variant="rounded" height={52} />
          <Skeleton variant="rounded" height={180} />
        </Stack>
      </Paper>
      <Paper sx={{ p: 3 }}>
        <Stack spacing={2}>
          <Skeleton variant="rounded" height={52} />
          <Skeleton variant="rounded" height={52} />
          <Skeleton variant="rounded" height={52} />
        </Stack>
      </Paper>
    </Stack>
  );
};
