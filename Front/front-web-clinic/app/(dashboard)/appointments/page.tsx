import { Metadata } from 'next';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import { AppointmentTabs } from '@/components/appointments/AppointmentTabs';

export const metadata: Metadata = {
  title: 'Agendamentos - Solutions Clinic',
};

export default function AppointmentsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Agendamentos</h2>
          <p className="text-muted-foreground">
            Visualize e gerencie os agendamentos da clínica
          </p>
        </div>
        <Button asChild>
          <Link href="/appointments/new">
            <Plus className="mr-2 h-4 w-4" />
            Novo Agendamento
          </Link>
        </Button>
      </div>

      <AppointmentTabs />
    </div>
  );
}