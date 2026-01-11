'use server';

import { apiRequest } from './_helpers';
import type { 
  User,
  CreateUserRequest,
  UserRole,
  ActionResult,
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