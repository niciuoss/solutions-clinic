'use client'

import { useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AppointmentCalendar } from './AppointmentCalendar';
import { ProfessionalAgenda } from './ProfessionalAgenda';
import { AppointmentTable } from './AppointmentTable';
import { useProfessionals } from '@/hooks/useProfessionals';
import { Calendar, Users, List } from 'lucide-react';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function AppointmentTabs() {
  const { professionals, isLoading } = useProfessionals();
  const [activeTab, setActiveTab] = useState('calendar');

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
      <TabsList className="grid w-full grid-cols-3 lg:w-[400px]">
        <TabsTrigger value="calendar" className="flex items-center gap-2">
          <Calendar className="h-4 w-4" />
          <span className="hidden sm:inline">Calendário Geral</span>
          <span className="sm:hidden">Calendário</span>
        </TabsTrigger>
        <TabsTrigger value="professionals" className="flex items-center gap-2">
          <Users className="h-4 w-4" />
          <span className="hidden sm:inline">Por Profissional</span>
          <span className="sm:hidden">Profissional</span>
        </TabsTrigger>
        <TabsTrigger value="list" className="flex items-center gap-2">
          <List className="h-4 w-4" />
          Lista
        </TabsTrigger>
      </TabsList>

      <TabsContent value="calendar" className="space-y-4">
        <AppointmentCalendar />
      </TabsContent>

      <TabsContent value="professionals" className="space-y-4">
        <ProfessionalAgenda professionals={professionals || []} />
      </TabsContent>

      <TabsContent value="list" className="space-y-4">
        <AppointmentTable />
      </TabsContent>
    </Tabs>
  );
}