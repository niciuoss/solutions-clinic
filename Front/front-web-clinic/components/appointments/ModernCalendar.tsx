'use client'

import { useState, useMemo, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAppointmentsByDateRange } from '@/hooks/useAppointments';
import { useAuth } from '@/hooks/useAuth';
import { useRouter } from 'next/navigation';
import { 
  format, 
  startOfWeek, 
  endOfWeek, 
  addWeeks, 
  subWeeks,
  eachDayOfInterval,
  isSameDay,
  isToday,
  startOfDay,
  parseISO,
  addHours,
  differenceInMinutes,
} from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { ChevronLeft, ChevronRight, Calendar as CalendarIcon, Clock } from 'lucide-react';
import type { Appointment } from '@/types';

// ✅ CORES MAIS VIBRANTES E VIVAS
const statusConfig: Record<string, { label: string; bg: string; border: string }> = {
  AGENDADO: { 
    label: 'Agendado', 
    bg: 'bg-blue-500', 
    border: 'border-blue-600'
  },
  CONFIRMADO: { 
    label: 'Confirmado', 
    bg: 'bg-green-500', 
    border: 'border-green-600'
  },
  EM_ATENDIMENTO: { 
    label: 'Em Atendimento', 
    bg: 'bg-amber-500', 
    border: 'border-amber-600'
  },
  FINALIZADO: { 
    label: 'Finalizado', 
    bg: 'bg-indigo-500', 
    border: 'border-indigo-600'
  },
  CANCELADO: { 
    label: 'Cancelado', 
    bg: 'bg-red-500', 
    border: 'border-red-600'
  },
  NAO_COMPARECEU: { 
    label: 'Não Compareceu', 
    bg: 'bg-gray-500', 
    border: 'border-gray-600'
  },
};

const HOUR_HEIGHT = 80;
const START_HOUR = 7;
const END_HOUR = 20;

export function ModernCalendar() {
  const router = useRouter();
  const { user } = useAuth();
  const [currentWeek, setCurrentWeek] = useState(new Date());
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const interval = setInterval(() => setCurrentTime(new Date()), 60000);
    return () => clearInterval(interval);
  }, []);

  const weekStart = startOfWeek(currentWeek, { locale: ptBR });
  const weekEnd = endOfWeek(currentWeek, { locale: ptBR });
  const weekDays = eachDayOfInterval({ start: weekStart, end: weekEnd });

  const startDate = format(weekStart, "yyyy-MM-dd'T'00:00:00");
  const endDate = format(weekEnd, "yyyy-MM-dd'T'23:59:59");

  const { data: appointments = [], isLoading } = useAppointmentsByDateRange(
    user?.clinicId ?? null,
    startDate,
    endDate
  );

  const appointmentsByDay = useMemo(() => {
    const grouped: Record<string, Appointment[]> = {};
    
    weekDays.forEach(day => {
      const dayKey = format(day, 'yyyy-MM-dd');
      grouped[dayKey] = appointments.filter(apt => 
        isSameDay(parseISO(apt.scheduledAt), day)
      ).sort((a, b) => 
        new Date(a.scheduledAt).getTime() - new Date(b.scheduledAt).getTime()
      );
    });
    
    return grouped;
  }, [appointments, weekDays]);

  const hours = Array.from({ length: END_HOUR - START_HOUR }, (_, i) => START_HOUR + i);

  const handlePreviousWeek = () => setCurrentWeek(subWeeks(currentWeek, 1));
  const handleNextWeek = () => setCurrentWeek(addWeeks(currentWeek, 1));
  const handleToday = () => setCurrentWeek(new Date());

  const handleAppointmentClick = (appointmentId: string) => {
    router.push(`/appointments/${appointmentId}`);
  };

  const handleTimeSlotClick = (day: Date, hour: number) => {
    const dateTime = addHours(startOfDay(day), hour);
    router.push(`/appointments/new?date=${format(dateTime, "yyyy-MM-dd'T'HH:mm:ss")}`);
  };

  const getCurrentTimePosition = () => {
    const currentHour = currentTime.getHours();
    const currentMinute = currentTime.getMinutes();
    
    if (currentHour < START_HOUR || currentHour >= END_HOUR) return null;
    
    const hourOffset = currentHour - START_HOUR;
    const minuteOffset = (currentMinute / 60) * HOUR_HEIGHT;
    
    return hourOffset * HOUR_HEIGHT + minuteOffset;
  };

  const currentTimePosition = getCurrentTimePosition();

  const getAppointmentStyle = (appointment: Appointment) => {
    const startTime = parseISO(appointment.scheduledAt);
    const hour = startTime.getHours();
    const minute = startTime.getMinutes();
    
    const hourOffset = hour - START_HOUR;
    const minuteOffset = (minute / 60) * HOUR_HEIGHT;
    const top = hourOffset * HOUR_HEIGHT + minuteOffset;
    
    const height = (appointment.durationMinutes / 60) * HOUR_HEIGHT;
    
    return { top, height };
  };

  if (isLoading) {
    return (
      <Card className="p-8 text-center">
        <p className="text-muted-foreground">Carregando agendamentos...</p>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <Card className="p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Button variant="outline" size="icon" onClick={handlePreviousWeek}>
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <Button variant="outline" onClick={handleToday}>
                Hoje
              </Button>
              <Button variant="outline" size="icon" onClick={handleNextWeek}>
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
            
            <div className="flex items-center gap-2">
              <CalendarIcon className="h-5 w-5 text-muted-foreground" />
              <h3 className="text-lg font-semibold">
                {format(weekStart, 'dd', { locale: ptBR })} - {format(weekEnd, "dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
              </h3>
            </div>
          </div>

          {/* Legenda */}
          <div className="hidden lg:flex items-center gap-3">
            {Object.entries(statusConfig).map(([status, config]) => (
              <div key={status} className="flex items-center gap-1.5">
                <div className={`h-3 w-3 rounded-full ${config.bg}`} />
                <span className="text-xs text-muted-foreground">{config.label}</span>
              </div>
            ))}
          </div>
        </div>
      </Card>

      {/* Calendário */}
      <Card className="overflow-hidden">
        <div className="overflow-x-auto">
          <div className="min-w-[800px]">
            <div className="grid grid-cols-8 border-b border-t bg-muted/50">
              <div className="p-4 font-medium text-sm text-muted-foreground border-r">
                Horário
              </div>
              {weekDays.map((day, index) => (
                <div
                  key={day.toISOString()}
                  className={`p-4 text-center ${
                    index < weekDays.length - 1 ? 'border-r' : ''
                  } ${isToday(day) ? 'bg-blue-50' : ''}`}
                >
                  <div className="font-medium text-sm capitalize">
                    {format(day, 'EEE', { locale: ptBR })}
                  </div>
                  <div className={`text-2xl font-bold mt-1 ${
                    isToday(day) ? 'text-blue-600' : 'text-foreground'
                  }`}>
                    {format(day, 'dd')}
                  </div>
                </div>
              ))}
            </div>

            {/* Grid de horários */}
            <div className="relative">
              {/* Linhas de hora */}
              <div className="grid grid-cols-8">
                <div className="border-r">
                  {hours.map(hour => (
                    <div
                      key={hour}
                      className="border-b"
                      style={{ height: `${HOUR_HEIGHT}px` }}
                    >
                      <span className="text-xs text-muted-foreground p-2 block">
                        {hour.toString().padStart(2, '0')}:00
                      </span>
                    </div>
                  ))}
                </div>

                {/* Colunas de dias */}
                {weekDays.map((day, dayIndex) => {
                  const dayKey = format(day, 'yyyy-MM-dd');
                  const dayAppointments = appointmentsByDay[dayKey] || [];

                  return (
                    <div
                      key={day.toISOString()}
                      className={`relative ${
                        dayIndex < weekDays.length - 1 ? 'border-r' : ''
                      } ${isToday(day) ? 'bg-blue-50/30' : ''}`}
                    >
                      {/* Células de hora clicáveis */}
                      {hours.map(hour => (
                        <div
                          key={hour}
                          className="border-b hover:bg-accent/50 cursor-pointer transition-colors"
                          style={{ height: `${HOUR_HEIGHT}px` }}
                          onClick={() => handleTimeSlotClick(day, hour)}
                        />
                      ))}

                      {/* Agendamentos */}
                      {dayAppointments.map(appointment => {
                        const { top, height } = getAppointmentStyle(appointment);
                        const config = statusConfig[appointment.status];

                        return (
                          <div
                            key={appointment.id}
                            className={`absolute left-1 right-1 rounded-lg border-2 p-2 cursor-pointer transition-all hover:shadow-lg hover:scale-[1.02] ${config.bg} ${config.border}`}
                            style={{
                              top: `${top}px`,
                              height: `${Math.max(height, 40)}px`,
                              zIndex: 10,
                            }}
                            onClick={() => handleAppointmentClick(appointment.id)}
                          >
                            <div className="flex flex-col h-full overflow-hidden text-white">
                              <div className="flex items-center gap-1 mb-1">
                                <Clock className="h-3 w-3 flex-shrink-0" />
                                <span className="text-xs font-bold">
                                  {format(parseISO(appointment.scheduledAt), 'HH:mm')}
                                </span>
                              </div>
                              <p className="font-bold text-sm line-clamp-1">
                                {appointment.patient.fullName}
                              </p>
                              {height > 60 && (
                                <p className="text-xs font-medium opacity-90 line-clamp-1">
                                  {appointment.professional.user.fullName}
                                </p>
                              )}
                              {height > 80 && appointment.room?.name && (
                                <Badge 
                                  variant="secondary" 
                                  className="mt-auto w-fit text-xs bg-white/20 text-white border-white/30"
                                >
                                  {appointment.room.name}
                                </Badge>
                              )}
                            </div>
                          </div>
                        );
                      })}

                      {/* Indicador de hora atual */}
                      {isToday(day) && currentTimePosition !== null && (
                        <>
                          <div
                            className="absolute left-0 right-0 border-t-2 border-red-500 z-20"
                            style={{ top: `${currentTimePosition}px` }}
                          >
                            <div className="absolute -left-2 -top-2 h-4 w-4 rounded-full bg-red-500" />
                          </div>
                        </>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}