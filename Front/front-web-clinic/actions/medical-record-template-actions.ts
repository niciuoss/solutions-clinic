'use server';

import { apiRequest } from './_helpers';
import type {
  MedicalRecordTemplate,
  CreateMedicalRecordTemplateRequest,
  ActionResult,
} from '@/types';

/**
 * Lista modelos de prontuário disponíveis para o tenant (globais + da clínica).
 * @param tenantId - ID da clínica (obrigatório)
 * @param activeOnly - apenas ativos (default true)
 * @param professionalType - filtrar por tipo de profissional (ex: FISIOTERAPEUTA)
 */
export async function getMedicalRecordTemplatesAction(
  tenantId: string,
  activeOnly: boolean = true,
  professionalType?: string
): Promise<ActionResult<MedicalRecordTemplate[]>> {
  try {
    const params: Record<string, string | boolean> = {
      tenantId,
      activeOnly: String(activeOnly),
    };
    if (professionalType) params.professionalType = professionalType;

    const list = await apiRequest<MedicalRecordTemplate[]>(
      '/medical-record-templates',
      { method: 'GET', params }
    );
    return {
      success: true,
      data: Array.isArray(list) ? list : [],
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar modelos de prontuário',
    };
  }
}

/**
 * Busca um modelo de prontuário por ID (visível se global ou do tenant).
 */
export async function getMedicalRecordTemplateByIdAction(
  id: string
): Promise<ActionResult<MedicalRecordTemplate>> {
  try {
    const template = await apiRequest<MedicalRecordTemplate>(
      `/medical-record-templates/${id}`,
      { method: 'GET' }
    );
    return { success: true, data: template };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar modelo',
    };
  }
}

/**
 * Cria um novo modelo de prontuário (sempre da clínica; globais são criados pelo sistema).
 */
export async function createMedicalRecordTemplateAction(
  body: CreateMedicalRecordTemplateRequest
): Promise<ActionResult<MedicalRecordTemplate>> {
  try {
    const template = await apiRequest<MedicalRecordTemplate>(
      '/medical-record-templates',
      { method: 'POST', body }
    );
    return { success: true, data: template };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao criar modelo',
    };
  }
}
