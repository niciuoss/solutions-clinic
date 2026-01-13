'use server';

import { apiRequest } from './_helpers';
import type { 
  Patient, 
  CreatePatientRequest, 
  UpdatePatientRequest,
  PaginatedResponse,
  ActionResult, 
} from '@/types';

export async function createPatientAction(
  data: CreatePatientRequest
): Promise<ActionResult<Patient>> { 
  try {
    const requestData: any = {
      ...data,
      firstName: data.fullName,
    };
    delete requestData.fullName;

    const patient = await apiRequest<any>('/patients', {
      method: 'POST',
      body: requestData,
    });

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

export async function updatePatientAction(
  patientId: string, 
  data: UpdatePatientRequest
): Promise<ActionResult<Patient>> { 
  try {
    const requestData: any = { ...data };
    if (data.fullName) {
      requestData.firstName = data.fullName;
      delete requestData.fullName;
    }

    const patient = await apiRequest<any>(`/patients/${patientId}`, {
      method: 'PUT',
      body: requestData,
    });

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

export async function getPatientByIdAction(
  patientId: string
): Promise<ActionResult<Patient>> { 
  try {
    const patient = await apiRequest<any>(`/patients/${patientId}`, {
      method: 'GET',
    });

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

export async function getAllPatientsAction(
  tenantId: string, 
  page: number = 0, 
  size: number = 20
): Promise<ActionResult<PaginatedResponse<Patient>>> { 
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

export async function searchPatientsAction(
  query: string, 
  page: number = 0, 
  size: number = 20
): Promise<ActionResult<PaginatedResponse<Patient>>> { 
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

export async function autocompletePatientsAction(
  tenantId: string
): Promise<ActionResult<Patient[]>> { 
  try {
    if (!tenantId) {
      return {
        success: false,
        error: 'ID da clínica (tenantId) é obrigatório',
      };
    }

    // Buscar todos os pacientes do tenant (com limite maior para autocomplete)
    const response = await apiRequest<any>('/patients', {
      method: 'GET',
      params: { 
        tenantId, 
        page: 0, 
        size: 100, // Limite maior para autocomplete
        sort: 'firstName,asc' 
      },
    });

    let patients: Patient[] = [];
    
    if (response?.content) {
      patients = response.content.map((patient: any) => ({
        ...patient,
        fullName: patient.firstName || '',
      }));
    }

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

export async function deletePatientAction(
  patientId: string
): Promise<ActionResult<void>> { 
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