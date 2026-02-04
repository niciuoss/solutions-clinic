'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ScrollArea } from '@/components/ui/scroll-area';
import { FileText, FlaskConical, Pill, Calendar } from 'lucide-react';

interface RecentHistoryProps {
  patientId: string;
}

export function RecentHistory({ patientId }: RecentHistoryProps) {
  // Mock data - substituir por dados reais
  const history = [
    {
      id: '1',
      type: 'consultation',
      date: '15/01/2025',
      title: 'Consulta de Rotina',
      description: 'Paciente em bom estado geral. Renovação de receitas.',
      professional: 'Dr(a). Carlos Mendes',
    },
    {
      id: '2',
      type: 'exam',
      date: '10/01/2025',
      title: 'Hemograma Completo',
      description: 'Resultados dentro dos parâmetros normais.',
    },
    {
      id: '3',
      type: 'prescription',
      date: '05/01/2025',
      title: 'Receita - Anti-hipertensivo',
      description: '',
      professional: 'Dr(a). Carlos Mendes',
    },
  ];

  const getIcon = (type: string) => {
    switch (type) {
      case 'consultation':
        return <FileText className="h-4 w-4 text-cyan-600" />;
      case 'exam':
        return <FlaskConical className="h-4 w-4 text-blue-600" />;
      case 'prescription':
        return <Pill className="h-4 w-4 text-green-600" />;
      default:
        return <Calendar className="h-4 w-4" />;
    }
  };

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'consultation':
        return 'Consulta';
      case 'exam':
        return 'Exame';
      case 'prescription':
        return 'Prescrição';
      default:
        return 'Outro';
    }
  };

  return (
    <Card className="h-full">
      <CardHeader className="pb-3">
        <CardTitle className="text-base text-muted-foreground font-medium uppercase tracking-wide flex items-center gap-2">
          <Calendar className="h-4 w-4" />
          Histórico Recente
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ScrollArea className="h-[calc(100vh-12rem)]">
          <div className="space-y-4">
            {history.map((item) => (
              <div
                key={item.id}
                className="border rounded-lg p-3 hover:bg-accent/50 transition-colors cursor-pointer"
              >
                <div className="flex items-start gap-3">
                  <div className="mt-1">{getIcon(item.type)}</div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs text-muted-foreground font-medium">
                        {getTypeLabel(item.type)}
                      </span>
                      <span className="text-xs text-muted-foreground">•</span>
                      <span className="text-xs text-muted-foreground">{item.date}</span>
                    </div>
                    <h4 className="font-medium text-sm mb-1">{item.title}</h4>
                    {item.professional && (
                      <p className="text-xs text-muted-foreground">{item.professional}</p>
                    )}
                    {item.description && (
                      <p className="text-xs text-muted-foreground mt-1">
                        {item.description}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
}