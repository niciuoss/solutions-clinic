// ========== Sinais vitais (compatível com JSONB vital_signs) ==========
export interface VitalSigns {
  bloodPressure?: string;
  heartRate?: number;
  temperature?: number;
  oxygenSaturation?: number;
  weight?: number;
  height?: number;
  imc?: number;
}

// ========== Schema do template (campo do formulário) ==========
export interface MedicalRecordTemplateField {
  id: string;
  label: string;
  type: 'text' | 'textarea' | 'number' | 'date' | string;
  placeholder?: string;
}

// ========== Modelo de prontuário (global ou da clínica) ==========
export interface MedicalRecordTemplate {
  id: string;
  tenantId: string | null; // null = template global (sistema)
  name: string;
  professionalType: string | null;
  schema: MedicalRecordTemplateField[];
  readOnly: boolean; // true = template padrão do sistema
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMedicalRecordTemplateRequest {
  tenantId: string;
  name: string;
  professionalType?: string;
  schema: MedicalRecordTemplateField[];
}

// ========== Prontuário (respostas conforme template) ==========
export interface MedicalRecord {
  id: string;
  appointmentId: string;
  templateId: string;
  content: Record<string, unknown>;
  vitalSigns: VitalSigns | null;
  signedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrUpdateMedicalRecordRequest {
  appointmentId: string;
  templateId: string;
  content: Record<string, unknown>;
  vitalSigns?: VitalSigns | null;
}
