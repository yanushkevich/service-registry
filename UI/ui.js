'use client';

export function Badge({ children, bg, color, border }) {
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 3,
      fontSize: 10.5, padding: '2px 9px', borderRadius: 20,
      background: bg || '#F0EEE7', color: color || '#5F5E5A',
      border: `0.5px solid ${border || '#DDD9CE'}`,
      whiteSpace: 'nowrap', fontWeight: 500,
    }}>
      {children}
    </span>
  );
}

export function SL({ children }) {
  return (
    <div style={{
      fontSize: 10, fontWeight: 700, letterSpacing: '0.07em',
      color: '#A09E97', textTransform: 'uppercase', margin: '14px 0 7px',
    }}>
      {children}
    </div>
  );
}
