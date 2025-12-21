export interface MedicalRecord {
  id: string;
  appointmentId: string;
  patientId: string;
  professionalId: string;

  content: Record<string, unknown>;

  professionalSignature?: string;
  patientSignature?: string;
  signedAt?: string;

  createdAt: string;
  updatedAt: string;
}

export interface CreateMedicalRecordRequest {
  content: Record<string, unknown>;
}

export interface SignMedicalRecordRequest {
  professionalSignature?: string;
  patientSignature?: string;
}
