import { Paper, Table, TableBody, TableCell, TableHead, TablePagination, TableRow, Typography } from '@mui/material';
import type { ReactNode } from 'react';

import styles from '@/widgets/entity-table/EntityTable.module.css';

export type EntityTableColumn<T> = {
  key: string;
  label: string;
  render: (row: T) => ReactNode;
  align?: 'left' | 'right' | 'center';
};

type EntityTableProps<T> = {
  columns: Array<EntityTableColumn<T>>;
  rows: T[];
  getRowId: (row: T) => number | string;
  actions?: (row: T) => ReactNode;
  page: number;
  rowsPerPage: number;
  totalElements: number;
  onPageChange: (nextPage: number) => void;
  onRowsPerPageChange: (nextSize: number) => void;
  emptyText?: string;
};

export const EntityTable = <T,>({
  columns,
  rows,
  getRowId,
  actions,
  page,
  rowsPerPage,
  totalElements,
  onPageChange,
  onRowsPerPageChange,
  emptyText = 'Нет данных для отображения',
}: EntityTableProps<T>) => {
  return (
    <Paper>
      <div className={styles.tableWrap}>
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell key={column.key} align={column.align}>
                  {column.label}
                </TableCell>
              ))}
              {actions ? <TableCell align="right">Действия</TableCell> : null}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.length === 0 ? (
              <TableRow>
                <TableCell colSpan={columns.length + (actions ? 1 : 0)}>
                  <Typography className={styles.empty}>{emptyText}</Typography>
                </TableCell>
              </TableRow>
            ) : (
              rows.map((row) => (
                <TableRow hover key={getRowId(row)}>
                  {columns.map((column) => (
                    <TableCell key={column.key} align={column.align}>
                      {column.render(row)}
                    </TableCell>
                  ))}
                  {actions ? <TableCell align="right">{actions(row)}</TableCell> : null}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        rowsPerPage={rowsPerPage}
        rowsPerPageOptions={[5, 10, 20, 50]}
        labelRowsPerPage="Строк на странице"
        onPageChange={(_event, nextPage) => onPageChange(nextPage)}
        onRowsPerPageChange={(event) => onRowsPerPageChange(Number(event.target.value))}
      />
    </Paper>
  );
};
