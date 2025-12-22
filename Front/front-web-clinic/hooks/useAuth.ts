'use client'

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { loginAction, logoutAction, setPasswordAction } from '@/actions/auth-actions';
import { ROUTES, STORAGE_KEYS } from '@/config/constants';
import type { LoginRequest, SetPasswordRequest, User } from '@/types';
import { toast } from 'sonner';

export function useAuth() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Para carregar usuário do localStorage na inicialização
  useEffect(() => {
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER);
    const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    
    if (storedUser && token) {
      setUser(JSON.parse(storedUser));
      setIsAuthenticated(true);
    }
    
    setIsLoading(false);
  }, []);

  const login = async (data: LoginRequest) => {
    try {
      setIsLoading(true);
      const response = await loginAction(data);
      
      setUser(response.user);
      setIsAuthenticated(true);
      
      toast.success('Login realizado com sucesso!');
      router.push(ROUTES.DASHBOARD);
      
      return response;
    } catch (error: any) {
      toast.error(error.message || 'Erro ao fazer login');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const setPassword = async (data: SetPasswordRequest) => {
    try {
      setIsLoading(true);
      await setPasswordAction(data);
      
      toast.success('Senha definida com sucesso! Você já pode fazer login.');
      router.push(ROUTES.LOGIN);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao definir senha');
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await logoutAction();
      
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
    setPassword,
    logout,
  };
}