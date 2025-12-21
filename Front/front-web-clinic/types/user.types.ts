import { UserRole, Gender } from './auth.types';

export interface CreateUserRequest {
  email: string;
  fullName: string;
  role: UserRole;
  cpf?: string;
  phone?: string;
  birthDate?: string;
  gender?: Gender;
}

export interface UpdateUserRequest {
  fullName?: string;
  cpf?: string;
  phone?: string;
  birthDate?: string;
  gender?: Gender;
}
