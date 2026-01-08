'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Calendar, Clock, User } from 'lucide-react';
import Link from 'next/link';
import { ROUTES } from '@/config/constants';

export function TodayAppointments() {
  // TODO: Buscar dados reais da API
  const appointments = [
    {
      id: '1',
      time: '08:00',
      patient: 'João Silva',
      professional: 'Dr. Carlos Santos',
      status: 'FINALIZADO',
    },
    {
      id: '2',
      time: '09:00',
      patient: 'Maria Oliveira',
      professional: 'Dra. Ana Costa',
      status: 'EM_ATENDIMENTO',
    },
    {
      id: '3',
      time: '10:00',
      patient: 'Pedro Santos',
      professional: 'Dr. Carlos Santos',
      status: 'AGENDADO',
    },
    {
      id: '4',
      time: '11:00',
      patient: 'Ana Paula',
      professional: 'Dra. Ana Costa',
      status: 'AGENDADO',
    },
  ];

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { label: string; variant: 'default' | 'secondary' | 'success' | 'warning' }> = {
      AGENDADO: { label: 'Agendado', variant: 'default' },
      EM_ATENDIMENTO: { label: 'Em Atendimento', variant: 'warning' },
      FINALIZADO: { label: 'Finalizado', variant: 'success' },
    };

    const config = variants[status] || variants.AGENDADO;

    return <Badge variant={config.variant as any}>{config.label}</Badge>;
  };

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="flex items-center gap-2">
          <Calendar className="h-5 w-5" />
          Agendamentos de Hoje
        </CardTitle>
        <Button variant="ghost" size="sm" asChild>
          <Link href={ROUTES.APPOINTMENTS}>Ver todos</Link>
        </Button>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {appointments.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-8">
              Nenhum agendamento para hoje
            </p>
          ) : (
            appointments.map((appointment) => (
              <div
                key={appointment.id}
                className="flex items-center justify-between p-3 rounded-lg border bg-card hover:bg-accent transition-colors"
              >
                <div className="flex items-center gap-3">
                  <div className="flex items-center gap-2">
                    <Clock className="h-4 w-4 text-muted-foreground" />
                    <span className="font-medium">{appointment.time}</span>
                  </div>
                  <div className="flex flex-col">
                    <span className="text-sm font-medium">{appointment.patient}</span>
                    <span className="text-xs text-muted-foreground">
                      {appointment.professional}
                    </span>
                  </div>
                </div>
                {getStatusBadge(appointment.status)}
              </div>
            ))
          )}
        </div>
      </CardContent>
    </Card>
  );
}