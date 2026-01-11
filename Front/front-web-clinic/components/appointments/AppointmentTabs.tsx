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
      {/* ✅ AJUSTADO - Melhor espaçamento entre botões */}
      <TabsList className="inline-flex h-10 items-center justify-center rounded-md bg-muted p-1 text-muted-foreground gap-1">
        <TabsTrigger 
          value="calendar" 
          className="inline-flex items-center justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium ring-offset-background transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm gap-2"
        >
          <Calendar className="h-4 w-4" />
          <span className="hidden sm:inline">Calendário Geral</span>
          <span className="sm:hidden">Geral</span>
        </TabsTrigger>
        <TabsTrigger 
          value="professionals"
          className="inline-flex items-center justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium ring-offset-background transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm gap-2"
        >
          <Users className="h-4 w-4" />
          <span className="hidden sm:inline">Por Profissional</span>
          <span className="sm:hidden">Profissional</span>
        </TabsTrigger>
        <TabsTrigger 
          value="list"
          className="inline-flex items-center justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium ring-offset-background transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm gap-2"
        >
          <List className="h-4 w-4" />
          <span>Lista</span>
        </TabsTrigger>
      </TabsList>

      <TabsContent value="calendar" className="space-y-4">
        <AppointmentCalendar />
      </TabsContent>

      <TabsContent value="professionals" className="space-y-4">
        {professionals && professionals.length > 0 ? (
          <ProfessionalAgenda professionals={professionals} />
        ) : (
          <div className="text-center py-12 text-muted-foreground">
            Nenhum profissional cadastrado
          </div>
        )}
      </TabsContent>

      <TabsContent value="list" className="space-y-4">
        <AppointmentTable />
      </TabsContent>
    </Tabs>
  );
}