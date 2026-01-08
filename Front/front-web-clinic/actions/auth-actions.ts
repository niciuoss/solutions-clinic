'use server';

import { 
  setAuthToken, 
  setRefreshToken,
  removeAuthToken, 
  apiRequest, 
  getUserIdFromToken,
  getClinicIdFromToken,
  getUserRoleFromToken 
} from './_helpers';
import type { AuthResponse } from '@/types';

interface LoginResult {
  success: boolean;
  data?: {
    user: {
      id: string;
      email: string;
      fullName: string;
      role: string;
      clinicId: string;
    };
  };
  error?: string;
}

/**
 * Realiza o login do usuário e salva o token nos cookies
 */
export async function loginAction(email: string, password: string): Promise<LoginResult> {
  try {
    if (!email || !password) {
      return {
        success: false,
        error: 'Email e senha são obrigatórios',
      };
    }

    const response = await apiRequest<AuthResponse>('/auth/login', {
      method: 'POST',
      body: { email, password },
      requireAuth: false,
    });

    // Salva os tokens nos cookies
    await setAuthToken(response.accessToken, response.expiresIn);
    await setRefreshToken(response.refreshToken, 604800); // 7 dias

    return {
      success: true,
      data: {
        user: {
          id: response.user.id,
          email: response.user.email,
          fullName: response.user.fullName,
          role: response.user.role,
          clinicId: response.user.clinicId,
        },
      },
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao fazer login',
    };
  }
}

/**
 * Define senha inicial do usuário
 */
export async function setPasswordAction(
  token: string,
  password: string,
  confirmPassword: string
) {
  try {
    if (!token || !password || !confirmPassword) {
      return {
        success: false,
        error: 'Todos os campos são obrigatórios',
      };
    }

    if (password !== confirmPassword) {
      return {
        success: false,
        error: 'As senhas não conferem',
      };
    }

    await apiRequest('/users/set-password', {
      method: 'POST',
      body: { token, password, confirmPassword },
      requireAuth: false,
    });

    return {
      success: true,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao definir senha',
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

/**
 * Obtém informações do usuário atual do token
 */
export async function getCurrentUserAction() {
  try {
    const userId = await getUserIdFromToken();
    const clinicId = await getClinicIdFromToken();
    const role = await getUserRoleFromToken();

    if (!userId || !clinicId || !role) {
      return {
        success: false,
        error: 'Usuário não autenticado',
      };
    }

    return {
      success: true,
      data: {
        userId,
        clinicId,
        role,
      },
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao obter dados do usuário',
    };
  }
}

/**
 * Verifica se o usuário está autenticado
 */
export async function isAuthenticatedAction() {
  try {
    const userId = await getUserIdFromToken();
    return {
      success: true,
      data: !!userId,
    };
  } catch {
    return {
      success: true,
      data: false,
    };
  }
}