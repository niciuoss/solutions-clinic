'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllPatientsAction,
  getPatientByIdAction,
  createPatientAction,
  updatePatientAction,
  deletePatientAction,
  searchPatientsAction,
  autocompletePatientsAction,
} from '@/actions/patient-actions';
import type { CreatePatientRequest, UpdatePatientRequest } from '@/types';
import { toast } from 'sonner';

export function usePatients(tenantId: string | null, page: number = 0, size: number = 20) {
  const queryClient = useQueryClient();

  // Listar todos os pacientes
  const { data: result, isLoading, error, refetch } = useQuery({
    queryKey: ['patients', tenantId, page, size],
    queryFn: async () => {
      if (!tenantId) {
        return { success: false, data: { content: [], totalElements: 0, totalPages: 0, size, number: page } };
      }
      return await getAllPatientsAction(tenantId, page, size);
    },
    enabled: !!tenantId,
  });

  const patients = result?.success ? result.data : null;

  // Criar paciente
  const createMutation = useMutation({
    mutationFn: (data: CreatePatientRequest) => createPatientAction(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
      toast.success('Paciente cadastrado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao cadastrar paciente');
    },
  });

  // Atualizar paciente
  const updateMutation = useMutation({
    mutationFn: ({ patientId, data }: { patientId: string; data: UpdatePatientRequest }) =>
      updatePatientAction(patientId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
      toast.success('Paciente atualizado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao atualizar paciente');
    },
  });

  // Excluir paciente
  const deleteMutation = useMutation({
    mutationFn: (patientId: string) => deletePatientAction(patientId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
      toast.success('Paciente excluído com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao excluir paciente');
    },
  });

  return {
    patients,
    isLoading,
    error,
    refetch,
    createPatient: createMutation.mutateAsync,
    updatePatient: updateMutation.mutateAsync,
    deletePatient: deleteMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
}

// Buscar um paciente específico
export function usePatient(patientId: string | null) {
  return useQuery({
    queryKey: ['patient', patientId],
    queryFn: () => getPatientByIdAction(patientId!),
    enabled: !!patientId,
  });
}

// Busca de pacientes
export function usePatientSearch(query: string, page: number = 0, size: number = 20) {
  const { data: result, isLoading, error } = useQuery({
    queryKey: ['patients', 'search', query, page, size],
    queryFn: async () => {
      if (query.length < 2) {
        return { success: false, data: { content: [], totalElements: 0, totalPages: 0, size, number: page } };
      }
      return await searchPatientsAction(query, page, size);
    },
    enabled: query.length >= 2,
  });

  return {
    data: result?.success ? result.data : null,
    isLoading,
    error,
  };
}

// Autocomplete de pacientes
export function usePatientAutocomplete(name: string) {
  return useQuery({
    queryKey: ['patients', 'autocomplete', name],
    queryFn: () => autocompletePatientsAction(name),
    enabled: name.length >= 2,
  });
}