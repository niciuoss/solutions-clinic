import { Metadata } from 'next';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import { ProfessionalList } from '@/components/professionals/ProfessionalList';

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
        <Button asChild>
          <Link href="/professionals/new">
            <Plus className="mr-2 h-4 w-4" />
            Novo Profissional
          </Link>
        </Button>
      </div>

      <ProfessionalList />
    </div>
  );
}
