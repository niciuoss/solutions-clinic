'use client'

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { loginAction, logoutAction, setPasswordAction, getCurrentUserAction, getUserByIdAction } from '@/actions/auth-actions';
import { ROUTES } from '@/config/constants';
import type { User, UserRole } from '@/types';
import { toast } from 'sonner';

export function useAuth() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Carregar usuário do token na inicialização
  useEffect(() => {
    async function loadUser() {
      try {
        const result = await getCurrentUserAction();
        
        if (result.success && result.data?.userId) {
          // Buscar dados completos do usuário
          const userResult = await getUserByIdAction(result.data.userId);
          
          if (userResult.success && userResult.data) {
            setUser(userResult.data as User);
            setIsAuthenticated(true);
          } else {
            // Se não conseguir buscar dados completos, ainda marca como autenticado
            // mas com dados básicos do token
            setIsAuthenticated(true);
          }
        } else {
          setIsAuthenticated(false);
          setUser(null);
        }
      } catch {
        setIsAuthenticated(false);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    }
    
    loadUser();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      setIsLoading(true);
      const result = await loginAction(email, password);
      
      if (!result.success) {
        toast.error(result.error || 'Erro ao fazer login');
        setIsLoading(false);
        return result;
      }
      
      // Buscar dados completos do usuário após login bem-sucedido
      if (result.data?.user?.id) {
        const userResult = await getUserByIdAction(result.data.user.id);
        
        if (userResult.success && userResult.data) {
          setUser(userResult.data as User);
          setIsAuthenticated(true);
        } else if (result.data.user) {
          // Usar dados parciais se não conseguir buscar completos
          const partialUser: User = {
            id: result.data.user.id,
            email: result.data.user.email,
            fullName: result.data.user.fullName,
            role: result.data.user.role as UserRole,
            clinicId: result.data.user.clinicId,
            isActive: true,
            emailVerified: true,
            createdAt: new Date().toISOString(),
          };
          setUser(partialUser);
          setIsAuthenticated(true);
        }
      }
      
      toast.success('Login realizado com sucesso!');
      router.push(ROUTES.DASHBOARD);
      
      return result;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro ao fazer login';
      toast.error(errorMessage);
      setIsLoading(false);
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
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro ao definir senha';
      toast.error(errorMessage);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      setIsLoading(true);
      const result = await logoutAction();
      
      if (!result.success) {
        toast.error('Erro ao fazer logout');
        setIsLoading(false);
        return;
      }
      
      // Limpar estado do usuário
      setUser(null);
      setIsAuthenticated(false);
      
      toast.success('Logout realizado com sucesso!');
      router.push(ROUTES.LOGIN);
    } catch {
      toast.error('Erro ao fazer logout');
    } finally {
      setIsLoading(false);
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