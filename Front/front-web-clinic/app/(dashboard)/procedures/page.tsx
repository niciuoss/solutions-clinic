import { Metadata } from 'next';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import { ProcedureList } from '@/components/procedures/ProcedureList';

export const metadata: Metadata = {
  title: 'Procedimentos - Solutions Clinic',
};

export default function ProceduresPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Procedimentos</h2>
          <p className="text-muted-foreground">
            Gerencie os procedimentos da clínica
          </p>
        </div>
        <Button asChild>
          <Link href="/procedures/new">
            <Plus className="mr-2 h-4 w-4" />
            Novo Procedimento
          </Link>
        </Button>
      </div>

      <ProcedureList />
    </div>
  );
}
