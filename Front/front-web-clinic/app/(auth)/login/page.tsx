import { LoginForm } from '@/components/auth/LoginForm';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Login - Solutions Clinic',
  description: 'Faça login no Solutions Clinic',
};

export default function LoginPage() {
  return <LoginForm />;
}