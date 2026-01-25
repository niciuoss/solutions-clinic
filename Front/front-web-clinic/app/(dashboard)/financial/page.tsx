'use client'

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { 
  ArrowUpCircle, 
  ArrowDownCircle, 
  DollarSign, 
  TrendingUp,
  Calendar,
  Filter,
} from 'lucide-react';
import { useFinancialDashboard } from '@/hooks/useFinancial';
import { useAuthContext } from '@/contexts/AuthContext';
import { formatCurrency } from '@/lib/utils';
import { FinancialStats } from '@/components/financial/FinancialStats';
import { FinancialCategoriesChart } from '@/components/financial/FinancialCategoriesChart';
import { PendingTransactions } from '@/components/financial/PendingTransactions';
import { FinancialTransactionsList } from '@/components/financial/FinancialTransactionsList';
import Link from 'next/link';
import { ROUTES } from '@/config/constants';
import { Plus } from 'lucide-react';

export default function FinancialPage() {
  const { user } = useAuthContext();
  const [startDate, setStartDate] = useState(() => {
    const date = new Date();
    date.setDate(1); // Primeiro dia do mês
    return date.toISOString().split('T')[0];
  });
  const [endDate, setEndDate] = useState(() => {
    return new Date().toISOString().split('T')[0];
  });

  const { data: dashboard, isLoading } = useFinancialDashboard(
    user?.clinicId || null,
    startDate,
    endDate
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Financeiro</h2>
          <p className="text-muted-foreground">
            Acompanhe receitas, despesas e fluxo de caixa
          </p>
        </div>
        <Button asChild>
          <Link href={`${ROUTES.FINANCIAL}/new`}>
            <Plus className="mr-2 h-4 w-4" />
            Nova Transação
          </Link>
        </Button>
      </div>

      {/* Filtros de Data */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Filter className="h-5 w-5" />
            Filtros
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="startDate">Data Inicial</Label>
              <Input
                id="startDate"
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="endDate">Data Final</Label>
              <Input
                id="endDate"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground">Carregando dados financeiros...</p>
        </div>
      ) : dashboard ? (
        <>
          {/* Estatísticas */}
          <FinancialStats dashboard={dashboard} />

          {/* Gráficos e Listas */}
          <div className="grid gap-6 md:grid-cols-2">
            <FinancialCategoriesChart 
              title="Despesas por Categoria"
              categories={dashboard.expensesByCategory}
              type="expense"
            />
            <FinancialCategoriesChart 
              title="Receitas por Categoria"
              categories={dashboard.incomesByCategory}
              type="income"
            />
          </div>

          {/* Transações Pendentes */}
          {dashboard.pendingTransactions && dashboard.pendingTransactions.length > 0 && (
            <PendingTransactions transactions={dashboard.pendingTransactions} />
          )}

          {/* Lista de Transações */}
          <FinancialTransactionsList 
            tenantId={user?.clinicId || ''}
            startDate={startDate}
            endDate={endDate}
          />
        </>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-muted-foreground">
              Nenhum dado financeiro encontrado para o período selecionado.
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
