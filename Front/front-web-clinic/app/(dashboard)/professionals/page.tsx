import { Metadata } from 'next';
import { ProfessionalList } from '@/components/professionals/ProfessionalList';
import { NewProfessionalButton } from '@/components/professionals/NewProfessionalButton';

export const metadata: Metadata = {
  title: 'Profissionais - Solutions Clinic',
};

export default function ProfessionalsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Profissionais</h2>
          <p className="text-muted-foreground">
            Gerencie os profissionais de saúde da clínica
          </p>
        </div>
        <NewProfessionalButton />
      </div>

      <ProfessionalList />
    </div>
  );
}
