export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  SET_PASSWORD: '/set-password',
  REGISTER_CLINIC: '/register-clinic',
  
  DASHBOARD: '/dashboard',
  PATIENTS: '/patients',
  APPOINTMENTS: '/appointments',
  ATTENDANCE: '/attendance',
  MEDICAL_RECORDS: '/medical-records',
  PROFESSIONALS: '/professionals',
  USERS: '/users',
  SETTINGS: '/settings',
} as const;

export const API_ROUTES = {
  AUTH: {
    LOGIN: '/auth/login',
    REFRESH: '/auth/refresh',
    LOGOUT: '/auth/logout',
  },
  CLINICS: {
    REGISTER: '/clinics/register',
    ME: '/clinics/me',
  },
  PATIENTS: '/patients',
  APPOINTMENTS: '/appointments',
  PROFESSIONALS: '/professionals',
  USERS: '/users',
  ROOMS: '/rooms',
  MEDICAL_RECORDS: '/medical-records',
} as const;

export const STORAGE_KEYS = {
  ACCESS_TOKEN: '@solutionsclinic:accessToken',
  REFRESH_TOKEN: '@solutionsclinic:refreshToken',
  USER: '@solutionsclinic:user',
} as const;