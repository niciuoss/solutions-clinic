'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { useFinancialTransactions } from '@/hooks/useFinancial';
import { FinancialTransaction, TransactionType } from '@/types';
import { formatCurrency, formatDate } from '@/lib/utils';
import { useState } from 'react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface FinancialTransactionsListProps {
  tenantId: string;
  startDate: string;
  endDate: string;
}

export function FinancialTransactionsList({ 
  tenantId, 
  startDate, 
  endDate 
}: FinancialTransactionsListProps) {
  const [typeFilter, setTypeFilter] = useState<TransactionType | 'ALL'>('ALL');
  const { data: transactions, isLoading } = useFinancialTransactions(
    tenantId,
    typeFilter !== 'ALL' ? typeFilter : undefined,
    undefined,
    startDate,
    endDate
  );

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { label: string; variant: 'default' | 'secondary' | 'destructive' }> = {
      PENDENTE: { label: 'Pendente', variant: 'default' },
      PAGO: { label: 'Pago', variant: 'secondary' },
      CANCELADO: { label: 'Cancelado', variant: 'destructive' },
    };

    const config = variants[status] || variants.PENDENTE;
    return <Badge variant={config.variant as any}>{config.label}</Badge>;
  };

  if (isLoading) {
    return (
      <Card>
        <CardContent className="py-12 text-center">
          <p className="text-muted-foreground">Carregando transações...</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>Transações</CardTitle>
          <Select value={typeFilter} onValueChange={(value) => setTypeFilter(value as TransactionType | 'ALL')}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Filtrar por tipo" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">Todas</SelectItem>
              <SelectItem value={TransactionType.INCOME}>Receitas</SelectItem>
              <SelectItem value={TransactionType.EXPENSE}>Despesas</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </CardHeader>
      <CardContent>
        {transactions && transactions.length > 0 ? (
          <div className="space-y-2">
            {transactions.map((transaction) => (
              <div
                key={transaction.id}
                className="flex items-center justify-between p-4 rounded-lg border border-gray-200 hover:bg-gray-50"
              >
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-medium">{transaction.description}</span>
                    {getStatusBadge(transaction.status)}
                  </div>
                  <div className="text-sm text-muted-foreground">
                    {transaction.paymentDate 
                      ? `Pago em: ${formatDate(transaction.paymentDate)}`
                      : `Vencimento: ${formatDate(transaction.dueDate)}`
                    }
                    {transaction.categoryName && ` • ${transaction.categoryName}`}
                  </div>
                </div>
                <div className={`text-lg font-bold ${
                  transaction.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {transaction.type === 'INCOME' ? '+' : '-'}
                  {formatCurrency(Math.abs(transaction.amount))}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-sm text-muted-foreground text-center py-4">
            Nenhuma transação encontrada para o período selecionado.
          </p>
        )}
      </CardContent>
    </Card>
  );
}
