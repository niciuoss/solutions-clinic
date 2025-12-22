'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllActiveProfessionalsAction,
  getProfessionalByIdAction,
  createProfessionalAction,
} from '@/actions/professional-actions';
import type { CreateProfessionalRequest } from '@/types';
import { toast } from 'sonner';

export function useProfessionals() {
  const queryClient = useQueryClient();

  // Listar todos os profissionais ativos
  const { data: professionals, isLoading, error, refetch } = useQuery({
    queryKey: ['professionals'],
    queryFn: () => getAllActiveProfessionalsAction(),
  });

  // Criar perfil profissional
  const createMutation = useMutation({
    mutationFn: (data: CreateProfessionalRequest) => createProfessionalAction(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['professionals'] });
      toast.success('Perfil profissional criado com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao criar perfil profissional');
    },
  });

  return {
    professionals,
    isLoading,
    error,
    refetch,
    createProfessional: createMutation.mutateAsync,
    isCreating: createMutation.isPending,
  };
}

// Hook para profissional específico
export function useProfessional(professionalId: string | null) {
  return useQuery({
    queryKey: ['professional', professionalId],
    queryFn: () => getProfessionalByIdAction(professionalId!),
    enabled: !!professionalId,
  });
}