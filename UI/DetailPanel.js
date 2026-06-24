'use client';
import { NodeIcon, IconClock, IconASB, IconGRPC, IconREST, IconJDBC } from './Icons';
import { Badge, SL } from './ui';
import { KIND_META, GRAPH, EDGE_TYPE_BG, EDGE_TYPE_COLOR } from '../lib/graphData';

function ConnRow({ edge }) {
  const bg = EDGE_TYPE_BG[edge.type] || '#F0EEE7';
  const col = EDGE_TYPE_COLOR[edge.type] || '#5F5E5A';
  const Icon = edge.type === 'JDBC' ? IconJDBC
    : edge.type === 'AZURE_SERVICE_BUS' ? IconASB
      : edge.type === 'gRPC' ? IconGRPC
        : IconREST;
  return (
    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10, padding: '7px 0', borderBottom: '0.5px solid #EEECE5' }}>
      <div style={{ width: 28, height: 28, borderRadius: 8, background: bg, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, marginTop: 1 }}>
        <Icon size={14} color={col} />
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 12, fontWeight: 600, color: '#1A1917' }}>
          {edge.dir === 'inbound' ? '← ' : '→ '}{edge.peer?.name ?? '?'}
        </div>
        <div style={{ fontSize: 10, color: '#A09E97', marginTop: 1, lineHeight: 1.4 }}>
          {edge.dir} · {edge.type} · {edge.pattern}
          {edge.auth ? ` · ${edge.auth.toLowerCase().replace(/_/g, ' ')}` : ''}
          {edge.drivenBy ? ' · driven by outbox relay' : ''}
          {edge.subRef ? ` · sub: ${edge.subRef}` : ''}
        </div>
      </div>
      <span style={{ fontSize: 10, padding: '1px 6px', borderRadius: 4, background: bg, color: col, flexShrink: 0, fontWeight: 600 }}>
        {edge.type === 'AZURE_SERVICE_BUS' ? 'ASB' : edge.type}
      </span>
    </div>
  );
}

function nodeEdges(nodeId) {
  return [
    ...GRAPH.edges.filter((e) => e.tgt === nodeId).map((e) => ({ ...e, dir: 'inbound', peer: GRAPH.nodes.find((n) => n.id === e.src) })),
    ...GRAPH.edges.filter((e) => e.src === nodeId).map((e) => ({ ...e, dir: 'outbound', peer: GRAPH.nodes.find((n) => n.id === e.tgt) })),
  ];
}

export default function DetailPanel({ selected, aiData, aiLoading }) {
  if (!selected) {
    return (
      <aside style={{ borderLeft: '1px solid #E4E1D8', display: 'flex', flexDirection: 'column', overflow: 'hidden', background: '#FDFCFA' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', flex: 1, color: '#A09E97', flexDirection: 'column', gap: 10 }}>
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9" stroke="#DDD9CE" strokeWidth="1.5" />
            <path d="M8 12h8M12 8v8" stroke="#DDD9CE" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
          <span style={{ fontSize: 12 }}>Select a node to inspect</span>
        </div>
      </aside>
    );
  }

  const edges = nodeEdges(selected.id);

  return (
    <aside style={{ borderLeft: '1px solid #E4E1D8', display: 'flex', flexDirection: 'column', overflow: 'hidden', background: '#FDFCFA' }}>
      {/* Header */}
      <div style={{ padding: '14px 16px 12px', borderBottom: '1px solid #E4E1D8', flexShrink: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 10 }}>
          <div style={{ width: 40, height: 40, borderRadius: 11, background: selected.colorFill, border: `1.5px solid ${selected.colorRing}`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
            <NodeIcon kind={selected.kind} size={24} color={selected.colorText} />
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 15 }}>{selected.name} {selected.nameLine2}</div>
            <div style={{ fontSize: 11, color: '#A09E97' }}>{KIND_META[selected.kind]?.label} · {selected.domain} domain</div>
          </div>
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 5 }}>
          <Badge bg="#DFF4EC" color="#1A5C3A" border="#8DD8BB">● {selected.health}</Badge>
          {selected.protocol && selected.protocol !== 'NONE' && <Badge bg="#EEEDFE" color="#3C3489" border="#AFA9EC">{selected.protocol}</Badge>}
          {selected.platform && <Badge bg="#E1F5EE" color="#085041" border="#5DCAA5">{selected.platform}</Badge>}
          {selected.version && <Badge bg="#F0EEE7" color="#5F5E5A" border="#DDD9CE">v{selected.version}</Badge>}
        </div>
      </div>

      {/* Body */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 20px' }}>

        {/* AI Insights */}
        <SL>AI Insights</SL>
        {aiLoading ? (
          <div style={{ fontSize: 12, color: '#A09E97', padding: '6px 0', display: 'flex', alignItems: 'center', gap: 7 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" style={{ animation: 'spin 1s linear infinite' }}>
              <circle cx="12" cy="12" r="9" stroke="#A09E97" strokeWidth="2" strokeDasharray="28 14" />
            </svg>
            Generating insights…
          </div>
        ) : aiData?.summary && (
          <>
            <div style={{ fontSize: 12, color: '#3A3836', lineHeight: 1.65, background: '#F2F0EB', borderRadius: 9, padding: '9px 11px', marginBottom: 8 }}>{aiData.summary}</div>
            {aiData.insights?.map((ins, i) => (
              <div key={i} style={{ fontSize: 11, color: '#5F5E5A', display: 'flex', gap: 7, lineHeight: 1.55, marginBottom: 4 }}>
                <span style={{ color: '#C5A856', flexShrink: 0, marginTop: 1 }}>◆</span>{ins}
              </div>
            ))}
            {aiData.suggestedTags?.length > 0 && (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginTop: 8 }}>
                {aiData.suggestedTags.map((t) => (
                  <span key={t} style={{ fontSize: 10, padding: '1px 7px', borderRadius: 20, background: '#ECEAE3', color: '#5F5E5A', border: '1px solid #DDD9CE' }}>{t}</span>
                ))}
              </div>
            )}
          </>
        )}

        {/* Ownership */}
        <SL>Ownership</SL>
        {[['Team', selected.team], ['Repo', selected.repo], ['SLA', selected.sla], ['Tech', selected.tech]].filter(([, v]) => v).map(([k, v]) => (
          <div key={k} style={{ display: 'flex', gap: 10, fontSize: 12, marginBottom: 5 }}>
            <span style={{ color: '#A09E97', width: 38, flexShrink: 0 }}>{k}</span>
            <span style={{ color: k === 'Repo' ? '#2563EB' : '#3A3836', wordBreak: 'break-all' }}>{v}</span>
          </div>
        ))}

        {/* Embedded Behaviours */}
        {selected.behaviours?.length > 0 && (
          <>
            <SL>Embedded Behaviours</SL>
            {selected.behaviours.map((b) => (
              <div key={b.id} style={{ border: '1px dashed #DDD9CE', borderRadius: 9, padding: '9px 11px', marginBottom: 8, background: '#F9F8F5' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 700, fontSize: 12, marginBottom: 3 }}>
                  <IconClock size={13} color="#534AB7" /><span>{b.name}</span>
                </div>
                <div style={{ fontSize: 10, color: '#A09E97' }}>{b.type} · {b.schedule} · drives {b.drivenEdges.length} edge</div>
                {b.desc && <div style={{ fontSize: 11, color: '#5F5E5A', marginTop: 4 }}>{b.desc}</div>}
              </div>
            ))}
          </>
        )}

        {/* Route Rules */}
        {selected.routeRules?.length > 0 && (
          <>
            <SL>Route Rules</SL>
            {selected.routeRules.map((r, i) => (
              <div key={i} style={{ fontSize: 12, padding: '6px 10px', background: '#F2F0EB', borderRadius: 8, marginBottom: 5, display: 'flex', gap: 7, alignItems: 'center' }}>
                <span style={{ color: '#A09E97', fontSize: 10 }}>{r.methods}</span>
                <span style={{ fontWeight: 600 }}>{r.path}</span>
                <span style={{ color: '#A09E97' }}>→</span>
                <span style={{ color: '#2563EB', fontSize: 11 }}>{r.backend}</span>
              </div>
            ))}
          </>
        )}

        {/* Connections */}
        <SL>Connections</SL>
        {edges.length === 0
          ? <div style={{ fontSize: 12, color: '#A09E97' }}>No connections registered</div>
          : edges.map((e) => <ConnRow key={e.id} edge={e} />)
        }

        {/* Broker Channels */}
        {selected.channels?.length > 0 && (
          <>
            <SL>Broker Channels ({selected.name})</SL>
            {selected.channels.map((ch) => (
              <div key={ch.id} style={{ border: '1px solid #E4E1D8', borderRadius: 10, padding: '9px 11px', marginBottom: 8, background: '#FBF9F6' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 3 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 700, fontSize: 12 }}>
                    <IconASB size={13} color={selected.colorText} />{ch.name}
                  </div>
                  <span style={{ fontSize: 9, padding: '1px 6px', borderRadius: 4, background: '#FAEEDA', color: '#633806', fontWeight: 600 }}>{ch.kind}</span>
                </div>
                {ch.schema && <div style={{ fontSize: 10, color: '#A09E97', marginBottom: 5 }}>{ch.schema} · schema-{ch.schemaVersion} · retention {ch.retention}{ch.dlqCount > 0 ? ` · DLQ: ${ch.dlqCount} msg` : ''}</div>}
                {ch.subscriptions?.length > 0 && (
                  <div style={{ borderLeft: '2px solid #E4E1D8', paddingLeft: 10, marginTop: 5 }}>
                    {ch.subscriptions.map((sub) => (
                      <div key={sub.id} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 11, color: '#5F5E5A', marginBottom: 3 }}>
                        <span style={{ color: '#A09E97' }}>↳</span>
                        <span style={{ fontWeight: 500 }}>{sub.name}</span>
                        <span style={{ fontSize: 9, padding: '1px 5px', borderRadius: 10, background: sub.mode === 'CODE_DECLARED' ? '#EEEDFE' : '#DFF4EC', color: sub.mode === 'CODE_DECLARED' ? '#3C3489' : '#085041', fontWeight: 600 }}>
                          {sub.mode === 'CODE_DECLARED' ? 'code-declared' : 'auto-discovered'}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </>
        )}

        {/* Observability */}
        <SL>Observability</SL>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 7, marginBottom: 10 }}>
          {[
            [selected.uptime, 'uptime (30d)'],
            [selected.p99, 'p99 latency'],
            selected.dlq != null ? [selected.dlq, 'DLQ messages'] : null,
            selected.eventsPerSec ? [selected.eventsPerSec, 'events published'] : null,
          ].filter(Boolean).map(([val, label]) => (
            <div key={label} style={{ padding: '10px 12px', background: '#F2F0EB', borderRadius: 10 }}>
              <div style={{ fontSize: 19, fontWeight: 700, color: label === 'DLQ messages' && val > 0 ? '#EF9F27' : '#1A1917', lineHeight: 1 }}>{val}</div>
              <div style={{ fontSize: 10, color: '#A09E97', marginTop: 3 }}>{label}</div>
            </div>
          ))}
        </div>
        <div style={{ display: 'flex', gap: 14, flexWrap: 'wrap' }}>
          {[['📊', 'Dashboard', '#'], ['📋', 'Logs', '#'], ['🔗', 'Traces', '#']].map(([ico, lbl, url]) => (
            <a key={lbl} href={url} style={{ fontSize: 11, color: '#2563EB', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: 4 }}>{ico} {lbl}</a>
          ))}
        </div>

        {/* Error Budget */}
        {selected.errorBudget != null && (
          <>
            <SL>Error Budget</SL>
            <div style={{ marginBottom: 4, fontSize: 11, color: '#5F5E5A', display: 'flex', justifyContent: 'space-between' }}>
              <span>Remaining</span><span style={{ fontWeight: 700 }}>{selected.errorBudget}%</span>
            </div>
            <div style={{ height: 8, background: '#E4E1D8', borderRadius: 4, overflow: 'hidden' }}>
              <div style={{ height: '100%', width: `${selected.errorBudget}%`, borderRadius: 4, transition: 'width .4s', background: selected.errorBudget > 50 ? '#52B53A' : selected.errorBudget > 20 ? '#EF9F27' : '#E24B4A' }} />
            </div>
            <div style={{ fontSize: 10, color: '#A09E97', marginTop: 4 }}>SLO target: {selected.sloTarget} · rolling 30 days</div>
          </>
        )}
      </div>

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </aside>
  );
}
