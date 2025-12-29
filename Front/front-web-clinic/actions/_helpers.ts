// Helpers para as Server Actions

import { cookies } from 'next/headers';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface ApiError {
  message: string;
  status: number;
}

/**
 * Obtém o token de autenticação dos cookies
 */
export async function getAuthToken(): Promise<string | null> {
  const cookieStore = await cookies();
  return cookieStore.get('auth-token')?.value || null;
}

/**
 * Obtém o userId do token JWT
 */
export async function getUserIdFromToken(): Promise<string | null> {
  const token = await getAuthToken();
  if (!token) return null;

  try {
    // Decodifica o JWT (sem verificar assinatura, apenas para ler o subject)
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const padding = '='.repeat((4 - (base64.length % 4)) % 4);
    const base64WithPadding = base64 + padding;
    
    // Usa Buffer do Node.js para decodificar base64
    const jsonPayload = Buffer.from(base64WithPadding, 'base64').toString('utf-8');
    const decoded = JSON.parse(jsonPayload);
    return decoded.sub || null;
  } catch {
    return null;
  }
}

/**
 * Define o token de autenticação nos cookies
 */
export async function setAuthToken(token: string, expiresIn: number): Promise<void> {
  const cookieStore = await cookies();
  const expiresAt = new Date(Date.now() + expiresIn * 1000);
  
  cookieStore.set('auth-token', token, {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax',
    expires: expiresAt,
    path: '/',
  });
}

/**
 * Remove o token de autenticação dos cookies
 */
export async function removeAuthToken(): Promise<void> {
  const cookieStore = await cookies();
  cookieStore.delete('auth-token');
}

/**
 * Faz uma requisição para a API
 */
export async function apiRequest<T>(
  endpoint: string,
  options: {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
    body?: unknown;
    params?: Record<string, string | number>;
    requireAuth?: boolean;
  } = {}
): Promise<T> {
  const { method = 'GET', body, params, requireAuth = false } = options;

  let url = `${API_BASE_URL}${endpoint}`;

  // Adiciona query params se existirem
  if (params) {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      searchParams.append(key, String(value));
    });
    url += `?${searchParams.toString()}`;
  }

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  // Adiciona token se necessário
  if (requireAuth) {
    const token = await getAuthToken();
    if (!token) {
      throw new Error('Token de autenticação não encontrado. Faça login novamente.');
    }
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    if (response.status === 401) {
      // Token inválido ou expirado
      await removeAuthToken();
      throw new Error('Sessão expirada. Faça login novamente.');
    }

    let errorMessage = `Erro na requisição (${response.status})`;
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorData.error || errorMessage;
    } catch {
      errorMessage = response.statusText || errorMessage;
    }

    const error: ApiError = {
      message: errorMessage,
      status: response.status,
    };
    throw error;
  }

  // Alguns endpoints podem retornar 204 (No Content) ou 200 sem corpo
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return {} as T;
  }

  // Verifica se há conteúdo antes de tentar fazer parse
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const text = await response.text();
    if (text.trim() === '') {
      return {} as T;
    }
    try {
      return JSON.parse(text);
    } catch {
      return {} as T;
    }
  }

  return {} as T;
}

