'use client'

import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useAppointmentsByTenant } from '@/hooks/useAppointments';
import { useAuthContext } from '@/contexts/AuthContext';
import { Search, Eye, Edit, X } from 'lucide-react';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

export function AppointmentTable() {
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const { user } = useAuthContext();
  const tenantId = user?.clinicId || null;

  const { data: appointments = [], isLoading } = useAppointmentsByTenant(
    tenantId,
    undefined,
    statusFilter !== 'all' ? statusFilter : undefined
  );

  const filteredAppointments = appointments?.filter((appointment) => {
    const matchesSearch =
      appointment.patient?.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      appointment.professional?.user?.fullName
        ?.toLowerCase()
        .includes(searchQuery.toLowerCase());

    return matchesSearch;
  });

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { label: string; variant: any }> = {
      AGENDADO: { label: 'Agendado', variant: 'default' },
      CONFIRMADO: { label: 'Confirmado', variant: 'default' },
      EM_ATENDIMENTO: { label: 'Em Atendimento', variant: 'secondary' },
      FINALIZADO: { label: 'Finalizado', variant: 'outline' },
      CANCELADO: { label: 'Cancelado', variant: 'destructive' },
      NAO_COMPARECEU: { label: 'Não Compareceu', variant: 'secondary' },
    };

    const config = variants[status] || variants.AGENDADO;
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  return (
    <div className="space-y-4">
      {/* Filtros */}
      <Card className="p-4">
        <div className="grid gap-4 md:grid-cols-2">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Buscar por paciente ou profissional..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>

          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Filtrar por status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos os status</SelectItem>
              <SelectItem value="AGENDADO">Agendado</SelectItem>
              <SelectItem value="CONFIRMADO">Confirmado</SelectItem>
              <SelectItem value="EM_ATENDIMENTO">Em Atendimento</SelectItem>
              <SelectItem value="FINALIZADO">Finalizado</SelectItem>
              <SelectItem value="CANCELADO">Cancelado</SelectItem>
              <SelectItem value="NAO_COMPARECEU">Não Compareceu</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </Card>

      {/* Tabela */}
      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Data/Hora</TableHead>
              <TableHead>Paciente</TableHead>
              <TableHead>Profissional</TableHead>
              <TableHead>Sala</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-8">
                  Carregando...
                </TableCell>
              </TableRow>
            ) : !filteredAppointments || filteredAppointments.length === 0 ? (
              <TableRow>
                <TableCell
                  colSpan={6}
                  className="text-center py-8 text-muted-foreground"
                >
                  Nenhum agendamento encontrado
                </TableCell>
              </TableRow>
            ) : (
              filteredAppointments.map((appointment) => (
                <TableRow key={appointment.id}>
                  <TableCell>
                    <div>
                      <p className="font-medium">
                        {format(new Date(appointment.scheduledAt), 'dd/MM/yyyy')}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {format(new Date(appointment.scheduledAt), 'HH:mm')}
                      </p>
                    </div>
                  </TableCell>
                  <TableCell>{appointment.patient.fullName}</TableCell>
                  <TableCell>
                    <div>
                      <p>{appointment.professional.user.fullName}</p>
                      <p className="text-sm text-muted-foreground">
                        {appointment.professional.specialty}
                      </p>
                    </div>
                  </TableCell>
                  <TableCell>
                    {appointment.room?.name || (
                      <span className="text-muted-foreground">-</span>
                    )}
                  </TableCell>
                  <TableCell>{getStatusBadge(appointment.status)}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button variant="ghost" size="icon" asChild>
                        <Link href={`/appointments/${appointment.id}`}>
                          <Eye className="h-4 w-4" />
                        </Link>
                      </Button>
                      <Button variant="ghost" size="icon" asChild>
                        <Link href={`/appointments/${appointment.id}/edit`}>
                          <Edit className="h-4 w-4" />
                        </Link>
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}