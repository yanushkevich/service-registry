'use client';
import { SL } from './ui';
import { KIND_META, DOMAIN_COLORS } from '../lib/graphData';

export default function Sidebar({
  nodes, edges, searchQuery, setSearchQuery, searchFiltered,
  filterKinds, setFilterKinds, filterDomains, setFilterDomains,
  eventQuery, setEventQuery, eventResults, setSelected,
  kindCounts, domCounts,
}) {
  const crossDomain = edges.filter((e) => e.crossDomain).length;

  return (
    <aside style={{
      borderRight: '1px solid #E4E1D8', display: 'flex', flexDirection: 'column',
      overflow: 'hidden', background: '#FDFCFA',
    }}>
      {/* Header */}
      <div style={{ padding: '14px 16px 12px', borderBottom: '1px solid #E4E1D8' }}>
        <div style={{ fontWeight: 700, fontSize: 15, marginBottom: 11, display: 'flex', alignItems: 'center', gap: 8 }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <circle cx="5" cy="5" r="3" stroke="#534AB7" strokeWidth="1.8" />
            <circle cx="19" cy="5" r="3" stroke="#1D9E75" strokeWidth="1.8" />
            <circle cx="5" cy="19" r="3" stroke="#D4880A" strokeWidth="1.8" />
            <circle cx="19" cy="19" r="3" stroke="#E24B4A" strokeWidth="1.8" />
            <path d="M8 5h8M5 8v8M19 8v8M8 19h8" stroke="#B4B2A9" strokeWidth="1.2" />
          </svg>
          Service Registry
        </div>

        {/* Search */}
        <div style={{ position: 'relative' }}>
          <input
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search services…"
            style={{
              width: '100%', boxSizing: 'border-box', border: '1px solid #DDD9CE',
              borderRadius: 8, padding: '6px 10px 6px 32px', fontSize: 12,
              background: '#F2F0EB', color: '#1A1917', outline: 'none',
            }}
          />
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
            style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', pointerEvents: 'none' }}>
            <circle cx="11" cy="11" r="7" stroke="#A09E97" strokeWidth="2" />
            <path d="M20 20l-3-3" stroke="#A09E97" strokeWidth="2" strokeLinecap="round" />
          </svg>

          {searchQuery && searchFiltered.length > 0 && (
            <div style={{
              position: 'absolute', top: '100%', left: 0, right: 0, background: '#fff',
              border: '1px solid #DDD9CE', borderRadius: 8, zIndex: 30,
              boxShadow: '0 6px 18px rgba(0,0,0,.12)', marginTop: 3,
            }}>
              {searchFiltered.map((n) => (
                <div
                  key={n.id}
                  onClick={() => { setSelected(n); setSearchQuery(''); }}
                  style={{
                    padding: '8px 12px', cursor: 'pointer', fontSize: 12,
                    display: 'flex', alignItems: 'center', gap: 8,
                    borderBottom: '1px solid #F0EEE7',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = '#F5F3EE')}
                  onMouseLeave={(e) => (e.currentTarget.style.background = '')}
                >
                  <span style={{ width: 9, height: 9, borderRadius: '50%', background: n.colorRing, display: 'inline-block', flexShrink: 0 }} />
                  <span>{n.name} {n.nameLine2}</span>
                  <span style={{ color: '#A09E97', marginLeft: 'auto', fontSize: 10 }}>{n.domain}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Filters + Event Search */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 12px' }}>
        <SL>Node Type</SL>
        {Object.entries(KIND_META).map(([k, m]) => (
          <div
            key={k}
            onClick={() => setFilterKinds((prev) => { const s = new Set(prev); s.has(k) ? s.delete(k) : s.add(k); return s; })}
            style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 5, cursor: 'pointer', opacity: filterKinds.has(k) ? 1 : 0.35, transition: 'opacity .15s' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 7, fontSize: 12 }}>
              <span style={{ width: 9, height: 9, borderRadius: '50%', background: m.dot, display: 'inline-block' }} />
              {m.label}
            </div>
            <span style={{ fontSize: 11, color: '#A09E97' }}>{kindCounts[k] || 0}</span>
          </div>
        ))}

        <SL>Domain</SL>
        {Object.keys(domCounts).map((d) => (
          <div
            key={d}
            onClick={() => setFilterDomains((prev) => { const s = new Set(prev); s.has(d) ? s.delete(d) : s.add(d); return s; })}
            style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 5, cursor: 'pointer', opacity: filterDomains.has(d) ? 1 : 0.35, transition: 'opacity .15s' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 7, fontSize: 12 }}>
              <span style={{ width: 9, height: 9, borderRadius: '50%', background: DOMAIN_COLORS[d] || '#888', display: 'inline-block' }} />
              {d}
            </div>
            <span style={{ fontSize: 11, color: '#A09E97' }}>{domCounts[d]}</span>
          </div>
        ))}

        <SL>Event Search</SL>
        <div style={{ position: 'relative', marginBottom: 8 }}>
          <input
            value={eventQuery}
            onChange={(e) => setEventQuery(e.target.value)}
            placeholder="e.g. OrderPlaced"
            style={{
              width: '100%', boxSizing: 'border-box', border: '1px solid #DDD9CE',
              borderRadius: 8, padding: '6px 10px 6px 30px', fontSize: 12,
              background: '#F2F0EB', color: '#1A1917', outline: 'none',
            }}
          />
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
            style={{ position: 'absolute', left: 9, top: '50%', transform: 'translateY(-50%)', pointerEvents: 'none' }}>
            <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="#D4880A" strokeWidth="2" strokeLinejoin="round" />
          </svg>
        </div>

        {eventResults && (
          <div style={{ background: '#F5F3EE', borderRadius: 9, padding: '10px 11px', border: '1px solid #E4E1D8', marginBottom: 10 }}>
            <div style={{ fontWeight: 700, fontSize: 12, marginBottom: 2 }}>{eventResults.eventName}</div>
            <div style={{ fontSize: 10, color: '#A09E97', marginBottom: 7 }}>
              {eventResults.channel.name} · {eventResults.broker.name} ({eventResults.broker.sub})
            </div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
              {eventResults.producers.map((p) => (
                <span key={p.node.id} onClick={() => setSelected(p.node)}
                  style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 10, padding: '2px 8px', borderRadius: 20, background: '#EEEDFE', color: '#3C3489', cursor: 'pointer', border: '1px solid #C4C0F0' }}>
                  ↑ {p.node.name}
                </span>
              ))}
              {eventResults.consumers.map((c) => (
                <span key={c.node.id} onClick={() => setSelected(c.node)}
                  style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 10, padding: '2px 8px', borderRadius: 20, background: '#DFF4EC', color: '#085041', cursor: 'pointer', border: '1px solid #8DD8BB' }}>
                  ↓ {c.node.name}
                </span>
              ))}
            </div>
          </div>
        )}

        {eventQuery && !eventResults && (
          <div style={{ fontSize: 11, color: '#A09E97', padding: '6px 0' }}>No events matching &quot;{eventQuery}&quot;</div>
        )}
      </div>

      {/* Footer */}
      <div style={{ padding: '9px 16px', borderTop: '1px solid #E4E1D8', fontSize: 10, color: '#A09E97', background: '#FDFCFA' }}>
        {nodes.length} nodes · {edges.length} edges · {crossDomain} cross-domain
      </div>
    </aside>
  );
}
