'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  Professional,
  CreateProfessionalRequest,
  ApiResponse 
} from '@/types';

export async function createProfessionalAction(
  data: CreateProfessionalRequest
): Promise<Professional> {
  try {
    const response = await api.post<ApiResponse<Professional>>(
      API_ROUTES.PROFESSIONALS,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao criar profissional');
  }
}

export async function getProfessionalByIdAction(professionalId: string): Promise<Professional> {
  try {
    const response = await api.get<ApiResponse<Professional>>(
      `${API_ROUTES.PROFESSIONALS}/${professionalId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar profissional');
  }
}

export async function getProfessionalByUserIdAction(userId: string): Promise<Professional> {
  try {
    const response = await api.get<ApiResponse<Professional>>(
      `${API_ROUTES.PROFESSIONALS}/user/${userId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar profissional');
  }
}

export async function getAllActiveProfessionalsAction(): Promise<Professional[]> {
  try {
    const response = await api.get<ApiResponse<Professional[]>>(
      API_ROUTES.PROFESSIONALS
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao listar profissionais');
  }
}

export async function getProfessionalsBySpecialtyAction(
  specialty: string
): Promise<Professional[]> {
  try {
    const response = await api.get<ApiResponse<Professional[]>>(
      `${API_ROUTES.PROFESSIONALS}/specialty/${specialty}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar profissionais');
  }
}