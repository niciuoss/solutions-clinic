'use client';

import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from 'sonner';
import { Loader2, Clock, Trash2, Plus, Save } from 'lucide-react';
import {
  ProfessionalSchedule,
  DayOfWeek,
  DAY_OF_WEEK_LABELS,
  DAY_OF_WEEK_ORDER,
  CreateProfessionalScheduleRequest,
  UpdateProfessionalScheduleRequest,
} from '@/types/professional-schedule.types';
import {
  createProfessionalScheduleAction,
  updateProfessionalScheduleAction,
  deleteProfessionalScheduleAction,
  getProfessionalSchedulesByProfessionalIdAction,
} from '@/actions/professional-schedule-actions';
import { useQueryClient } from '@tanstack/react-query';
import { Checkbox } from '@/components/ui/checkbox';

const scheduleSchema = z.object({
  startTime: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/, 'Formato inválido (HH:mm)'),
  endTime: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/, 'Formato inválido (HH:mm)'),
  lunchBreakStart: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/, 'Formato inválido (HH:mm)'),
  lunchBreakEnd: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/, 'Formato inválido (HH:mm)'),
  slotDurationMinutes: z.number().min(15).max(120),
});

type ScheduleFormData = z.infer<typeof scheduleSchema>;

interface DayScheduleFormProps {
  dayOfWeek: DayOfWeek;
  professionalId: string;
  existingSchedule?: ProfessionalSchedule;
  onSave: () => void;
}

function DayScheduleForm({ dayOfWeek, professionalId, existingSchedule, onSave }: DayScheduleFormProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const queryClient = useQueryClient();

  const form = useForm<ScheduleFormData>({
    resolver: zodResolver(scheduleSchema),
    defaultValues: existingSchedule
      ? {
          startTime: existingSchedule.startTime,
          endTime: existingSchedule.endTime,
          lunchBreakStart: existingSchedule.lunchBreakStart,
          lunchBreakEnd: existingSchedule.lunchBreakEnd,
          slotDurationMinutes: existingSchedule.slotDurationMinutes,
        }
      : {
          startTime: '08:00',
          endTime: '18:00',
          lunchBreakStart: '12:00',
          lunchBreakEnd: '13:00',
          slotDurationMinutes: 30,
        },
  });

  const onSubmit = async (data: ScheduleFormData) => {
    setIsSubmitting(true);
    try {
      let result;
      if (existingSchedule) {
        const updateData: UpdateProfessionalScheduleRequest = {
          id: existingSchedule.id,
          ...data,
        };
        result = await updateProfessionalScheduleAction(updateData);
      } else {
        const createData: CreateProfessionalScheduleRequest = {
          professionalId,
          dayOfWeek,
          ...data,
        };
        result = await createProfessionalScheduleAction(createData);
      }

      if (result.success) {
        toast.success(`Horário de ${DAY_OF_WEEK_LABELS[dayOfWeek]} ${existingSchedule ? 'atualizado' : 'criado'} com sucesso!`);
        queryClient.invalidateQueries({ queryKey: ['professional-schedules', professionalId] });
        onSave();
      } else {
        toast.error(result.error || 'Erro ao salvar horário');
      }
    } catch (error) {
      toast.error('Erro inesperado ao salvar horário');
      console.error(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!existingSchedule) return;

    if (!confirm(`Deseja realmente remover o horário de ${DAY_OF_WEEK_LABELS[dayOfWeek]}?`)) {
      return;
    }

    setIsDeleting(true);
    try {
      const result = await deleteProfessionalScheduleAction(existingSchedule.id);
      if (result.success) {
        toast.success(`Horário de ${DAY_OF_WEEK_LABELS[dayOfWeek]} removido com sucesso!`);
        queryClient.invalidateQueries({ queryKey: ['professional-schedules', professionalId] });
        onSave();
      } else {
        toast.error(result.error || 'Erro ao remover horário');
      }
    } catch (error) {
      toast.error('Erro inesperado ao remover horário');
      console.error(error);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div>
          <Label htmlFor={`startTime-${dayOfWeek}`}>Início *</Label>
          <Input
            id={`startTime-${dayOfWeek}`}
            type="time"
            {...form.register('startTime')}
            className={form.formState.errors.startTime ? 'border-red-500' : ''}
          />
          {form.formState.errors.startTime && (
            <p className="mt-1 text-xs text-red-500">{form.formState.errors.startTime.message}</p>
          )}
        </div>

        <div>
          <Label htmlFor={`endTime-${dayOfWeek}`}>Fim *</Label>
          <Input
            id={`endTime-${dayOfWeek}`}
            type="time"
            {...form.register('endTime')}
            className={form.formState.errors.endTime ? 'border-red-500' : ''}
          />
          {form.formState.errors.endTime && (
            <p className="mt-1 text-xs text-red-500">{form.formState.errors.endTime.message}</p>
          )}
        </div>

        <div>
          <Label htmlFor={`lunchBreakStart-${dayOfWeek}`}>Início Almoço *</Label>
          <Input
            id={`lunchBreakStart-${dayOfWeek}`}
            type="time"
            {...form.register('lunchBreakStart')}
            className={form.formState.errors.lunchBreakStart ? 'border-red-500' : ''}
          />
          {form.formState.errors.lunchBreakStart && (
            <p className="mt-1 text-xs text-red-500">{form.formState.errors.lunchBreakStart.message}</p>
          )}
        </div>

        <div>
          <Label htmlFor={`lunchBreakEnd-${dayOfWeek}`}>Fim Almoço *</Label>
          <Input
            id={`lunchBreakEnd-${dayOfWeek}`}
            type="time"
            {...form.register('lunchBreakEnd')}
            className={form.formState.errors.lunchBreakEnd ? 'border-red-500' : ''}
          />
          {form.formState.errors.lunchBreakEnd && (
            <p className="mt-1 text-xs text-red-500">{form.formState.errors.lunchBreakEnd.message}</p>
          )}
        </div>
      </div>

      <div className="flex items-end gap-4">
        <div className="flex-1">
          <Label htmlFor={`slotDuration-${dayOfWeek}`}>Duração do Slot (minutos) *</Label>
          <Select
            value={form.watch('slotDurationMinutes').toString()}
            onValueChange={(value) => form.setValue('slotDurationMinutes', parseInt(value))}
          >
            <SelectTrigger className={form.formState.errors.slotDurationMinutes ? 'border-red-500' : ''}>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="15">15 minutos</SelectItem>
              <SelectItem value="30">30 minutos</SelectItem>
              <SelectItem value="45">45 minutos</SelectItem>
              <SelectItem value="60">60 minutos</SelectItem>
              <SelectItem value="90">90 minutos</SelectItem>
              <SelectItem value="120">120 minutos</SelectItem>
            </SelectContent>
          </Select>
          {form.formState.errors.slotDurationMinutes && (
            <p className="mt-1 text-xs text-red-500">{form.formState.errors.slotDurationMinutes.message}</p>
          )}
        </div>

        <div className="flex gap-2">
          {existingSchedule && (
            <Button
              type="button"
              variant="destructive"
              size="sm"
              onClick={handleDelete}
              disabled={isDeleting || isSubmitting}
            >
              {isDeleting ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Trash2 className="h-4 w-4" />
              )}
            </Button>
          )}
          <Button type="submit" size="sm" disabled={isSubmitting || isDeleting}>
            {isSubmitting ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <Save className="h-4 w-4" />
            )}
            {existingSchedule ? 'Atualizar' : 'Salvar'}
          </Button>
        </div>
      </div>
    </form>
  );
}

interface ProfessionalScheduleFormProps {
  professionalId: string;
  professionalName: string;
}

export function ProfessionalScheduleForm({ professionalId, professionalName }: ProfessionalScheduleFormProps) {
  const [schedules, setSchedules] = useState<ProfessionalSchedule[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [expandedDays, setExpandedDays] = useState<Set<DayOfWeek>>(new Set());

  const loadSchedules = async () => {
    setIsLoading(true);
    try {
      const result = await getProfessionalSchedulesByProfessionalIdAction(professionalId);
      if (result.success && result.data) {
        setSchedules(result.data);
        // Expandir dias que já têm horário configurado e manter os que já estavam expandidos
        const daysWithSchedule = new Set(result.data.map((s) => s.dayOfWeek));
        setExpandedDays((prev) => {
          const newSet = new Set(prev);
          daysWithSchedule.forEach((day) => newSet.add(day));
          return newSet;
        });
      }
    } catch (error) {
      console.error('Erro ao carregar horários:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadSchedules();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [professionalId]);

  const getScheduleForDay = (dayOfWeek: DayOfWeek): ProfessionalSchedule | undefined => {
    return schedules.find((s) => s.dayOfWeek === dayOfWeek);
  };

  const toggleDay = (dayOfWeek: DayOfWeek) => {
    const newExpanded = new Set(expandedDays);
    if (newExpanded.has(dayOfWeek)) {
      newExpanded.delete(dayOfWeek);
    } else {
      newExpanded.add(dayOfWeek);
    }
    setExpandedDays(newExpanded);
  };

  if (isLoading) {
    return (
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5" />
            Configuração de Horários
          </CardTitle>
          <CardDescription>
            Configure os horários de trabalho para {professionalName}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {DAY_OF_WEEK_ORDER.map((dayOfWeek) => {
            const schedule = getScheduleForDay(dayOfWeek);
            const isExpanded = expandedDays.has(dayOfWeek);

            return (
              <Card key={dayOfWeek} className="border-l-4 border-l-primary/50">
                <CardHeader className="pb-3">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <Checkbox
                        checked={isExpanded}
                        onCheckedChange={() => toggleDay(dayOfWeek)}
                      />
                      <CardTitle className="text-base font-medium">
                        {DAY_OF_WEEK_LABELS[dayOfWeek]}
                      </CardTitle>
                      {schedule && (
                        <span className="text-sm text-muted-foreground">
                          ({schedule.startTime} - {schedule.endTime})
                        </span>
                      )}
                    </div>
                    {!isExpanded && !schedule && (
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={() => toggleDay(dayOfWeek)}
                      >
                        <Plus className="h-4 w-4 mr-2" />
                        Adicionar Horário
                      </Button>
                    )}
                  </div>
                </CardHeader>
                {isExpanded && (
                  <CardContent>
                    <DayScheduleForm
                      dayOfWeek={dayOfWeek}
                      professionalId={professionalId}
                      existingSchedule={schedule}
                      onSave={loadSchedules}
                    />
                  </CardContent>
                )}
              </Card>
            );
          })}
        </CardContent>
      </Card>
    </div>
  );
}
