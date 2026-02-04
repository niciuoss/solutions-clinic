'use client'

import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { 
  Calendar, 
  Clock, 
  Phone, 
  Heart,
  AlertTriangle,
} from 'lucide-react';
import { format, parseISO } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import type { Appointment } from '@/types';

interface PatientHeaderProps {
  appointment: Appointment;
}

export function PatientHeader({ appointment }: PatientHeaderProps) {
  const { patient } = appointment;
  
  // Calcular idade (exemplo, ajuste conforme seu modelo)
  const calculateAge = (birthDate?: string) => {
    if (!birthDate) return null;
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
  };

  const age = patient.birthDate ? calculateAge(patient.birthDate) : null;

  // Mock data - ajuste conforme seu modelo
  const allergies = ['Dipirona', 'Penicilina']; // Pegar do patient
  const bloodType = 'O+'; // Pegar do patient
  const healthInsurance = 'Unimed Nacional'; // Pegar do patient

  return (
    <Card className="p-6">
      <div className="flex items-start justify-between">
        {/* Patient Info */}
        <div className="flex items-center gap-4">
          <Avatar className="h-20 w-20 bg-primary/10">
            <AvatarFallback className="text-2xl font-semibold text-primary">
              {patient.fullName.charAt(0)}
            </AvatarFallback>
          </Avatar>

          <div className="space-y-2">
            <h1 className="text-2xl font-bold">{patient.fullName}</h1>
            
            <div className="flex items-center gap-4 text-sm text-muted-foreground">
              {age && <span>{age} anos</span>}
              <span>•</span>
              <span>{patient.gender === 'MALE' ? 'Masculino' : 'Feminino'}</span>
              {bloodType && (
                <>
                  <span>•</span>
                  <span>Tipo {bloodType}</span>
                </>
              )}
            </div>

            <div className="flex items-center gap-4 text-sm">
              {patient.phone && (
                <div className="flex items-center gap-1">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span>{patient.phone}</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Appointment Info & Allergies */}
        <div className="flex flex-col items-end gap-3">
          <div className="flex items-center gap-4 text-sm">
            <div className="flex items-center gap-2 text-primary">
              <Calendar className="h-4 w-4" />
              <span className="font-medium">Consulta Hoje</span>
            </div>
            <div className="flex items-center gap-2">
              <Clock className="h-4 w-4 text-muted-foreground" />
              <span>{format(parseISO(appointment.scheduledAt), 'HH:mm')}</span>
            </div>
          </div>

          {healthInsurance && (
            <Badge variant="outline" className="text-primary border-primary">
              <Heart className="mr-1 h-3 w-3" />
              {healthInsurance}
            </Badge>
          )}

          {allergies.length > 0 && (
            <Badge variant="destructive" className="bg-red-50 text-red-700 border-red-200">
              <AlertTriangle className="mr-1 h-3 w-3" />
              ALERGIAS: {allergies.join(', ')}
            </Badge>
          )}
        </div>
      </div>
    </Card>
  );
}