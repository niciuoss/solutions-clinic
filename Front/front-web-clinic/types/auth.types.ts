export type TenantStatus = 'PENDING_SETUP' | 'TRIAL' | 'ACTIVE' | 'SUSPENDED' | 'CANCELED';
export type PlanType = 'BASIC' | 'PRO' | 'CUSTOM';

export interface User {
  id: string;
  clinicId: string;
  email: string;
  fullName: string;
  role: UserRole;
  cpf?: string;
  phone?: string;
  birthDate?: string;
  gender?: Gender;
  isActive: boolean;
  emailVerified: boolean;
  lastLoginAt?: string;
  createdAt: string;
  // Dados do Tenant
  tenantStatus?: TenantStatus;
  planType?: PlanType;
  trialEndsAt?: string;
}

export enum UserRole {
  ADMIN_CLINIC = 'ADMIN_CLINIC',
  PROFISSIONAL_SAUDE = 'PROFISSIONAL_SAUDE',
  RECEPCIONISTA = 'RECEPCIONISTA',
}

export enum Gender {
  MASCULINO = 'MASCULINO',
  FEMININO = 'FEMININO',
  OUTRO = 'OUTRO',
  NAO_INFORMADO = 'NAO_INFORMADO',
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SetPasswordRequest {
  token: string;
  password: string;
  confirmPassword: string;
}

// ===== AUTENTICAÇÃO =====
export interface AuthUserRequest {
  email: string;
  password: string;
}

export interface AuthUserResponse {
  access_token: string;
  expires_in: number;
}
