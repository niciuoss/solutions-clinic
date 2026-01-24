export interface Procedure {
  id: string;
  tenantId: string;
  name: string;
  description?: string;
  estimatedDurationMinutes: number;
  basePrice: number;
  professionalCommissionPercent?: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProcedureRequest {
  tenantId: string;
  name: string;
  description?: string;
  estimatedDurationMinutes: number;
  basePrice: number;
  professionalCommissionPercent?: number;
}

export type UpdateProcedureRequest = Partial<CreateProcedureRequest> & {
  active?: boolean;
};
