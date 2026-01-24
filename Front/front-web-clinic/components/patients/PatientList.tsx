'use client'

import { useState, useCallback } from 'react';
import { usePatients, usePatientSearch } from '@/hooks/usePatients';
import { useAuth } from '@/hooks/useAuth';
import { useDebounce } from '@/hooks/useDebounce';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Switch } from '@/components/ui/switch';
import { Search, Eye, Edit } from 'lucide-react';
import Link from 'next/link';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { EmptyState } from '@/components/common/EmptyState';
import type { Patient } from '@/types/patient.types';

const PAGE_SIZE = 10;

export function PatientList() {
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());

  const debouncedSearch = useDebounce(searchQuery, 500);
  const { user } = useAuth();
  const tenantId = user?.clinicId || null;

  const isSearchMode = debouncedSearch.length >= 2;

  const { patients, isLoading: isLoadingAll, updatePatient } = usePatients(
    tenantId,
    page,
    PAGE_SIZE
  );
  const { data: searchData, isLoading: isSearching } = usePatientSearch(
    debouncedSearch,
    page,
    PAGE_SIZE
  );

  const isLoading = isLoadingAll || isSearching;
  const displayPatients = isSearchMode ? searchData?.content ?? [] : patients?.content ?? [];
  const pagination = isSearchMode ? searchData : patients;

  const toggleSelection = useCallback((id: string) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }, []);

  const toggleSelectAll = useCallback(
    (checked: boolean) => {
      if (checked) {
        setSelectedIds(new Set(displayPatients.map((p) => p.id)));
      } else {
        setSelectedIds(new Set());
      }
    },
    [displayPatients]
  );

  const isAllSelected =
    displayPatients.length > 0 && selectedIds.size === displayPatients.length;
  const isSomeSelected = selectedIds.size > 0;

  const handlePageChange = useCallback((newPage: number) => {
    setPage(newPage);
    setSelectedIds(new Set());
  }, []);

  const handleStatusToggle = useCallback(
    (patient: Patient) => {
      updatePatient(patient.id, { isActive: !patient.isActive });
    },
    [updatePatient]
  );

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
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setPage(0);
              setSelectedIds(new Set());
            }}
            className="pl-10"
          />
        </div>
      </Card>

      {/* Table */}
      {!displayPatients || displayPatients.length === 0 ? (
        <Card className="p-8">
          <EmptyState
            title="Nenhum paciente encontrado"
            description={
              isSearchMode
                ? 'Nenhum paciente corresponde à sua busca'
                : 'Adicione o primeiro paciente da clínica'
            }
          />
        </Card>
      ) : (
        <>
          <Card>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-10">
                    <Checkbox
                      checked={isAllSelected ? true : isSomeSelected ? 'indeterminate' : false}
                      onCheckedChange={(checked) =>
                        toggleSelectAll(checked === true)}
                      aria-label="Selecionar todos"
                    />
                  </TableHead>
                  <TableHead>Nome</TableHead>
                  <TableHead>CPF</TableHead>
                  <TableHead>Telefone</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Ações</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {displayPatients.map((patient) => (
                  <TableRow key={patient.id}>
                    <TableCell>
                      <Checkbox
                        checked={selectedIds.has(patient.id)}
                        onCheckedChange={() => toggleSelection(patient.id)}
                        aria-label={`Selecionar ${patient.fullName}`}
                      />
                    </TableCell>
                    <TableCell className="font-medium">{patient.fullName}</TableCell>
                    <TableCell className="text-muted-foreground">
                      {patient.cpf ?? '—'}
                    </TableCell>
                    <TableCell className="text-muted-foreground">
                      {patient.phone ?? patient.whatsapp ?? '—'}
                    </TableCell>
                    <TableCell className="text-muted-foreground">
                      {patient.email ?? '—'}
                    </TableCell>
                    <TableCell>
                      <div className="flex flex-col items-start gap-1.5">
                        <span className="text-sm font-medium">
                          {patient.isActive ? 'Ativo' : 'Inativo'}
                        </span>
                        <Switch
                          checked={patient.isActive}
                          onCheckedChange={() => handleStatusToggle(patient)}
                          className="data-[state=checked]:bg-green-600"
                          aria-label={patient.isActive ? 'Desativar paciente' : 'Ativar paciente'}
                        />
                      </div>
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="flex items-center justify-end gap-1">
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
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>

          {/* Pagination */}
          {pagination && pagination.totalPages > 1 && (
            <Card className="p-4">
              <div className="flex items-center justify-between">
                <div className="text-sm text-muted-foreground">
                  Mostrando {page * PAGE_SIZE + 1} a{' '}
                  {Math.min((page + 1) * PAGE_SIZE, pagination.totalElements)} de{' '}
                  {pagination.totalElements}
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handlePageChange(page - 1)}
                    disabled={page === 0}
                  >
                    Anterior
                  </Button>
                  <div className="flex items-center gap-1">
                    {Array.from(
                      { length: Math.min(5, pagination.totalPages) },
                      (_, i) => {
                        let pageNum: number;
                        if (pagination.totalPages <= 5) {
                          pageNum = i;
                        } else if (page < 3) {
                          pageNum = i;
                        } else if (page > pagination.totalPages - 4) {
                          pageNum = pagination.totalPages - 5 + i;
                        } else {
                          pageNum = page - 2 + i;
                        }
                        return (
                          <Button
                            key={pageNum}
                            variant={page === pageNum ? 'default' : 'outline'}
                            size="sm"
                            onClick={() => handlePageChange(pageNum)}
                            className="min-w-[40px]"
                          >
                            {pageNum + 1}
                          </Button>
                        );
                      }
                    )}
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handlePageChange(page + 1)}
                    disabled={page >= pagination.totalPages - 1}
                  >
                    Próxima
                  </Button>
                </div>
              </div>
            </Card>
          )}
        </>
      )}
    </div>
  );
}
