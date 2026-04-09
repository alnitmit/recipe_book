import { Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Stack, TextField } from '@mui/material';
import { useEffect, useState } from 'react';

type Primitive = string | number | null | undefined;

interface SelectOption {
  label: string;
  value: number | string;
}

interface FieldConfig<T extends object> {
  name: keyof T;
  label: string;
  type?: 'text' | 'email' | 'number' | 'multiline' | 'select';
  rows?: number;
  required?: boolean;
  disabled?: boolean;
  options?: SelectOption[];
}

interface EntityDialogProps<T extends object> {
  open: boolean;
  title: string;
  submitLabel?: string;
  initialValues: T;
  fields: Array<FieldConfig<T>>;
  loading?: boolean;
  fieldErrors?: Record<string, string>;
  onSubmit: (values: T) => void;
  onClose: () => void;
}

export function EntityDialog<T extends object>({
  open,
  title,
  submitLabel = 'Сохранить',
  initialValues,
  fields,
  loading = false,
  fieldErrors = {},
  onSubmit,
  onClose,
}: EntityDialogProps<T>) {
  const [values, setValues] = useState(initialValues);

  useEffect(() => {
    if (open) {
      setValues(initialValues);
    }
  }, [initialValues, open]);

  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          {fields.map((field) => {
            const rawValue = values[field.name] as Primitive;
            const value = rawValue ?? '';

            return (
              <TextField
                key={String(field.name)}
                fullWidth
                required={field.required}
                disabled={loading || field.disabled}
                label={field.label}
                type={field.type === 'email' ? 'email' : field.type === 'number' ? 'number' : 'text'}
                multiline={field.type === 'multiline'}
                minRows={field.rows}
                select={field.type === 'select'}
                value={value}
                error={Boolean(fieldErrors[String(field.name)])}
                helperText={fieldErrors[String(field.name)]}
                onChange={(event) => {
                  const nextValue =
                    field.type === 'number'
                      ? event.target.value === ''
                        ? null
                        : Number(event.target.value)
                      : event.target.value;

                  setValues((previous) => ({
                    ...previous,
                    [field.name]: nextValue,
                  }) as T);
                }}
              >
                {field.options?.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </TextField>
            );
          })}
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3 }}>
        <Button disabled={loading} onClick={onClose}>
          Отмена
        </Button>
        <Button disabled={loading} variant="contained" onClick={() => onSubmit(values)}>
          {submitLabel}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
