'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getTodayAppointmentsAction,
  getAppointmentByIdAction,
  getAppointmentsByDateRangeAction,
  createAppointmentAction,
  updateAppointmentAction,
  startAppointmentAction,
  finishAppointmentAction,
  cancelAppointmentAction,
} from '@/actions/appointment-actions';
import type { 
  CreateAppointmentRequest, 
  UpdateAppointmentRequest,
  FinishAppointmentRequest 
} from '@/types';
import { toast } from 'sonner';

export function useAppointments() {
  const queryClient = useQueryClient();

  // Agendamentos de hoje
  const { data: todayAppointments, isLoading, refetch } = useQuery({
    queryKey: ['appointments', 'today'],
    queryFn: () => getTodayAppointmentsAction(),
    refetchInterval: 30000, // Atualiza a cada 30 segundos
  });

  // Criar agendamento
  const createMutation = useMutation({
    mutationFn: (data: CreateAppointmentRequest) => createAppointmentAction(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      toast.success('Agendamento criado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao criar agendamento');
    },
  });

  // Atualizar agendamento
  const updateMutation = useMutation({
    mutationFn: ({ appointmentId, data }: { appointmentId: string; data: UpdateAppointmentRequest }) =>
      updateAppointmentAction(appointmentId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      toast.success('Agendamento atualizado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao atualizar agendamento');
    },
  });

  // Iniciar atendimento
  const startMutation = useMutation({
    mutationFn: (appointmentId: string) => startAppointmentAction(appointmentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      toast.success('Atendimento iniciado!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao iniciar atendimento');
    },
  });

  // Finalizar atendimento
  const finishMutation = useMutation({
    mutationFn: ({ appointmentId, data }: { appointmentId: string; data: FinishAppointmentRequest }) =>
      finishAppointmentAction(appointmentId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      toast.success('Atendimento finalizado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao finalizar atendimento');
    },
  });

  // Cancelar agendamento
  const cancelMutation = useMutation({
    mutationFn: ({ appointmentId, reason }: { appointmentId: string; reason?: string }) =>
      cancelAppointmentAction(appointmentId, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      toast.success('Agendamento cancelado!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao cancelar agendamento');
    },
  });

  return {
    todayAppointments,
    isLoading,
    refetch,
    createAppointment: createMutation.mutateAsync,
    updateAppointment: updateMutation.mutateAsync,
    startAppointment: startMutation.mutateAsync,
    finishAppointment: finishMutation.mutateAsync,
    cancelAppointment: cancelMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isStarting: startMutation.isPending,
    isFinishing: finishMutation.isPending,
    isCancelling: cancelMutation.isPending,
  };
}

// Buscar agendamento por Profissional
  export function useAppointmentsByProfessional(
    professionalId: string,
    startDate: string,
    endDate: string
  ) {
    return useQuery({
      queryKey: ['appointments', 'professional', professionalId, startDate, endDate],
      queryFn: async () => {
        const result = await getAppointmentsByProfessionalAction(
          professionalId,
          startDate,
          endDate
        );
        return result.success ? result.data : [];
      },
      enabled: !!professionalId && !!startDate && !!endDate,
    });
  }

  // Buscar agendamentos por período
  export function useAppointmentsByDateRange(startDate: string, endDate: string) {
    return useQuery({
      queryKey: ['appointments', 'range', startDate, endDate],
      queryFn: async () => {
        const result = await getAppointmentsByDateRangeAction(startDate, endDate);
        return result.success ? result.data : [];
      },
      enabled: !!startDate && !!endDate,
    });
  }

// Agendamento específico
export function useAppointment(appointmentId: string | null) {
  return useQuery({
    queryKey: ['appointment', appointmentId],
    queryFn: () => getAppointmentByIdAction(appointmentId!),
    enabled: !!appointmentId,
  });
}

// Agendamentos por período
// export function useAppointmentsByDateRange(startDate: string, endDate: string) {
//   return useQuery({
//     queryKey: ['appointments', 'range', startDate, endDate],
//     queryFn: () => getAppointmentsByDateRangeAction(startDate, endDate),
//     enabled: !!startDate && !!endDate,
//   });
// }