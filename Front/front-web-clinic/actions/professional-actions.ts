'use server';

import { apiRequest } from './_helpers';
import type { 
  Professional,
  CreateProfessionalRequest,
  ActionResult,
} from '@/types';

export async function createProfessionalAction(
  data: CreateProfessionalRequest
): Promise<ActionResult<Professional>> {
  try {
    const professional = await apiRequest<Professional>('/professionals', {
      method: 'POST',
      body: data,
    });

    return {
      success: true,
      data: professional,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao criar profissional',
    };
  }
}

export async function getProfessionalByIdAction(
  professionalId: string
): Promise<ActionResult<Professional>> {
  try {
    const professional = await apiRequest<Professional>(
      `/professionals/${professionalId}`,
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: professional,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar profissional',
    };
  }
}

export async function getProfessionalByUserIdAction(
  userId: string
): Promise<ActionResult<Professional>> {
  try {
    const professional = await apiRequest<Professional>(
      `/professionals/user/${userId}`,
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: professional,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar profissional',
    };
  }
}

export async function getAllActiveProfessionalsAction(): Promise<ActionResult<Professional[]>> {
  try {
    const professionals = await apiRequest<Professional[]>('/professionals', {
      method: 'GET',
    });

    return {
      success: true,
      data: professionals,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao listar profissionais',
    };
  }
}

export async function getProfessionalsBySpecialtyAction(
  specialty: string
): Promise<ActionResult<Professional[]>> {
  try {
    const professionals = await apiRequest<Professional[]>(
      `/professionals/specialty/${specialty}`,
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: professionals,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar profissionais',
    };
  }
}