'use client'

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { loginAction, logoutAction, setPasswordAction, getCurrentUserAction } from '@/actions/auth-actions';
import { ROUTES } from '@/config/constants';
import type { User } from '@/types';
import { toast } from 'sonner';

export function useAuth() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Carregar usuário do token na inicialização
  useEffect(() => {
    async function loadUser() {
      const result = await getCurrentUserAction();
      
      if (result.success && result.data) {
        // Aqui você pode fazer uma requisição para buscar dados completos do usuário
        // Por enquanto, apenas marca como autenticado
        setIsAuthenticated(true);
      }
      
      setIsLoading(false);
    }
    
    loadUser();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      setIsLoading(true);
      const result = await loginAction(email, password);
      
      if (!result.success) {
        toast.error(result.error || 'Erro ao fazer login');
        return result;
      }
      
      if (result.data) {
        setUser(result.data.user as any);
        setIsAuthenticated(true);
      }
      
      toast.success('Login realizado com sucesso!');
      router.push(ROUTES.DASHBOARD);
      
      return result;
    } catch (error: any) {
      toast.error(error.message || 'Erro ao fazer login');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const definePassword = async (token: string, password: string, confirmPassword: string) => {
    try {
      setIsLoading(true);
      const result = await setPasswordAction(token, password, confirmPassword);
      
      if (!result.success) {
        toast.error(result.error || 'Erro ao definir senha');
        return result;
      }
      
      toast.success('Senha definida com sucesso! Você já pode fazer login.');
      router.push(ROUTES.LOGIN);
      
      return result;
    } catch (error: any) {
      toast.error(error.message || 'Erro ao definir senha');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      const result = await logoutAction();
      
      if (!result.success) {
        toast.error('Erro ao fazer logout');
        return;
      }
      
      setUser(null);
      setIsAuthenticated(false);
      
      toast.success('Logout realizado com sucesso!');
      router.push(ROUTES.LOGIN);
    } catch (error: any) {
      toast.error('Erro ao fazer logout');
    }
  };

  return {
    user,
    isLoading,
    isAuthenticated,
    login,
    setPassword: definePassword,
    logout,
  };
}