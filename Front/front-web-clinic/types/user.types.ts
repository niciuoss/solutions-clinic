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

// UserResponse do backend (firstName, lastName separados)
export interface UserListItem {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  cpf?: string;
  birthDate?: string;
  blocked: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateUserBodyRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
  cpf?: string;
  birthDate?: string;
  email?: string;
}

export interface UpdateUserBlockedBodyRequest {
  blocked: boolean;
}
