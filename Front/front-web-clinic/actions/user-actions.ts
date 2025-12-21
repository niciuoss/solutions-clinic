'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  User,
  CreateUserRequest,
  UserRole,
  ApiResponse 
} from '@/types';

export async function createUserAction(data: CreateUserRequest): Promise<User> {
  try {
    const response = await api.post<ApiResponse<User>>(
      API_ROUTES.USERS,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao criar usuário');
  }
}

export async function getUsersByRoleAction(role: UserRole): Promise<User[]> {
  try {
    const response = await api.get<ApiResponse<User[]>>(
      `${API_ROUTES.USERS}/role/${role}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar usuários');
  }
}

export async function getProfessionalsAction(): Promise<User[]> {
  try {
    const response = await api.get<ApiResponse<User[]>>(
      `${API_ROUTES.USERS}/professionals`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar profissionais');
  }
}

export async function getReceptionistsAction(): Promise<User[]> {
  try {
    const response = await api.get<ApiResponse<User[]>>(
      `${API_ROUTES.USERS}/receptionists`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar recepcionistas');
  }
}