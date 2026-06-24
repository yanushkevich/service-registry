'use client';

export const IconService = ({ size = 28, color = '#534AB7' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <rect x="2" y="3" width="20" height="14" rx="2" stroke={color} strokeWidth="1.6" fill="none" />
    <path d="M8 21h8M12 17v4" stroke={color} strokeWidth="1.6" strokeLinecap="round" />
    <circle cx="7.5" cy="10" r="1.5" fill={color} />
    <circle cx="12" cy="10" r="1.5" fill={color} />
    <circle cx="16.5" cy="10" r="1.5" fill={color} />
  </svg>
);

export const IconGateway = ({ size = 28, color = '#0F6E56' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <path d="M12 2L2 7l10 5 10-5-10-5z" stroke={color} strokeWidth="1.6" strokeLinejoin="round" fill="none" />
    <path d="M2 17l10 5 10-5" stroke={color} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
    <path d="M2 12l10 5 10-5" stroke={color} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const IconASB = ({ size = 28, color = '#854F0B' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <ellipse cx="12" cy="6" rx="8" ry="3" stroke={color} strokeWidth="1.5" fill="none" />
    <path d="M4 6v5c0 1.66 3.58 3 8 3s8-1.34 8-3V6" stroke={color} strokeWidth="1.5" fill="none" />
    <path d="M4 11v5c0 1.66 3.58 3 8 3s8-1.34 8-3v-5" stroke={color} strokeWidth="1.5" fill="none" />
    <path d="M8 14.5l2 1.5 4-3" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const IconDatabase = ({ size = 28, color = '#A32D2D' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <ellipse cx="12" cy="5" rx="8" ry="3" stroke={color} strokeWidth="1.6" fill="none" />
    <path d="M4 5v4.5M20 5v4.5" stroke={color} strokeWidth="1.6" />
    <ellipse cx="12" cy="9.5" rx="8" ry="3" stroke={color} strokeWidth="1.6" fill="none" />
    <path d="M4 9.5V14M20 9.5V14" stroke={color} strokeWidth="1.6" />
    <ellipse cx="12" cy="14" rx="8" ry="3" stroke={color} strokeWidth="1.6" fill="none" />
    <path d="M4 14v4.5M20 14v4.5" stroke={color} strokeWidth="1.6" />
    <ellipse cx="12" cy="18.5" rx="8" ry="3" stroke={color} strokeWidth="1.6" fill="none" />
  </svg>
);

export const IconExternal = ({ size = 28, color = '#5F5E5A' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <circle cx="12" cy="12" r="9" stroke={color} strokeWidth="1.5" fill="none" />
    <path d="M12 3c-2.5 2.5-4 5.6-4 9s1.5 6.5 4 9" stroke={color} strokeWidth="1.2" fill="none" />
    <path d="M12 3c2.5 2.5 4 5.6 4 9s-1.5 6.5-4 9" stroke={color} strokeWidth="1.2" fill="none" />
    <path d="M3 12h18" stroke={color} strokeWidth="1.2" />
    <path d="M3.5 8h17M3.5 16h17" stroke={color} strokeWidth="0.9" />
  </svg>
);

export const IconClock = ({ size = 14, color = '#fff' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <circle cx="12" cy="12" r="9" stroke={color} strokeWidth="2" />
    <path d="M12 7v5l3 3" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const IconGRPC = ({ size = 12, color = '#534AB7' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <path d="M4 6h16M4 12h16M4 18h10" stroke={color} strokeWidth="2.2" strokeLinecap="round" />
  </svg>
);

export const IconREST = ({ size = 12, color = '#0C447C' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <path d="M3 12h18M14 6l6 6-6 6" stroke={color} strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

export const IconJDBC = ({ size = 12, color = '#7A1F1F' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <ellipse cx="12" cy="8" rx="7" ry="2.5" stroke={color} strokeWidth="1.8" fill="none" />
    <path d="M5 8v8c0 1.38 3.13 2.5 7 2.5s7-1.12 7-2.5V8" stroke={color} strokeWidth="1.8" fill="none" />
  </svg>
);

export const IconNodes = ({ size = 18, color = '#534AB7' }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
    <circle cx="5" cy="5" r="3" stroke={color} strokeWidth="1.8" />
    <circle cx="19" cy="5" r="3" stroke={color} strokeWidth="1.8" />
    <circle cx="5" cy="19" r="3" stroke={color} strokeWidth="1.8" />
    <circle cx="19" cy="19" r="3" stroke={color} strokeWidth="1.8" />
    <path d="M8 5h8M5 8v8M19 8v8M8 19h8" stroke={color} strokeWidth="1.2" />
  </svg>
);

export function NodeIcon({ kind, size = 28, color }) {
  if (kind === 'API_GATEWAY')    return <IconGateway size={size} color={color || '#0F6E56'} />;
  if (kind === 'MESSAGE_BROKER') return <IconASB size={size} color={color || '#854F0B'} />;
  if (kind === 'DATABASE')       return <IconDatabase size={size} color={color || '#A32D2D'} />;
  if (kind === 'EXTERNAL_SYSTEM') return <IconExternal size={size} color={color || '#5F5E5A'} />;
  return <IconService size={size} color={color || '#534AB7'} />;
}
