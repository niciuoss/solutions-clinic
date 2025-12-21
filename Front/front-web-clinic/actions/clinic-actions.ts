'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  Clinic,
  RegisterClinicRequest,
  ApiResponse 
} from '@/types';

export async function registerClinicAction(data: RegisterClinicRequest): Promise<Clinic> {
  try {
    const response = await api.post<ApiResponse<Clinic>>(
      API_ROUTES.CLINICS.REGISTER,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao registrar clínica');
  }
}

export async function getCurrentClinicAction(): Promise<Clinic> {
  try {
    const response = await api.get<ApiResponse<Clinic>>(
      API_ROUTES.CLINICS.ME
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar dados da clínica');
  }
}