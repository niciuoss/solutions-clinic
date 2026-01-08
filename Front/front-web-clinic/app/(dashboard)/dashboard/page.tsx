import { Metadata } from 'next';
import { DashboardStats } from '@/components/dashboard/DashboardStats';
import { TodayAppointments } from '@/components/dashboard/TodayAppointments';
import { RecentPatients } from '@/components/dashboard/RecentPatients';

export const metadata: Metadata = {
  title: 'Dashboard - Solutions Clinic',
};

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <DashboardStats />
      
      <div className="grid gap-6 md:grid-cols-2">
        <TodayAppointments />
        <RecentPatients />
      </div>
    </div>
  );
}