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

  const { data: result, isLoading, error, refetch } = useQuery({
    queryKey: ['professionals'],
    queryFn: () => getAllActiveProfessionalsAction(),
  });

  const professionals = result?.success ? result.data : [];

  const createMutation = useMutation({
    mutationFn: (data: CreateProfessionalRequest) => createProfessionalAction(data),
    onSuccess: (result) => {
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ['professionals'] });
        toast.success('Perfil profissional criado com sucesso!');
      } else {
        toast.error(result.error || 'Erro ao criar perfil profissional');
      }
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

export function useProfessional(professionalId: string | null) {
  return useQuery({
    queryKey: ['professional', professionalId],
    queryFn: async () => {
      if (!professionalId) return null;
      const result = await getProfessionalByIdAction(professionalId);
      return result.success ? result.data : null;
    },
    enabled: !!professionalId,
  });
}