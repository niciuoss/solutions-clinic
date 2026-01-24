'use client'

import Link from 'next/link';
import Image from 'next/image';
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
  FileText,
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
    name: 'Procedimentos',
    href: ROUTES.PROCEDURES,
    icon: FileText,
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
    <div className="flex h-full w-64 flex-col border-r bg-sidebar">
      {/* Logo */}
      <div className="flex h-16 items-center border-b px-6">
        <Link href={ROUTES.DASHBOARD} className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg ">
            <Image 
              src="/Logo.png" 
              alt="Logo da página"
              width={35} 
              height={35}
              className="object-contain" />
          </div>
          <span className="text-lg font-semibold text-sidebar-foreground">Solutions Clinic</span>
        </Link>
      </div>

      {/* User info */}
      <div className="border-b p-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-sidebar-accent">
            <UserCircle className="h-6 w-6 text-sidebar-accent-foreground" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate text-sidebar-foreground">
              {user?.fullName || 'Usuário'}
            </p>
            <p className="text-xs text-sidebar-foreground/70 truncate">{user?.email}</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <ScrollArea className="flex-1 px-3 py-4">
        <nav className="space-y-1">
          {navigation.map((item) => {
            if (item.adminOnly && !isAdmin) return null;

            const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
            const Icon = item.icon;

            return (
              <Link
                key={item.name}
                href={item.href}
                className={cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-all',
                  isActive
                    ?
                      'bg-sidebar-accent text-sidebar-accent-foreground font-semibold shadow-sm'
                    : 'text-sidebar-foreground/60 hover:bg-sidebar-accent/30 hover:text-sidebar-foreground'
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
          className="w-full justify-start text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          onClick={logout}
        >
          <LogOut className="mr-3 h-5 w-5" />
          Sair
        </Button>
      </div>
    </div>
  );
}