import { Metadata } from 'next';
import { AppointmentTabs } from '@/components/appointments/AppointmentTabs';
import { NewAppointmentButton } from '@/components/appointments/NewAppointmentButton';

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
        <NewAppointmentButton />
      </div>

      <AppointmentTabs />
    </div>
  );
}