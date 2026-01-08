'use server';

import { apiRequest } from './_helpers';
import type { 
  Patient, 
  CreatePatientRequest, 
  UpdatePatientRequest,
  PaginatedResponse 
} from '@/types';

export async function createPatientAction(data: CreatePatientRequest) {
  try {
    const patient = await apiRequest<Patient>('/patients', {
      method: 'POST',
      body: data,
    });

    return {
      success: true,
      data: patient,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao criar paciente',
    };
  }
}

export async function updatePatientAction(patientId: string, data: UpdatePatientRequest) {
  try {
    const patient = await apiRequest<Patient>(`/patients/${patientId}`, {
      method: 'PUT',
      body: data,
    });

    return {
      success: true,
      data: patient,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao atualizar paciente',
    };
  }
}

export async function getPatientByIdAction(patientId: string) {
  try {
    const patient = await apiRequest<Patient>(`/patients/${patientId}`, {
      method: 'GET',
    });

    return {
      success: true,
      data: patient,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar paciente',
    };
  }
}

export async function getAllPatientsAction(page: number = 0, size: number = 20) {
  try {
    const patients = await apiRequest<PaginatedResponse<Patient>>('/patients', {
      method: 'GET',
      params: { page, size, sort: 'fullName,asc' },
    });

    return {
      success: true,
      data: patients,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao listar pacientes',
    };
  }
}

export async function searchPatientsAction(query: string, page: number = 0, size: number = 20) {
  try {
    const patients = await apiRequest<PaginatedResponse<Patient>>('/patients/search', {
      method: 'GET',
      params: { query, page, size },
    });

    return {
      success: true,
      data: patients,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar pacientes',
    };
  }
}

export async function autocompletePatientsAction(name: string) {
  try {
    const patients = await apiRequest<Patient[]>('/patients/autocomplete', {
      method: 'GET',
      params: { name },
    });

    return {
      success: true,
      data: patients,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar pacientes',
    };
  }
}

export async function deletePatientAction(patientId: string) {
  try {
    await apiRequest(`/patients/${patientId}`, {
      method: 'DELETE',
    });

    return {
      success: true,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao excluir paciente',
    };
  }
}