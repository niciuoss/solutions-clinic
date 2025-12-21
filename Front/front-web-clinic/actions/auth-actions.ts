'use server'

import api from '@/lib/api';
import { API_ROUTES, STORAGE_KEYS } from '@/config/constants';
import type { 
  LoginRequest, 
  AuthResponse, 
  SetPasswordRequest,
  ApiResponse 
} from '@/types';
import { cookies } from 'next/headers';

export async function loginAction(data: LoginRequest): Promise<AuthResponse> {
  try {
    const response = await api.post<ApiResponse<AuthResponse>>(
      API_ROUTES.AUTH.LOGIN,
      data
    );
    
    const { accessToken, refreshToken, user } = response.data.data;
    
    // Salvar nos cookies (mais seguro que localStorage para tokens)
    cookies().set('accessToken', accessToken, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 60 * 60, // 1 hora
    });
    
    cookies().set('refreshToken', refreshToken, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 7, // 7 dias
    });
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao fazer login');
  }
}

export async function setPasswordAction(data: SetPasswordRequest): Promise<void> {
  try {
    await api.post<ApiResponse<void>>('/users/set-password', data);
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao definir senha');
  }
}

export async function logoutAction(): Promise<void> {
  cookies().delete('accessToken');
  cookies().delete('refreshToken');
}

export async function getStoredUserAction() {
  // Implementar lógica para pegar usuário do token/cookie
  // Pode decodificar JWT ou fazer request para /me
  return null;
}

export async function isAuthenticatedAction(): Promise<boolean> {
  const token = cookies().get('accessToken');
  return !!token;
}