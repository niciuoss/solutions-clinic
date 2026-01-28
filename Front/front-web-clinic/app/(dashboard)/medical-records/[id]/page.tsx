'use client'

import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft, FileText } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

export default function MedicalRecordDetailPage() {
  const params = useParams();
  const router = useRouter();
  const recordId = params.id as string;

  return (
    <div className="container mx-auto py-8 space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h1 className="text-2xl font-bold">Detalhes do Prontuario</h1>
          <p className="text-muted-foreground">
            Visualizacao do prontuario medico
          </p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            Prontuario #{recordId}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
            Esta pagina esta em desenvolvimento. Em breve voce podera visualizar detalhes do prontuario por aqui.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
