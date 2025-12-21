'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  Room,
  CreateRoomRequest,
  ApiResponse 
} from '@/types';

export async function createRoomAction(data: CreateRoomRequest): Promise<Room> {
  try {
    const response = await api.post<ApiResponse<Room>>(
      API_ROUTES.ROOMS,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao criar sala');
  }
}

export async function getRoomByIdAction(roomId: string): Promise<Room> {
  try {
    const response = await api.get<ApiResponse<Room>>(
      `${API_ROUTES.ROOMS}/${roomId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar sala');
  }
}

export async function getAllActiveRoomsAction(): Promise<Room[]> {
  try {
    const response = await api.get<ApiResponse<Room[]>>(
      API_ROUTES.ROOMS
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao listar salas');
  }
}

export async function deleteRoomAction(roomId: string): Promise<void> {
  try {
    await api.delete<ApiResponse<void>>(
      `${API_ROUTES.ROOMS}/${roomId}`
    );
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao excluir sala');
  }
}