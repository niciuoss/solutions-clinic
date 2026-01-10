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
    // Converter fullName para firstName para o backend
    const requestData: any = {
      ...data,
      firstName: data.fullName,
    };
    delete requestData.fullName;

    const patient = await apiRequest<any>('/patients', {
      method: 'POST',
      body: requestData,
    });

    // Mapear firstName para fullName para compatibilidade com o frontend
    const mappedPatient: Patient = {
      ...patient,
      fullName: patient.firstName || '',
    };

    return {
      success: true,
      data: mappedPatient,
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
    // Converter fullName para firstName para o backend, se existir
    const requestData: any = { ...data };
    if (data.fullName) {
      requestData.firstName = data.fullName;
      delete requestData.fullName;
    }

    const patient = await apiRequest<any>(`/patients/${patientId}`, {
      method: 'PUT',
      body: requestData,
    });

    // Mapear firstName para fullName para compatibilidade com o frontend
    const mappedPatient: Patient = {
      ...patient,
      fullName: patient.firstName || '',
    };

    return {
      success: true,
      data: mappedPatient,
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
    const patient = await apiRequest<any>(`/patients/${patientId}`, {
      method: 'GET',
    });

    // Mapear firstName para fullName para compatibilidade com o frontend
    const mappedPatient: Patient = {
      ...patient,
      fullName: patient.firstName || '',
    };

    return {
      success: true,
      data: mappedPatient,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar paciente',
    };
  }
}

export async function getAllPatientsAction(tenantId: string, page: number = 0, size: number = 20) {
  try {
    if (!tenantId) {
      return {
        success: false,
        error: 'ID da clínica (tenantId) é obrigatório',
      };
    }

    const response = await apiRequest<any>('/patients', {
      method: 'GET',
      params: { tenantId, page, size, sort: 'firstName,asc' },
    });

    // Mapear firstName para fullName para compatibilidade com o frontend
    if (response?.content) {
      response.content = response.content.map((patient: any) => ({
        ...patient,
        fullName: patient.firstName || '',
      }));
    }

    return {
      success: true,
      data: response as PaginatedResponse<Patient>,
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