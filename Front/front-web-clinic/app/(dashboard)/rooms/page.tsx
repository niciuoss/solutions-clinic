import { Metadata } from 'next';
import { RoomList } from '@/components/rooms/RoomList';
import { NewRoomButton } from '@/components/rooms/NewRoomButton';

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
        <NewRoomButton />
      </div>

      <RoomList />
    </div>
  );
}