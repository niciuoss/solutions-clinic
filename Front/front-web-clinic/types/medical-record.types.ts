export interface VitalSigns {
  bloodPressure?: string; 
  heartRate?: number; 
  temperature?: number; 
  oxygenSaturation?: number; 
  weight?: number; 
  height?: number; 
  imc?: number; 
}

export interface MedicalRecord {
  id: string;
  appointmentId: string;
  patientId: string;
  professionalId: string;

  content: Record<string, unknown>;

  chiefComplaint?: string; 
  historyOfPresentIllness?: string; 
  physicalExamination?: string; 
  diagnosticHypothesis?: string; 
  treatmentPlan?: string; 
  prescriptions?: string; 
  examsRequested?: string;
  procedures?: string; 
  observations?: string; 

  vitalSigns?: VitalSigns;

  professionalSignature?: string;
  patientSignature?: string;
  signedAt?: string;

  createdAt: string;
  updatedAt: string;
}

export interface CreateMedicalRecordRequest {
  content: Record<string, unknown>;
  appointmentId: string;
  chiefComplaint?: string;
  historyOfPresentIllness?: string;
  physicalExamination?: string;
  diagnosticHypothesis?: string;
  treatmentPlan?: string;
  prescriptions?: string;
  examsRequested?: string;
  procedures?: string;
  observations?: string;
  vitalSigns?: VitalSigns;
}

export interface SignMedicalRecordRequest {
  professionalSignature?: string;
  patientSignature?: string;
  chiefComplaint?: string;
  historyOfPresentIllness?: string;
  physicalExamination?: string;
  diagnosticHypothesis?: string;
  treatmentPlan?: string;
  prescriptions?: string;
  examsRequested?: string;
  procedures?: string;
  observations?: string;
  vitalSigns?: VitalSigns;
}
