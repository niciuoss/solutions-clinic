'use server';

import { apiRequest } from './_helpers';
import type { 
  Room,
  CreateRoomRequest,
  ActionResult,
} from '@/types';

export async function createRoomAction(
  data: CreateRoomRequest
): Promise<ActionResult<Room>> {
  try {
    const room = await apiRequest<Room>('/rooms', {
      method: 'POST',
      body: data,
    });

    return {
      success: true,
      data: room,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao criar sala',
    };
  }
}

export async function getRoomByIdAction(
  roomId: string
): Promise<ActionResult<Room>> {
  try {
    const room = await apiRequest<Room>(`/rooms/${roomId}`, {
      method: 'GET',
    });

    return {
      success: true,
      data: room,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar sala',
    };
  }
}

export async function getAllActiveRoomsAction(): Promise<ActionResult<Room[]>> {
  try {
    const rooms = await apiRequest<Room[]>('/rooms', {
      method: 'GET',
    });

    return {
      success: true,
      data: rooms,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao listar salas',
    };
  }
}

export async function getRoomsByTenantAction(
  tenantId: string,
  activeOnly: boolean = true
): Promise<ActionResult<Room[]>> {
  try {
    if (!tenantId) {
      return {
        success: false,
        error: 'ID da clínica (tenantId) é obrigatório',
      };
    }

    const rooms = await apiRequest<Room[]>('/rooms', {
      method: 'GET',
      params: { tenantId, activeOnly: String(activeOnly) },
    });

    return {
      success: true,
      data: rooms,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao listar salas',
    };
  }
}

export async function deleteRoomAction(roomId: string): Promise<ActionResult<void>> {
  try {
    await apiRequest(`/rooms/${roomId}`, {
      method: 'DELETE',
    });

    return {
      success: true,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao excluir sala',
    };
  }
}