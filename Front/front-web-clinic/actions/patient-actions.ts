'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  Patient, 
  CreatePatientRequest, 
  UpdatePatientRequest,
  ApiResponse,
  PaginatedResponse 
} from '@/types';

export async function createPatientAction(data: CreatePatientRequest): Promise<Patient> {
  try {
    const response = await api.post<ApiResponse<Patient>>(
      API_ROUTES.PATIENTS,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao criar paciente');
  }
}

export async function updatePatientAction(
  patientId: string,
  data: UpdatePatientRequest
): Promise<Patient> {
  try {
    const response = await api.put<ApiResponse<Patient>>(
      `${API_ROUTES.PATIENTS}/${patientId}`,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao atualizar paciente');
  }
}

export async function getPatientByIdAction(patientId: string): Promise<Patient> {
  try {
    const response = await api.get<ApiResponse<Patient>>(
      `${API_ROUTES.PATIENTS}/${patientId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar paciente');
  }
}

export async function getAllPatientsAction(
  page: number = 0,
  size: number = 20
): Promise<PaginatedResponse<Patient>> {
  try {
    const response = await api.get<ApiResponse<PaginatedResponse<Patient>>>(
      API_ROUTES.PATIENTS,
      {
        params: { page, size, sort: 'fullName,asc' }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao listar pacientes');
  }
}

export async function searchPatientsAction(
  query: string,
  page: number = 0,
  size: number = 20
): Promise<PaginatedResponse<Patient>> {
  try {
    const response = await api.get<ApiResponse<PaginatedResponse<Patient>>>(
      `${API_ROUTES.PATIENTS}/search`,
      {
        params: { query, page, size }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar pacientes');
  }
}

export async function autocompletePatientsAction(name: string): Promise<Patient[]> {
  try {
    const response = await api.get<ApiResponse<Patient[]>>(
      `${API_ROUTES.PATIENTS}/autocomplete`,
      {
        params: { name }
      }
    );
    return response.data.data;
  } 
  catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar pacientes');
  }
}

export async function deletePatientAction(patientId: string): Promise<void> {
  try {
    await api.delete<ApiResponse<void>>(
      `${API_ROUTES.PATIENTS}/${patientId}`
    );
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao excluir paciente');
  }
}