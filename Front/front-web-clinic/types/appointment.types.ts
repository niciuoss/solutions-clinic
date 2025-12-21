import { Patient } from './patient.types';
import { Professional } from './professional.types';

export interface Appointment {
  id: string;
  patient: Patient;
  professional: Professional;
  room?: Room;
  
  scheduledAt: string;
  durationMinutes: number;
  status: AppointmentStatus;
  
  observations?: string;
  cancellationReason?: string;
  
  startedAt?: string;
  finishedAt?: string;
  durationActualMinutes?: number;
  
  totalValue: number;
  paymentMethod?: PaymentMethod;
  paymentStatus: PaymentStatus;
  paidAt?: string;
  
  procedures: Procedure[];
  createdAt: string;
}

export enum AppointmentStatus {
  AGENDADO = 'AGENDADO',
  CONFIRMADO = 'CONFIRMADO',
  EM_ATENDIMENTO = 'EM_ATENDIMENTO',
  FINALIZADO = 'FINALIZADO',
  CANCELADO = 'CANCELADO',
  NAO_COMPARECEU = 'NAO_COMPARECEU',
}

export enum PaymentMethod {
  PIX = 'PIX',
  DEBITO = 'DEBITO',
  CREDITO = 'CREDITO',
  DINHEIRO = 'DINHEIRO',
  BOLETO = 'BOLETO',
  OUTRO = 'OUTRO',
}

export enum PaymentStatus {
  PENDENTE = 'PENDENTE',
  PAGO = 'PAGO',
  CANCELADO = 'CANCELADO',
}

export interface Procedure {
  id: string;
  name: string;
  description?: string;
  value: number;
  quantity: number;
  totalValue: number;
}

export interface Room {
  id: string;
  name: string;
  description?: string;
  capacity: number;
  isActive: boolean;
}

export interface CreateAppointmentRequest {
  patientId: string;
  professionalId: string;
  roomId?: string;
  scheduledAt: string;
  durationMinutes?: number;
  observations?: string;
  procedures?: {
    name: string;
    description?: string;
    value: number;
    quantity: number;
  }[];
}

export interface UpdateAppointmentRequest {
  patientId?: string;
  professionalId?: string;
  roomId?: string;
  scheduledAt?: string;
  durationMinutes?: number;
  observations?: string;
  procedures?: {
    name: string;
    description?: string;
    value: number;
    quantity: number;
  }[];
}

export interface FinishAppointmentRequest {
  paymentMethod?: PaymentMethod;
  paymentStatus?: PaymentStatus;
  observations?: string;
}

export interface CreateRoomRequest {
  name: string;
  description?: string;
  capacity?: number;
}