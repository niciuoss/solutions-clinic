'use server';

import { apiRequest } from './_helpers';
import type { 
  MedicalRecord,
  PaginatedResponse,
  ActionResult,
} from '@/types';

export async function saveMedicalRecordAction(
  appointmentId: string,
  content: Record<string, any>
): Promise<ActionResult<MedicalRecord>> {
  try {
    const record = await apiRequest<MedicalRecord>(
      `/medical-records/appointment/${appointmentId}`,
      {
        method: 'POST',
        body: content,
      }
    );

    return {
      success: true,
      data: record,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao salvar prontuário',
    };
  }
}

export async function signMedicalRecordAction(
  recordId: string,
  professionalSignature?: string,
  patientSignature?: string
): Promise<ActionResult<MedicalRecord>> {
  try {
    const record = await apiRequest<MedicalRecord>(
      `/medical-records/${recordId}/sign`,
      {
        method: 'POST',
        params: {
          ...(professionalSignature && { professionalSignature }),
          ...(patientSignature && { patientSignature }),
        },
      }
    );

    return {
      success: true,
      data: record,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao assinar prontuário',
    };
  }
}

export async function getMedicalRecordByAppointmentAction(
  appointmentId: string
): Promise<ActionResult<MedicalRecord>> {
  try {
    const record = await apiRequest<MedicalRecord>(
      `/medical-records/appointment/${appointmentId}`,
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: record,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar prontuário',
    };
  }
}

export async function getMedicalRecordsByPatientAction(
  patientId: string,
  page: number = 0,
  size: number = 20
): Promise<ActionResult<PaginatedResponse<MedicalRecord>>> {
  try {
    const records = await apiRequest<PaginatedResponse<MedicalRecord>>(
      `/medical-records/patient/${patientId}`,
      {
        method: 'GET',
        params: { page, size },
      }
    );

    return {
      success: true,
      data: records,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar prontuários do paciente',
    };
  }
}

export async function getMedicalRecordsByProfessionalAction(
  professionalId: string,
  page: number = 0,
  size: number = 20
): Promise<ActionResult<PaginatedResponse<MedicalRecord>>> {
  try {
    const records = await apiRequest<PaginatedResponse<MedicalRecord>>(
      `/medical-records/professional/${professionalId}`,
      {
        method: 'GET',
        params: { page, size },
      }
    );

    return {
      success: true,
      data: records,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar prontuários do profissional',
    };
  }
}