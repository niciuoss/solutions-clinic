'use client'

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  LayoutDashboard,
  Users,
  Calendar,
  ClipboardList,
  UserCircle,
  Settings,
  LogOut,
  Stethoscope,
  UsersRound,
  DoorOpen,
} from 'lucide-react';
import { useAuthContext } from '@/contexts/AuthContext';
import { ROUTES } from '@/config/constants';

const navigation = [
  {
    name: 'Dashboard',
    href: ROUTES.DASHBOARD,
    icon: LayoutDashboard,
  },
  {
    name: 'Pacientes',
    href: ROUTES.PATIENTS,
    icon: Users,
  },
  {
    name: 'Agendamentos',
    href: ROUTES.APPOINTMENTS,
    icon: Calendar,
  },
  {
    name: 'Prontuários',
    href: ROUTES.MEDICAL_RECORDS,
    icon: ClipboardList,
  },
  {
    name: 'Profissionais',
    href: ROUTES.PROFESSIONALS,
    icon: Stethoscope,
    adminOnly: true,
  },
  {
    name: 'Usuários',
    href: ROUTES.USERS,
    icon: UsersRound,
    adminOnly: true,
  },
  {
    name: 'Salas',
    href: '/rooms',
    icon: DoorOpen,
    adminOnly: true,
  },
  {
    name: 'Configurações',
    href: ROUTES.SETTINGS,
    icon: Settings,
  },
];

export function Sidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuthContext();

  const isAdmin = user?.role === 'ADMIN_CLINIC';

  return (
    <div className="flex h-full w-64 flex-col border-r bg-white">
      {/* Logo */}
      <div className="flex h-16 items-center border-b px-6">
        <Link href={ROUTES.DASHBOARD} className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600">
            <Stethoscope className="h-5 w-5 text-white" />
          </div>
          <span className="text-lg font-semibold">Solutions Clinic</span>
        </Link>
      </div>

      {/* User info */}
      <div className="border-b p-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100">
            <UserCircle className="h-6 w-6 text-blue-600" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate">{user?.fullName || 'Usuário'}</p>
            <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <ScrollArea className="flex-1 px-3 py-4">
        <nav className="space-y-1">
          {navigation.map((item) => {
            // Esconder itens admin se não for admin
            if (item.adminOnly && !isAdmin) return null;

            const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
            const Icon = item.icon;

            return (
              <Link
                key={item.name}
                href={item.href}
                className={cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-700 hover:bg-gray-100'
                )}
              >
                <Icon className="h-5 w-5" />
                {item.name}
              </Link>
            );
          })}
        </nav>
      </ScrollArea>

      {/* Logout */}
      <div className="border-t p-4">
        <Button
          variant="ghost"
          className="w-full justify-start"
          onClick={logout}
        >
          <LogOut className="mr-3 h-5 w-5" />
          Sair
        </Button>
      </div>
    </div>
  );
}