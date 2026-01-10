'use client'

import { useState } from 'react';
import { usePatients, usePatientSearch } from '@/hooks/usePatients';
import { useAuth } from '@/hooks/useAuth';
import { useDebounce } from '@/hooks/useDebounce';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Search, Eye, Edit, Trash2 } from 'lucide-react';
import Link from 'next/link';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { EmptyState } from '@/components/common/EmptyState';

export function PatientList() {
  const [searchQuery, setSearchQuery] = useState('');
  const debouncedSearch = useDebounce(searchQuery, 500);
  const { user } = useAuth();
  const tenantId = user?.clinicId || null;

  const { patients, isLoading: isLoadingAll } = usePatients(tenantId, 0, 20);
  const { data: searchResults, isLoading: isSearching } = usePatientSearch(
    debouncedSearch,
    0,
    20
  );

  const isLoading = isLoadingAll || isSearching;
  const displayPatients = debouncedSearch.length >= 2 
    ? searchResults?.content 
    : patients?.content;

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="space-y-4">
      {/* Search */}
      <Card className="p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Buscar por nome, CPF ou telefone..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </Card>

      {/* List */}
      {!displayPatients || displayPatients.length === 0 ? (
        <EmptyState
          title="Nenhum paciente encontrado"
          description={
            debouncedSearch
              ? 'Nenhum paciente corresponde à sua busca'
              : 'Adicione o primeiro paciente da clínica'
          }
        />
      ) : (
        <Card>
          <div className="divide-y">
            {displayPatients.map((patient) => (
              <div
                key={patient.id}
                className="flex items-center justify-between p-4 hover:bg-accent/50 transition-colors"
              >
                <div className="flex-1">
                  <div className="flex items-center gap-3">
                    <h3 className="font-medium">{patient.fullName}</h3>
                    {!patient.isActive && (
                      <Badge variant="secondary">Inativo</Badge>
                    )}
                  </div>
                  <div className="mt-1 flex flex-wrap gap-3 text-sm text-muted-foreground">
                    {patient.cpf && <span>CPF: {patient.cpf}</span>}
                    {patient.phone && <span>Tel: {patient.phone}</span>}
                    {patient.email && <span>{patient.email}</span>}
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <Button variant="ghost" size="icon" asChild>
                    <Link href={`/patients/${patient.id}`}>
                      <Eye className="h-4 w-4" />
                    </Link>
                  </Button>
                  <Button variant="ghost" size="icon" asChild>
                    <Link href={`/patients/${patient.id}/edit`}>
                      <Edit className="h-4 w-4" />
                    </Link>
                  </Button>
                  <Button variant="ghost" size="icon">
                    <Trash2 className="h-4 w-4 text-red-500" />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Pagination */}
      {displayPatients && displayPatients.length > 0 && (
        <div className="flex justify-center">
          <Button variant="outline">Carregar mais</Button>
        </div>
      )}
    </div>
  );
}