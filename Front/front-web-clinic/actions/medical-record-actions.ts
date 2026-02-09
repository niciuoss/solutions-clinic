'use server';

import { apiRequest } from './_helpers';
import type {
  MedicalRecord,
  CreateOrUpdateMedicalRecordRequest,
  VitalSigns,
  ActionResult,
} from '@/types';

/**
 * Cria ou atualiza prontuário do agendamento.
 * Backend: POST /v1/medical-records
 * Body: { appointmentId, templateId, content, vitalSigns? }
 */
export async function saveMedicalRecordAction(
  appointmentId: string,
  templateId: string,
  content: Record<string, unknown>,
  vitalSigns?: VitalSigns | null
): Promise<ActionResult<MedicalRecord>> {
  try {
    const body: CreateOrUpdateMedicalRecordRequest = {
      appointmentId,
      templateId,
      content,
    };
    if (vitalSigns != null) body.vitalSigns = vitalSigns;

    const record = await apiRequest<MedicalRecord>('/medical-records', {
      method: 'POST',
      body,
    });
    return { success: true, data: record };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao salvar prontuário',
    };
  }
}

/**
 * Assina o prontuário (define signed_at).
 * Backend: POST /v1/medical-records/:id/sign
 */
export async function signMedicalRecordAction(
  recordId: string
): Promise<ActionResult<MedicalRecord>> {
  try {
    const record = await apiRequest<MedicalRecord>(
      `/medical-records/${recordId}/sign`,
      { method: 'POST' }
    );
    return { success: true, data: record };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao assinar prontuário',
    };
  }
}

/**
 * Busca prontuário por agendamento.
 * Backend: GET /v1/medical-records/appointment/:appointmentId
 * Retorna 200 com body ou 204 No Content (sem prontuário ainda).
 */
export async function getMedicalRecordByAppointmentAction(
  appointmentId: string
): Promise<ActionResult<MedicalRecord | null>> {
  try {
    const record = await apiRequest<MedicalRecord | null>(
      `/medical-records/appointment/${appointmentId}`,
      { method: 'GET' }
    );
    return {
      success: true,
      data: record && typeof record === 'object' && 'id' in record ? record : null,
    };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar prontuário',
    };
  }
}

/**
 * Busca prontuário por ID.
 * Backend: GET /v1/medical-records/:id
 */
export async function getMedicalRecordByIdAction(
  id: string
): Promise<ActionResult<MedicalRecord>> {
  try {
    const record = await apiRequest<MedicalRecord>(`/medical-records/${id}`, {
      method: 'GET',
    });
    return { success: true, data: record };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Erro ao buscar prontuário',
    };
  }
}
