import { Box, CircularProgress } from '@mui/material';

export const PageLoader = () => {
  return (
    <Box
      sx={{
        minHeight: '40vh',
        display: 'grid',
        placeItems: 'center',
      }}
    >
      <CircularProgress />
    </Box>
  );
};
