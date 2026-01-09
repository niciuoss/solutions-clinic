'use server';

import { apiRequest } from './_helpers';
import { API_ROUTES } from '@/config/constants';
import type { SignUpClinicOwnerRequest, SignUpSoloRequest, SignUpResponse } from '@/types';

interface SignUpResult {
  success: boolean;
  data?: SignUpResponse;
  error?: string;
}

/**
 * Cadastra uma nova clínica
 * Endpoint: POST /v1/auth/signup/clinic-owner
 */
export async function signUpClinicOwnerAction(
  data: SignUpClinicOwnerRequest
): Promise<SignUpResult> {
  try {
    if (!data.firstName || !data.lastName || !data.email || !data.password) {
      return {
        success: false,
        error: 'Dados do usuário são obrigatórios',
      };
    }

    if (!data.name || !data.cnpj || !data.subdomain) {
      return {
        success: false,
        error: 'Nome da clínica, CNPJ e subdomínio são obrigatórios',
      };
    }

    const response = await apiRequest<SignUpResponse>(
      API_ROUTES.AUTH.SIGNUP_CLINIC_OWNER,
      {
        method: 'POST',
        body: data,
        requireAuth: false,
      }
    );

    return {
      success: true,
      data: response,
    };
  } catch (error) {
    // Captura erros específicos da API
    if (error instanceof Error) {
      // Se a mensagem contém "sessão expirada" mas requireAuth é false, 
      // provavelmente é um erro de validação do backend
      if (error.message.includes('Sessão expirada') || error.message.includes('sessão expirada')) {
        return {
          success: false,
          error: 'Erro ao processar cadastro. Verifique os dados informados.',
        };
      }
      return {
        success: false,
        error: error.message,
      };
    }
    return {
      success: false,
      error: 'Erro ao cadastrar clínica',
    };
  }
}

/**
 * Cadastra um novo profissional solo
 * Endpoint: POST /v1/auth/signup/solo
 */
export async function signUpSoloAction(
  data: SignUpSoloRequest
): Promise<SignUpResult> {
  try {
    if (!data.firstName || !data.lastName || !data.email || !data.password || !data.birthDate) {
      return {
        success: false,
        error: 'Dados do usuário são obrigatórios',
      };
    }

    if (!data.name || !data.cpf || !data.subdomain) {
      return {
        success: false,
        error: 'Nome, CPF e subdomínio são obrigatórios',
      };
    }

    const response = await apiRequest<SignUpResponse>(
      API_ROUTES.AUTH.SIGNUP_SOLO,
      {
        method: 'POST',
        body: data,
        requireAuth: false,
      }
    );
    console.log("🚀 ~ signUpSoloAction ~ response:", response)

    return {
      success: true,
      data: response,
    };
  } catch (error) {
    console.log("🚀 ~ signUpSoloAction ~ error:", error)
    // Captura erros específicos da API
    if (error instanceof Error) {
      // Se a mensagem contém "sessão expirada" mas requireAuth é false, 
      // provavelmente é um erro de validação do backend
      if (error.message.includes('Sessão expirada') || error.message.includes('sessão expirada')) {
        return {
          success: false,
          error: 'Erro ao processar cadastro. Verifique os dados informados.',
        };
      }
      return {
        success: false,
        error: error.message,
      };
    }
    return {
      success: false,
      error: 'Erro ao cadastrar profissional',
    };
  }
}
