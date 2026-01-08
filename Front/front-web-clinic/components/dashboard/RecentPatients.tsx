'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Users } from 'lucide-react';
import Link from 'next/link';
import { ROUTES } from '@/config/constants';

export function RecentPatients() {
  // TODO: Buscar dados reais da API
  const patients = [
    { id: '1', name: 'João Silva', initials: 'JS', lastVisit: '2 dias atrás' },
    { id: '2', name: 'Maria Oliveira', initials: 'MO', lastVisit: '3 dias atrás' },
    { id: '3', name: 'Pedro Santos', initials: 'PS', lastVisit: '1 semana atrás' },
    { id: '4', name: 'Ana Paula', initials: 'AP', lastVisit: '2 semanas atrás' },
  ];

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="flex items-center gap-2">
          <Users className="h-5 w-5" />
          Pacientes Recentes
        </CardTitle>
        <Button variant="ghost" size="sm" asChild>
          <Link href={ROUTES.PATIENTS}>Ver todos</Link>
        </Button>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {patients.map((patient) => (
            <div
              key={patient.id}
              className="flex items-center justify-between p-3 rounded-lg border bg-card hover:bg-accent transition-colors"
            >
              <div className="flex items-center gap-3">
                <Avatar>
                  <AvatarFallback>{patient.initials}</AvatarFallback>
                </Avatar>
                <div className="flex flex-col">
                  <span className="text-sm font-medium">{patient.name}</span>
                  <span className="text-xs text-muted-foreground">
                    Última visita: {patient.lastVisit}
                  </span>
                </div>
              </div>
              <Button variant="ghost" size="sm" asChild>
                <Link href={`${ROUTES.PATIENTS}/${patient.id}`}>Ver</Link>
              </Button>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}