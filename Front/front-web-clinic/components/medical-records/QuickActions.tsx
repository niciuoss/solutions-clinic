'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  FileText,
  FlaskConical,
  FileCheck,
  Paperclip,
  History,
  Stethoscope,
  Send,
  Printer,
} from 'lucide-react';

interface QuickActionsProps {
  appointmentId: string;
}

export function QuickActions({ appointmentId }: QuickActionsProps) {
  const actions = [
    { icon: FileText, label: 'Receita', onClick: () => console.log('Receita') },
    { icon: FlaskConical, label: 'Exames', onClick: () => console.log('Exames') },
    { icon: FileCheck, label: 'Atestado', onClick: () => console.log('Atestado') },
    { icon: Paperclip, label: 'Anexos', onClick: () => console.log('Anexos') },
    { icon: History, label: 'Histórico', onClick: () => console.log('Histórico') },
    { icon: Stethoscope, label: 'Procedimentos', onClick: () => console.log('Procedimentos') },
    { icon: Send, label: 'Encaminhar', onClick: () => console.log('Encaminhar') },
    { icon: Printer, label: 'Imprimir', onClick: () => console.log('Imprimir') },
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base text-muted-foreground font-medium uppercase tracking-wide">
          Ações Rápidas
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-4 lg:grid-cols-8 gap-3">
          {actions.map((action) => {
            const Icon = action.icon;
            return (
              <Button
                key={action.label}
                variant="outline"
                className="flex flex-col h-auto py-4 gap-2"
                onClick={action.onClick}
              >
                <Icon className="h-5 w-5 text-primary" />
                <span className="text-xs">{action.label}</span>
              </Button>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}