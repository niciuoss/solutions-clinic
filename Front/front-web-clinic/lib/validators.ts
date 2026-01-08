import { z } from 'zod';

// Login
export const loginSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
});

// Set Password
export const setPasswordSchema = z.object({
  token: z.string().min(1, 'Token é obrigatório'),
  password: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
  confirmPassword: z.string().min(6, 'Confirmação de senha é obrigatória'),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'As senhas não conferem',
  path: ['confirmPassword'],
});

// Patient
export const patientSchema = z.object({
  fullName: z.string().min(3, 'Nome completo é obrigatório'),
  cpf: z.string().optional(),
  rg: z.string().optional(),
  birthDate: z.string().optional(),
  gender: z.enum(['MASCULINO', 'FEMININO', 'OUTRO', 'NAO_INFORMADO']).optional(),
  email: z.string().email('Email inválido').optional().or(z.literal('')),
  phone: z.string().optional(),
  whatsapp: z.string().optional(),
  
  addressStreet: z.string().optional(),
  addressNumber: z.string().optional(),
  addressComplement: z.string().optional(),
  addressNeighborhood: z.string().optional(),
  addressCity: z.string().optional(),
  addressState: z.string().max(2).optional(),
  addressZipcode: z.string().optional(),
  
  bloodType: z.string().optional(),
  allergies: z.string().optional(),
  
  guardianName: z.string().optional(),
  guardianPhone: z.string().optional(),
  guardianRelationship: z.string().optional(),
});

// Appointment
export const appointmentSchema = z.object({
  patientId: z.string().min(1, 'Paciente é obrigatório'),
  professionalId: z.string().min(1, 'Profissional é obrigatório'),
  roomId: z.string().optional(),
  scheduledAt: z.string().min(1, 'Data e hora são obrigatórias'),
  durationMinutes: z.number().min(15).max(480).default(60),
  observations: z.string().optional(),
  procedures: z.array(z.object({
    name: z.string().min(1, 'Nome do procedimento é obrigatório'),
    description: z.string().optional(),
    value: z.number().min(0, 'Valor deve ser positivo'),
    quantity: z.number().min(1, 'Quantidade deve ser pelo menos 1').default(1),
  })).optional(),
});

// User
export const userSchema = z.object({
  email: z.string().email('Email inválido'),
  fullName: z.string().min(3, 'Nome completo é obrigatório'),
  role: z.enum(['ADMIN_CLINIC', 'PROFISSIONAL_SAUDE', 'RECEPCIONISTA']),
  cpf: z.string().optional(),
  phone: z.string().optional(),
  birthDate: z.string().optional(),
  gender: z.enum(['MASCULINO', 'FEMININO', 'OUTRO', 'NAO_INFORMADO']).optional(),
});

// Professional
export const professionalSchema = z.object({
  userId: z.string().min(1, 'Usuário é obrigatório'),
  specialty: z.string().min(1, 'Especialidade é obrigatória'),
  documentType: z.enum(['CRM', 'CREFITO', 'CRO', 'CRP', 'CRN', 'COREN', 'OUTRO']),
  documentNumber: z.string().min(1, 'Número do documento é obrigatório'),
  documentState: z.string().max(2).optional(),
  bio: z.string().optional(),
  profileImageUrl: z.string().url('URL inválida').optional().or(z.literal('')),
});

// Room
export const roomSchema = z.object({
  name: z.string().min(1, 'Nome da sala é obrigatório'),
  description: z.string().optional(),
  capacity: z.number().min(1).default(1),
});