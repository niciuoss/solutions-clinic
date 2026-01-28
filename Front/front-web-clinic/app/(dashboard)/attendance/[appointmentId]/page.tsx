'use client'

import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

export default function AttendancePage() {
  const params = useParams();
  const router = useRouter();
  const appointmentId = params.appointmentId as string;

  return (
    <div className="container mx-auto py-8 space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h1 className="text-2xl font-bold">Atendimento</h1>
          <p className="text-muted-foreground">
            Pagina de atendimento - Em desenvolvimento
          </p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Atendimento #{appointmentId}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
            Esta pagina esta em desenvolvimento. Em breve voce podera realizar atendimentos por aqui.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
