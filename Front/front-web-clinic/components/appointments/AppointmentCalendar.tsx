'use client'

import { useEffect, useRef, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import listPlugin from '@fullcalendar/list';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAppointmentsByDateRange } from '@/hooks/useAppointments';
import { useRouter } from 'next/navigation';
import { format, startOfMonth, endOfMonth } from 'date-fns';
import ptBR from 'date-fns/locale/pt-BR';
import type { Appointment } from '@/types';
import './calendar-styles.css'; // Vamos criar esse arquivo

const statusColors: Record<string, string> = {
  AGENDADO: '#3b82f6', // blue
  CONFIRMADO: '#10b981', // green
  EM_ATENDIMENTO: '#f59e0b', // amber
  FINALIZADO: '#6366f1', // indigo
  CANCELADO: '#ef4444', // red
  NAO_COMPARECEU: '#9ca3af', // gray
};

export function AppointmentCalendar() {
  const router = useRouter();
  const calendarRef = useRef<FullCalendar>(null);
  const [currentMonth, setCurrentMonth] = useState(new Date());

  const startDate = format(startOfMonth(currentMonth), "yyyy-MM-dd'T'HH:mm:ss");
  const endDate = format(endOfMonth(currentMonth), "yyyy-MM-dd'T'HH:mm:ss");

  const { data: appointments, isLoading } = useAppointmentsByDateRange(startDate, endDate);

  // Converter appointments para eventos do FullCalendar
  const events = appointments?.map((appointment: Appointment) => ({
    id: appointment.id,
    title: `${appointment.patient.fullName} - ${appointment.professional.user.fullName}`,
    start: appointment.scheduledAt,
    end: new Date(
      new Date(appointment.scheduledAt).getTime() + 
      appointment.durationMinutes * 60000
    ).toISOString(),
    backgroundColor: statusColors[appointment.status],
    borderColor: statusColors[appointment.status],
    extendedProps: {
      appointment,
    },
  })) || [];

  const handleEventClick = (info: any) => {
    const appointment = info.event.extendedProps.appointment;
    router.push(`/appointments/${appointment.id}`);
  };

  const handleDateClick = (info: any) => {
    // Redirecionar para novo agendamento com data pré-selecionada
    router.push(`/appointments/new?date=${info.dateStr}`);
  };

  const handleDatesSet = (dateInfo: any) => {
    setCurrentMonth(dateInfo.view.currentStart);
  };

  return (
    <Card className="p-4">
      {/* Legenda de status */}
      <div className="mb-4 flex flex-wrap gap-2">
        {Object.entries(statusColors).map(([status, color]) => (
          <div key={status} className="flex items-center gap-2">
            <div
              className="h-3 w-3 rounded-full"
              style={{ backgroundColor: color }}
            />
            <span className="text-xs text-muted-foreground">
              {status.replace('_', ' ')}
            </span>
          </div>
        ))}
      </div>

      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin]}
        initialView="timeGridWeek"
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
        }}
        buttonText={{
          today: 'Hoje',
          month: 'Mês',
          week: 'Semana',
          day: 'Dia',
          list: 'Lista',
        }}
        locale="pt-br"
        timeZone="America/Fortaleza"
        slotMinTime="07:00:00"
        slotMaxTime="20:00:00"
        allDaySlot={false}
        weekends={true}
        events={events}
        eventClick={handleEventClick}
        dateClick={handleDateClick}
        datesSet={handleDatesSet}
        height="auto"
        nowIndicator={true}
        eventTimeFormat={{
          hour: '2-digit',
          minute: '2-digit',
          meridiem: false,
        }}
        slotLabelFormat={{
          hour: '2-digit',
          minute: '2-digit',
          meridiem: false,
        }}
      />
    </Card>
  );
}