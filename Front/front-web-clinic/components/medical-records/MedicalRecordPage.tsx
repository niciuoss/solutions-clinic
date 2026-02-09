'use client';

import { useState, useCallback } from 'react';
import { useAppointment } from '@/hooks/useAppointments';
import { useAuth } from '@/hooks/useAuth';
import { PatientHeader } from './PatientHeader';
import { VitalSigns } from './VitalSigns';
import { QuickActions } from './QuickActions';
import { MedicalRecordForm } from './MedicalRecordForm';
import { RecentHistory } from './RecentHistory';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { Card } from '@/components/ui/card';
import type { VitalSigns as VitalSignsType, MedicalRecord } from '@/types';

interface MedicalRecordPageProps {
  appointmentId: string;
}

export function MedicalRecordPage({ appointmentId }: MedicalRecordPageProps) {
  const { data: appointment, isLoading } = useAppointment(appointmentId);
  const { user } = useAuth();
  const tenantId = user?.clinicId ?? null;

  const [vitalSigns, setVitalSigns] = useState<VitalSignsType | null>(null);

  const handleRecordLoaded = useCallback((record: MedicalRecord) => {
    if (record.vitalSigns && typeof record.vitalSigns === 'object' && Object.keys(record.vitalSigns).length > 0) {
      setVitalSigns((prev) => ({ ...prev, ...record.vitalSigns } as VitalSignsType));
    }
  }, []);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!appointment) {
    return (
      <Card className="p-8 text-center">
        <p className="text-muted-foreground">Agendamento não encontrado</p>
      </Card>
    );
  }

  return (
    <div className="flex gap-6 h-[calc(100vh-4rem)]">
      <div className="flex-1 overflow-auto space-y-6 pb-6">
        <PatientHeader appointment={appointment} />

        <VitalSigns
          appointmentId={appointmentId}
          value={vitalSigns}
          onChange={setVitalSigns}
        />

        <QuickActions appointmentId={appointmentId} />

        <MedicalRecordForm
          appointmentId={appointmentId}
          tenantId={tenantId}
          professionalType={appointment.professional?.specialty ?? null}
          vitalSigns={vitalSigns ?? undefined}
          onRecordLoaded={handleRecordLoaded}
        />
      </div>

      <div className="w-96 overflow-auto">
        <RecentHistory patientId={appointment.patient.id} />
      </div>
    </div>
  );
}
