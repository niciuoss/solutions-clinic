export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  SET_PASSWORD: '/set-password',
  REGISTER: '/register',
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
    SIGN_IN: "/auth/sign-in",
    SIGNUP_CLINIC_OWNER: "/auth/signup/clinic-owner",
    SIGNUP_SOLO: "/auth/signup/solo",
    REFRESH: "/auth/refresh",
    LOGOUT: "/auth/logout",
  },
  CLINICS: {
    REGISTER: "/clinics/register",
    ME: "/clinics/me",
  },
  PATIENTS: "/patients",
  APPOINTMENTS: "/appointments",
  PROFESSIONALS: "/professionals",
  USERS: "/users",
  ROOMS: "/rooms",
  MEDICAL_RECORDS: "/medical-records",
} as const;

export const STORAGE_KEYS = {
  ACCESS_TOKEN: '@solutionsclinic:accessToken',
  REFRESH_TOKEN: '@solutionsclinic:refreshToken',
  USER: '@solutionsclinic:user',
} as const;