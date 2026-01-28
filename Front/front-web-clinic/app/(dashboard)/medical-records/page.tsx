'use client'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { FileText } from 'lucide-react';

export default function MedicalRecordsPage() {
  return (
    <div className="container mx-auto py-8 space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Prontuarios</h1>
        <p className="text-muted-foreground">
          Gerenciamento de prontuarios medicos
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            Prontuarios
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
            Esta pagina esta em desenvolvimento. Em breve voce podera gerenciar prontuarios por aqui.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
