import { Metadata } from 'next';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import { RoomList } from '@/components/rooms/RoomList';

export const metadata: Metadata = {
  title: 'Salas - Solutions Clinic',
};

export default function RoomsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Salas</h2>
          <p className="text-muted-foreground">
            Gerencie as salas de atendimento da clínica
          </p>
        </div>
        <Button asChild>
          <Link href="/rooms/new">
            <Plus className="mr-2 h-4 w-4" />
            Nova Sala
          </Link>
        </Button>
      </div>

      <RoomList />
    </div>
  );
}