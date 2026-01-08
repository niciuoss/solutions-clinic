'use server';

import { apiRequest } from './_helpers';
import type { 
  Appointment,
  CreateAppointmentRequest,
  UpdateAppointmentRequest,
  FinishAppointmentRequest,
  ActionResult,
} from '@/types';

export async function createAppointmentAction(
  data: CreateAppointmentRequest,
  forceSchedule: boolean = false
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>('/appointments', {
      method: 'POST',
      body: { ...data, forceSchedule },
    });

    return {
      success: true,
      data: appointment, // ✅ appointment JÁ é Appointment
    };
  } catch (error: any) {
    if (error.status === 409) {
      return {
        success: false,
        isConflict: true,
        error: error.message,
      };
    }

    return {
      success: false,
      isConflict: false,
      error: error.message || 'Erro ao criar agendamento',
    };
  }
}

export async function updateAppointmentAction(
  appointmentId: string,
  data: UpdateAppointmentRequest,
  forceSchedule: boolean = false
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>(
      `/appointments/${appointmentId}`,
      {
        method: 'PUT',
        body: { ...data, forceSchedule },
      }
    );

    return {
      success: true,
      data: appointment,
    };
  } catch (error: any) {
    if (error.status === 409) {
      return {
        success: false,
        isConflict: true,
        error: error.message,
      };
    }

    return {
      success: false,
      isConflict: false,
      error: error.message || 'Erro ao atualizar agendamento',
    };
  }
}

export async function startAppointmentAction(
  appointmentId: string
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>(
      `/appointments/${appointmentId}/start`,
      {
        method: 'POST',
      }
    );

    return {
      success: true,
      data: appointment,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao iniciar atendimento',
    };
  }
}

export async function finishAppointmentAction(
  appointmentId: string,
  data: FinishAppointmentRequest
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>(
      `/appointments/${appointmentId}/finish`,
      {
        method: 'POST',
        body: data,
      }
    );

    return {
      success: true,
      data: appointment,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao finalizar atendimento',
    };
  }
}

export async function cancelAppointmentAction(
  appointmentId: string,
  reason?: string
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>(
      `/appointments/${appointmentId}/cancel`,
      {
        method: 'POST',
        params: reason ? { reason } : undefined,
      }
    );

    return {
      success: true,
      data: appointment,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao cancelar agendamento',
    };
  }
}

export async function getAppointmentByIdAction(
  appointmentId: string
): Promise<ActionResult<Appointment>> {
  try {
    const appointment = await apiRequest<Appointment>(
      `/appointments/${appointmentId}`,
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: appointment,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar agendamento',
    };
  }
}

export async function getAppointmentsByDateRangeAction(
  startDate: string,
  endDate: string
): Promise<ActionResult<Appointment[]>> { // ✅ Tipagem explícita
  try {
    const appointments = await apiRequest<Appointment[]>(
      '/appointments/date-range',
      {
        method: 'GET',
        params: { startDate, endDate },
      }
    );

    return {
      success: true,
      data: appointments, // ✅ appointments JÁ é Appointment[]
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar agendamentos',
    };
  }
}

export async function getTodayAppointmentsAction(): Promise<ActionResult<Appointment[]>> {
  try {
    const appointments = await apiRequest<Appointment[]>(
      '/appointments/today',
      {
        method: 'GET',
      }
    );

    return {
      success: true,
      data: appointments,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar agendamentos de hoje',
    };
  }
}

export async function getAppointmentsByProfessionalAction(
  professionalId: string,
  startDate: string,
  endDate: string
): Promise<ActionResult<Appointment[]>> {
  try {
    const appointments = await apiRequest<Appointment[]>(
      `/appointments/professional/${professionalId}`,
      {
        method: 'GET',
        params: { startDate, endDate },
      }
    );

    return {
      success: true,
      data: appointments,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar agendamentos do profissional',
    };
  }
}

export async function getAppointmentsByPatientAction(
  patientId: string,
  page: number = 0,
  size: number = 20
): Promise<ActionResult<any>> { // PaginatedResponse<Appointment>
  try {
    const appointments = await apiRequest<any>(
      `/appointments/patient/${patientId}`,
      {
        method: 'GET',
        params: { page, size },
      }
    );

    return {
      success: true,
      data: appointments,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar histórico do paciente',
    };
  }
}

export async function checkAvailabilityAction(
  professionalId: string,
  startTime: string,
  durationMinutes: number,
  appointmentId?: string
): Promise<ActionResult<boolean>> {
  try {
    const isAvailable = await apiRequest<boolean>(
      '/appointments/check-availability',
      {
        method: 'GET',
        params: {
          professionalId,
          startTime,
          durationMinutes,
          ...(appointmentId && { appointmentId }),
        },
      }
    );

    return {
      success: true,
      data: isAvailable,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao verificar disponibilidade',
    };
  }
}

export async function getAvailableSlotsAction(
  professionalId: string,
  date: string,
  durationMinutes: number = 60
): Promise<ActionResult<string[]>> {
  try {
    const slots = await apiRequest<string[]>(
      '/appointments/available-slots',
      {
        method: 'GET',
        params: {
          professionalId,
          date,
          durationMinutes,
        },
      }
    );

    return {
      success: true,
      data: slots,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Erro ao buscar horários disponíveis',
    };
  }
}