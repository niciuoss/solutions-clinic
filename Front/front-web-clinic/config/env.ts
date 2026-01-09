export const env = {
  apiUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/v1',
  appName: process.env.NEXT_PUBLIC_APP_NAME || 'Solutions Clinic',
  appVersion: process.env.NEXT_PUBLIC_APP_VERSION || '1.0.0',
} as const;