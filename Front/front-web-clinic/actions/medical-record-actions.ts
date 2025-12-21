'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  MedicalRecord,
  ApiResponse,
  PaginatedResponse 
} from '@/types';

export async function saveMedicalRecordAction(
  appointmentId: string,
  content: Record<string, any>
): Promise<MedicalRecord> {
  try {
    const response = await api.post<ApiResponse<MedicalRecord>>(
      `${API_ROUTES.MEDICAL_RECORDS}/appointment/${appointmentId}`,
      content
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao salvar prontuário');
  }
}

export async function signMedicalRecordAction(
  recordId: string,
  professionalSignature?: string,
  patientSignature?: string
): Promise<MedicalRecord> {
  try {
    const response = await api.post<ApiResponse<MedicalRecord>>(
      `${API_ROUTES.MEDICAL_RECORDS}/${recordId}/sign`,
      null,
      {
        params: { professionalSignature, patientSignature }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao assinar prontuário');
  }
}

export async function getMedicalRecordByAppointmentAction(
  appointmentId: string
): Promise<MedicalRecord> {
  try {
    const response = await api.get<ApiResponse<MedicalRecord>>(
      `${API_ROUTES.MEDICAL_RECORDS}/appointment/${appointmentId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar prontuário');
  }
}

export async function getMedicalRecordsByPatientAction(
  patientId: string,
  page: number = 0,
  size: number = 20
): Promise<PaginatedResponse<MedicalRecord>> {
  try {
    const response = await api.get<ApiResponse<PaginatedResponse<MedicalRecord>>>(
      `${API_ROUTES.MEDICAL_RECORDS}/patient/${patientId}`,
      {
        params: { page, size }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar prontuários do paciente');
  }
}

export async function getMedicalRecordsByProfessionalAction(
  professionalId: string,
  page: number = 0,
  size: number = 20
): Promise<PaginatedResponse<MedicalRecord>> {
  try {
    const response = await api.get<ApiResponse<PaginatedResponse<MedicalRecord>>>(
      `${API_ROUTES.MEDICAL_RECORDS}/professional/${professionalId}`,
      {
        params: { page, size }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar prontuários do profissional');
  }
}