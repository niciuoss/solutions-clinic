'use client'

import api from '@/lib/api';
import { API_ROUTES } from '@/config/constants';
import type { 
  Appointment,
  CreateAppointmentRequest,
  UpdateAppointmentRequest,
  FinishAppointmentRequest,
  ApiResponse,
  PaginatedResponse 
} from '@/types';

export async function createAppointmentAction(
  data: CreateAppointmentRequest
): Promise<Appointment> {
  try {
    const response = await api.post<ApiResponse<Appointment>>(
      API_ROUTES.APPOINTMENTS,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao criar agendamento');
  }
}

export async function updateAppointmentAction(
  appointmentId: string,
  data: UpdateAppointmentRequest
): Promise<Appointment> {
  try {
    const response = await api.put<ApiResponse<Appointment>>(
      `${API_ROUTES.APPOINTMENTS}/${appointmentId}`,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao atualizar agendamento');
  }
}

export async function startAppointmentAction(appointmentId: string): Promise<Appointment> {
  try {
    const response = await api.post<ApiResponse<Appointment>>(
      `${API_ROUTES.APPOINTMENTS}/${appointmentId}/start`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao iniciar atendimento');
  }
}

export async function finishAppointmentAction(
  appointmentId: string,
  data: FinishAppointmentRequest
): Promise<Appointment> {
  try {
    const response = await api.post<ApiResponse<Appointment>>(
      `${API_ROUTES.APPOINTMENTS}/${appointmentId}/finish`,
      data
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao finalizar atendimento');
  }
}

export async function cancelAppointmentAction(
  appointmentId: string,
  reason?: string
): Promise<Appointment> {
  try {
    const response = await api.post<ApiResponse<Appointment>>(
      `${API_ROUTES.APPOINTMENTS}/${appointmentId}/cancel`,
      null,
      { params: { reason } }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao cancelar agendamento');
  }
}

export async function getAppointmentByIdAction(appointmentId: string): Promise<Appointment> {
  try {
    const response = await api.get<ApiResponse<Appointment>>(
      `${API_ROUTES.APPOINTMENTS}/${appointmentId}`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar agendamento');
  }
}

export async function getAppointmentsByDateRangeAction(
  startDate: string,
  endDate: string
): Promise<Appointment[]> {
  try {
    const response = await api.get<ApiResponse<Appointment[]>>(
      `${API_ROUTES.APPOINTMENTS}/date-range`,
      {
        params: { startDate, endDate }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar agendamentos');
  }
}

export async function getTodayAppointmentsAction(): Promise<Appointment[]> {
  try {
    const response = await api.get<ApiResponse<Appointment[]>>(
      `${API_ROUTES.APPOINTMENTS}/today`
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar agendamentos de hoje');
  }
}

export async function getAppointmentsByProfessionalAction(
  professionalId: string,
  startDate: string,
  endDate: string
): Promise<Appointment[]> {
  try {
    const response = await api.get<ApiResponse<Appointment[]>>(
      `${API_ROUTES.APPOINTMENTS}/professional/${professionalId}`,
      {
        params: { startDate, endDate }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar agendamentos do profissional');
  }
}

export async function getAppointmentsByPatientAction(
  patientId: string,
  page: number = 0,
  size: number = 20
): Promise<PaginatedResponse<Appointment>> {
  try {
    const response = await api.get<ApiResponse<PaginatedResponse<Appointment>>>(
      `${API_ROUTES.APPOINTMENTS}/patient/${patientId}`,
      {
        params: { page, size }
      }
    );
    
    return response.data.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Erro ao buscar histórico do paciente');
  }
}