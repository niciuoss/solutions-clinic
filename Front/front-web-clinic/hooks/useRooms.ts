'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllActiveRoomsAction,
  getRoomByIdAction,
  createRoomAction,
  updateRoomAction,
  deleteRoomAction,
} from '@/actions/room-actions';
import type { CreateRoomRequest, UpdateRoomRequest } from '@/types';
import { toast } from 'sonner';

export function useRooms() {
  const queryClient = useQueryClient();

  const { data: result, isLoading, error, refetch } = useQuery({
    queryKey: ['rooms'],
    queryFn: () => getAllActiveRoomsAction(),
  });

  const rooms = result?.success ? result.data : [];

  const createMutation = useMutation({
    mutationFn: (data: CreateRoomRequest) => createRoomAction(data),
    onSuccess: (result) => {
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ['rooms'] });
        toast.success('Sala criada com sucesso!');
      } else {
        toast.error(result.error || 'Erro ao criar sala');
      }
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao criar sala');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ roomId, data }: { roomId: string; data: UpdateRoomRequest }) =>
      updateRoomAction(roomId, data),
    onSuccess: (result) => {
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ['rooms'] });
        toast.success('Sala atualizada com sucesso!');
      } else {
        toast.error(result.error || 'Erro ao atualizar sala');
      }
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao atualizar sala');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (roomId: string) => deleteRoomAction(roomId),
    onSuccess: (result) => {
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ['rooms'] });
        toast.success('Sala excluída com sucesso!');
      } else {
        toast.error(result.error || 'Erro ao excluir sala');
      }
    },
    onError: (error: any) => {
      toast.error(error.message || 'Erro ao excluir sala');
    },
  });

  return {
    rooms,
    isLoading,
    error,
    refetch,
    createRoom: createMutation.mutateAsync,
    updateRoom: updateMutation.mutateAsync,
    deleteRoom: deleteMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
}

export function useRoom(roomId: string | null) {
  return useQuery({
    queryKey: ['room', roomId],
    queryFn: async () => {
      if (!roomId) return null;
      const result = await getRoomByIdAction(roomId);
      return result.success ? result.data : null;
    },
    enabled: !!roomId,
  });
}