'use server';

import { apiRequest } from './_helpers';
import type { 
  User,
  CreateUserRequest,
  UserRole,
  ActionResult,
  PaginatedResponse,
  UserListItem,
  UpdateUserBodyRequest,
  UpdateUserBlockedBodyRequest,
} from '@/types';

export async function createUserAction(
  data: CreateUserRequest
): Promise<ActionResult<User>> {
  try {
    const user = await apiRequest<User>('/users', {
      method: 'POST',
      body: data,
    });

    return {
      success: true,
      data: user,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao criar usuário',
    };
  }
}

export async function getUsersByRoleAction(
  role: UserRole
): Promise<ActionResult<User[]>> {
  try {
    const users = await apiRequest<User[]>(`/users/role/${role}`, {
      method: 'GET',
    });

    return {
      success: true,
      data: users,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar usuários',
    };
  }
}

export async function getProfessionalsAction(): Promise<ActionResult<User[]>> {
  try {
    const users = await apiRequest<User[]>('/users/professionals', {
      method: 'GET',
    });

    return {
      success: true,
      data: users,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar profissionais',
    };
  }
}

export async function getReceptionistsAction(): Promise<ActionResult<User[]>> {
  try {
    const users = await apiRequest<User[]>('/users/receptionists', {
      method: 'GET',
    });

    return {
      success: true,
      data: users,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar recepcionistas',
    };
  }
}

export async function getUsersByTenantAction(
  tenantId: string,
  page: number = 0,
  size: number = 20,
  sort: string = 'firstName,asc',
  search?: string,
  blocked?: boolean
): Promise<ActionResult<PaginatedResponse<UserListItem>>> {
  try {
    if (!tenantId) {
      return {
        success: false,
        error: 'ID da clínica é obrigatório',
      };
    }

    // Preparar parâmetros
    const params: Record<string, string | number | boolean> = {
      tenantId,
      page,
      size,
      sort,
    };

    if (search) params.search = search;
    if (blocked !== undefined) params.blocked = blocked;

    // Buscar usuários paginados do backend
    const response = await apiRequest<PaginatedResponse<UserListItem>>(
      '/users',
      {
        method: 'GET',
        params,
      }
    );

    return {
      success: true,
      data: response,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar usuários',
    };
  }
}

export async function updateUserAction(
  userId: string,
  data: UpdateUserBodyRequest
): Promise<ActionResult<UserListItem>> {
  try {
    const user = await apiRequest<UserListItem>(`/users/${userId}`, {
      method: 'PUT',
      body: data,
    });

    return {
      success: true,
      data: user,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao atualizar usuário',
    };
  }
}

export async function deleteUserAction(
  userId: string
): Promise<ActionResult<void>> {
  try {
    await apiRequest(`/users/${userId}`, {
      method: 'DELETE',
    });

    return {
      success: true,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao deletar usuário',
    };
  }
}

export async function updateUserBlockedAction(
  userId: string,
  blocked: boolean
): Promise<ActionResult<UserListItem>> {
  try {
    const user = await apiRequest<UserListItem>(`/users/${userId}/blocked`, {
      method: 'PATCH',
      body: { blocked },
    });

    return {
      success: true,
      data: user,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao atualizar status de bloqueio',
    };
  }
}