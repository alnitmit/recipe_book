import { Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Stack, TextField } from '@mui/material';
import { useState } from 'react';

type Primitive = string | number | null | undefined;

type SelectOption = {
  label: string;
  value: number | string;
};

export type FieldConfig<T extends object> = {
  name: keyof T;
  label: string;
  type?: 'text' | 'email' | 'number' | 'multiline' | 'select';
  rows?: number;
  required?: boolean;
  disabled?: boolean;
  options?: SelectOption[];
};

type EntityDialogProps<T extends object> = {
  open: boolean;
  title: string;
  submitLabel?: string;
  resetKey?: string | number;
  initialValues: T;
  fields: Array<FieldConfig<T>>;
  loading?: boolean;
  fieldErrors?: Record<string, string>;
  onSubmit: (values: T) => void;
  onClose: () => void;
};

type EntityDialogFormProps<T extends object> = Omit<EntityDialogProps<T>, 'open' | 'title' | 'resetKey'> & {
  submitLabel: string;
};

const EntityDialogForm = <T extends object,>({
  initialValues,
  fields,
  loading = false,
  fieldErrors = {},
  onSubmit,
  onClose,
  submitLabel,
}: EntityDialogFormProps<T>) => {
  const [values, setValues] = useState(initialValues);

  return (
    <>
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
        <Button disabled={loading} onClick={() => onSubmit(values)} variant="contained">
          {submitLabel}
        </Button>
      </DialogActions>
    </>
  );
};

export const EntityDialog = <T extends object,>({
  open,
  title,
  submitLabel = 'Сохранить',
  resetKey,
  initialValues,
  fields,
  loading = false,
  fieldErrors = {},
  onSubmit,
  onClose,
}: EntityDialogProps<T>) => {
  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
      {open ? (
        <EntityDialogForm
          key={resetKey}
          initialValues={initialValues}
          fields={fields}
          loading={loading}
          fieldErrors={fieldErrors}
          onSubmit={onSubmit}
          onClose={onClose}
          submitLabel={submitLabel}
        />
      ) : null}
    </Dialog>
  );
};
