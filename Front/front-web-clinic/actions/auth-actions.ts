'use server';

import { setAuthToken, removeAuthToken, apiRequest, getUserIdFromToken } from './_helpers';
import type { AuthUserRequest, AuthUserResponse } from '@/types';

/**
 * Realiza o login do usuário e salva o token nos cookies
 */
export async function loginAction(email: string, password: string) {
  try {
    if (!email || !password) {
      return {
        success: false,
        error: 'Email e senha são obrigatórios',
      };
    }

    const response = await apiRequest<AuthUserResponse>('/auth/sign-in', {
      method: 'POST',
      body: { email, password } as AuthUserRequest,
      requireAuth: false,
    });

    // Salva o token nos cookies
    await setAuthToken(response.access_token, response.expires_in);

    return {
      success: true,
      token: response.access_token,
      expiresIn: response.expires_in,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao fazer login',
    };
  }
}

/**
 * Realiza o logout removendo o token dos cookies
 */
export async function logoutAction() {
  try {
    await removeAuthToken();
    return {
      success: true,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao fazer logout',
    };
  }
}

/**
 * Obtém o ID do usuário atual do token
 */
export async function getCurrentUserIdAction() {
  try {
    const userId = await getUserIdFromToken();
    if (!userId) {
      return {
        success: false,
        error: 'Usuário não autenticado',
      };
    }
    return {
      success: true,
      data: userId,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao obter ID do usuário',
    };
  }
}