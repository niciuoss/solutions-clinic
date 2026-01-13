import { User } from './auth.types';

export interface Professional {
  id: string;
  user: User;
  specialty: string;
  documentType: DocumentType;
  documentNumber: string;
  documentState?: string;
  bio?: string;
  profileImageUrl?: string;
  isActive: boolean;
}

export enum DocumentType {
  CRM = 'CRM',
  CREFITO = 'CREFITO',
  CRO = 'CRO',
  CRP = 'CRP',
  CRN = 'CRN',
  COREN = 'COREN',
  OUTRO = 'OUTRO',
}

export interface CreateProfessionalRequest {
  userId: string;
  tenantId?: string;
  specialty: string;
  documentType: DocumentType;
  documentNumber: string;
  documentState?: string;
  bio?: string;
}