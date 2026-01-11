'use client'

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { useAppointments } from '@/hooks/useAppointments';
import { useAvailability } from '@/hooks/useAvailability';
import { useProfessionals } from '@/hooks/useProfessionals';
import { ConflictDialog } from './ConflictDialog';
import { PatientAutocomplete } from './PatientAutocomplete';
import { RoomSelect } from './RoomSelect';
import { 
  CalendarIcon, 
  Clock, 
  Loader2, 
  Plus, 
  Trash2,
  AlertCircle,
  CheckCircle,
} from 'lucide-react';
import { format, parse } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { cn } from '@/lib/utils';
import type { CreateAppointmentRequest } from '@/types';

const appointmentSchema = z.object({
  patientId: z.string().min(1, 'Selecione um paciente'),
  professionalId: z.string().min(1, 'Selecione um profissional'),
  roomId: z.string().optional(),
  date: z.date({ required_error: 'Selecione uma data' }),
  time: z.string().min(1, 'Selecione um horário'),
  durationMinutes: z.number().min(15, 'Duração mínima de 15 minutos').optional(),
  observations: z.string().optional(),
  procedures: z.array(
    z.object({
      name: z.string().min(1, 'Nome do procedimento é obrigatório'),
      description: z.string().optional(),
      value: z.number().optional(),
      quantity: z.number().min(1).optional(),
    })
  ).optional(),
});

type AppointmentFormData = z.infer<typeof appointmentSchema>;

export function NewAppointmentForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { professionals, isLoading: loadingProfessionals } = useProfessionals();
  const { 
    createAppointment, 
    confirmConflict, 
    cancelConflict, 
    pendingConflict,
    isCreating 
  } = useAppointments();
  const { checkAvailability, isChecking } = useAvailability();

  const [availabilityStatus, setAvailabilityStatus] = useState<{
    isAvailable: boolean;
    message: string;
  } | null>(null);

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<AppointmentFormData>({
    resolver: zodResolver(appointmentSchema),
    defaultValues: {
      durationMinutes: 60,
      procedures: [],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'procedures',
  });

  // Observar campos para verificação de disponibilidade
  const professionalId = watch('professionalId');
  const date = watch('date');
  const time = watch('time');
  const durationMinutes = watch('durationMinutes') || 60;

  // Pré-preencher data se vier da URL
  useEffect(() => {
    const dateParam = searchParams.get('date');
    if (dateParam) {
      try {
        const parsedDate = parse(dateParam, 'yyyy-MM-dd', new Date());
        setValue('date', parsedDate);
      } catch (error) {
        console.error('Erro ao parsear data da URL:', error);
      }
    }
  }, [searchParams, setValue]);

  // Verificar disponibilidade em tempo real
  useEffect(() => {
    if (professionalId && date && time) {
      const checkTimeout = setTimeout(async () => {
        const scheduledAt = `${format(date, 'yyyy-MM-dd')}T${time}:00`;
        
        const result = await checkAvailability(
          professionalId,
          scheduledAt,
          durationMinutes
        );

        if (result.success) {
          setAvailabilityStatus({
            isAvailable: result.data || false,
            message: result.data
              ? '✓ Horário disponível'
              : '✗ Profissional já possui agendamento neste horário',
          });
        }
      }, 500);

      return () => clearTimeout(checkTimeout);
    } else {
      setAvailabilityStatus(null);
    }
  }, [professionalId, date, time, durationMinutes, checkAvailability]);

  const onSubmit = async (data: AppointmentFormData) => {
    try {
      const scheduledAt = `${format(data.date, 'yyyy-MM-dd')}T${data.time}:00`;

      const requestData: CreateAppointmentRequest = {
        patientId: data.patientId,
        professionalId: data.professionalId,
        roomId: data.roomId,
        scheduledAt,
        durationMinutes: data.durationMinutes,
        observations: data.observations,
        procedures: data.procedures,
      };

      await createAppointment(requestData);
      router.push('/appointments');
    } catch (error) {
      // Erro tratado no hook
    }
  };

  // Gerar opções de horário (7h às 20h, intervalo de 15 minutos)
  const timeSlots = [];
  for (let hour = 7; hour < 20; hour++) {
    for (let minute = 0; minute < 60; minute += 15) {
      const timeStr = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
      timeSlots.push(timeStr);
    }
  }

  return (
    <>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Informações Básicas */}
        <Card>
          <CardHeader>
            <CardTitle>Informações do Agendamento</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Paciente */}
            <div className="space-y-2">
              <Label>Paciente *</Label>
              <PatientAutocomplete
                onSelect={(patient) => setValue('patientId', patient.id)}
                error={errors.patientId?.message}
              />
            </div>

            {/* Profissional */}
            <div className="space-y-2">
              <Label htmlFor="professionalId">Profissional *</Label>
              <Select
                value={watch('professionalId')}
                onValueChange={(value) => setValue('professionalId', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o profissional" />
                </SelectTrigger>
                <SelectContent>
                  {loadingProfessionals ? (
                    <div className="p-2 text-sm text-muted-foreground">
                      Carregando...
                    </div>
                  ) : professionals && professionals.length > 0 ? (
                    professionals.map((professional) => (
                      <SelectItem key={professional.id} value={professional.id}>
                        {professional.user.fullName} - {professional.specialty}
                      </SelectItem>
                    ))
                  ) : (
                    <div className="p-2 text-sm text-muted-foreground">
                      Nenhum profissional cadastrado
                    </div>
                  )}
                </SelectContent>
              </Select>
              {errors.professionalId && (
                <p className="text-sm text-red-500">{errors.professionalId.message}</p>
              )}
            </div>

            {/* Sala */}
            <div className="space-y-2">
              <Label htmlFor="roomId">Sala (Opcional)</Label>
              <RoomSelect
                value={watch('roomId')}
                onValueChange={(value) => setValue('roomId', value)}
              />
            </div>
          </CardContent>
        </Card>

        {/* Data e Horário */}
        <Card>
          <CardHeader>
            <CardTitle>Data e Horário</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid gap-4 md:grid-cols-3">
              {/* Data */}
              <div className="space-y-2">
                <Label>Data *</Label>
                <Popover>
                  <PopoverTrigger asChild>
                    <Button
                      variant="outline"
                      className={cn(
                        'w-full justify-start text-left font-normal',
                        !date && 'text-muted-foreground'
                      )}
                    >
                      <CalendarIcon className="mr-2 h-4 w-4" />
                      {date ? format(date, 'PPP', { locale: ptBR }) : 'Selecione a data'}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0" align="start">
                    <Calendar
                      mode="single"
                      selected={date}
                      onSelect={(date) => date && setValue('date', date)}
                      locale={ptBR}
                      initialFocus
                    />
                  </PopoverContent>
                </Popover>
                {errors.date && (
                  <p className="text-sm text-red-500">{errors.date.message}</p>
                )}
              </div>

              {/* Horário */}
              <div className="space-y-2">
                <Label htmlFor="time">Horário *</Label>
                <Select
                  value={watch('time')}
                  onValueChange={(value) => setValue('time', value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o horário" />
                  </SelectTrigger>
                  <SelectContent className="max-h-[300px]">
                    {timeSlots.map((slot) => (
                      <SelectItem key={slot} value={slot}>
                        {slot}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {errors.time && (
                  <p className="text-sm text-red-500">{errors.time.message}</p>
                )}
              </div>

              {/* Duração */}
              <div className="space-y-2">
                <Label htmlFor="durationMinutes">Duração (minutos)</Label>
                <Input
                  type="number"
                  {...register('durationMinutes', { valueAsNumber: true })}
                  placeholder="60"
                  min="15"
                  step="15"
                />
                <p className="text-xs text-muted-foreground">
                  Padrão: 60 minutos
                </p>
              </div>
            </div>

            {/* Status de disponibilidade */}
            {isChecking && (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Loader2 className="h-4 w-4 animate-spin" />
                Verificando disponibilidade...
              </div>
            )}

            {availabilityStatus && !isChecking && (
              <div
                className={cn(
                  'flex items-center gap-2 rounded-lg border p-3',
                  availabilityStatus.isAvailable
                    ? 'border-green-200 bg-green-50 text-green-700'
                    : 'border-amber-200 bg-amber-50 text-amber-700'
                )}
              >
                {availabilityStatus.isAvailable ? (
                  <CheckCircle className="h-4 w-4" />
                ) : (
                  <AlertCircle className="h-4 w-4" />
                )}
                <span className="text-sm font-medium">{availabilityStatus.message}</span>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Procedimentos */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Procedimentos (Opcional)</CardTitle>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() =>
                append({ name: '', description: '', value: 0, quantity: 1 })
              }
            >
              <Plus className="mr-2 h-4 w-4" />
              Adicionar Procedimento
            </Button>
          </CardHeader>
          <CardContent className="space-y-4">
            {fields.length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                Nenhum procedimento adicionado
              </p>
            ) : (
              fields.map((field, index) => (
                <div
                  key={field.id}
                  className="grid gap-4 md:grid-cols-[2fr,1fr,1fr,auto] p-4 border rounded-lg"
                >
                  <div className="space-y-2">
                    <Label>Nome do Procedimento</Label>
                    <Input
                      {...register(`procedures.${index}.name`)}
                      placeholder="Ex: Consulta, Exame, Tratamento"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label>Valor (R$)</Label>
                    <Input
                      type="number"
                      {...register(`procedures.${index}.value`, {
                        valueAsNumber: true,
                      })}
                      placeholder="0.00"
                      step="0.01"
                      min="0"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label>Quantidade</Label>
                    <Input
                      type="number"
                      {...register(`procedures.${index}.quantity`, {
                        valueAsNumber: true,
                      })}
                      placeholder="1"
                      min="1"
                    />
                  </div>

                  <div className="flex items-end">
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      onClick={() => remove(index)}
                    >
                      <Trash2 className="h-4 w-4 text-red-500" />
                    </Button>
                  </div>
                </div>
              ))
            )}
          </CardContent>
        </Card>

        {/* Observações */}
        <Card>
          <CardHeader>
            <CardTitle>Observações (Opcional)</CardTitle>
          </CardHeader>
          <CardContent>
            <Textarea
              {...register('observations')}
              placeholder="Informações adicionais sobre o agendamento..."
              rows={4}
            />
          </CardContent>
        </Card>

        {/* Ações */}
        <div className="flex items-center justify-end gap-4">
          <Button
            type="button"
            variant="outline"
            onClick={() => router.back()}
          >
            Cancelar
          </Button>
          <Button type="submit" disabled={isCreating}>
            {isCreating && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            Criar Agendamento
          </Button>
        </div>
      </form>

      {/* Dialog de conflito */}
      <ConflictDialog
        open={!!pendingConflict}
        message={pendingConflict?.error || ''}
        onConfirm={confirmConflict}
        onCancel={cancelConflict}
        isLoading={isCreating}
      />
    </>
  );
}