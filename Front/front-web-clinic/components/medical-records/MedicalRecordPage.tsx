'use client'

import { useState } from 'react';
import { useAppointment } from '@/hooks/useAppointments';
import { PatientHeader } from './PatientHeader';
import { VitalSigns } from './VitalSigns';
import { QuickActions } from './QuickActions';
import { MedicalRecordForm } from './MedicalRecordForm';
import { RecentHistory } from './RecentHistory';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { Card } from '@/components/ui/card';

interface MedicalRecordPageProps {
  appointmentId: string;
}

export function MedicalRecordPage({ appointmentId }: MedicalRecordPageProps) {
  const { data: appointment, isLoading } = useAppointment(appointmentId);

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
      {/* Main Content */}
      <div className="flex-1 overflow-auto space-y-6 pb-6">
        {/* Patient Header */}
        <PatientHeader appointment={appointment} />

        {/* Vital Signs */}
        <VitalSigns appointmentId={appointmentId} />

        {/* Quick Actions */}
        <QuickActions appointmentId={appointmentId} />

        {/* Medical Record Form */}
        <MedicalRecordForm appointmentId={appointmentId} />
      </div>

      {/* Sidebar - Recent History */}
      <div className="w-96 overflow-auto">
        <RecentHistory patientId={appointment.patient.id} />
      </div>
    </div>
  );
}