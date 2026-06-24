'use client';
import { NodeIcon, IconClock } from './Icons';
import { POS, edgePath } from '../lib/graphData';

const FONT = "-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif";

function EdgeLabels() {
  return (
    <>
      <text x="338" y="196" fill="#7F77DD" fontSize="11" fontFamily={FONT} fontWeight="500">REST</text>
      <text x="338" y="348" fill="#7F77DD" fontSize="11" fontFamily={FONT} fontWeight="500">gRPC · mTLS</text>
      <text x="325" y="506" fill="#E24B4A" fontSize="11" fontFamily={FONT} fontWeight="500">JDBC</text>
      <text x="498" y="370" fill="#D4880A" fontSize="10.5" fontFamily={FONT} fontWeight="500">ASB</text>
      <text x="498" y="384" fill="#D4880A" fontSize="10.5" fontFamily={FONT} fontWeight="500">OUTBOX</text>
    </>
  );
}

function Legend() {
  const items = [
    { col: '#7F77DD', dash: false, label: 'REST / gRPC (sync)' },
    { col: '#D4880A', dash: true, label: 'ASB publish (outbox)' },
    { col: '#8A8880', dash: true, label: 'Cross-domain consume' },
  ];
  return (
    <g transform="translate(68,596)">
      {items.map((l, i) => (
        <g key={l.label} transform={`translate(0,${i * 18})`}>
          <line x1={0} y1={7} x2={28} y2={7} stroke={l.col} strokeWidth={l.dash ? 1.2 : 2} strokeDasharray={l.dash ? '6 3' : 'none'} />
          <text x={34} y={11} fill="#7A7872" fontSize={11} fontFamily={FONT}>{l.label}</text>
        </g>
      ))}
    </g>
  );
}

export default function GraphMap({ nodes, edges, visibleNodes, selected, setSelected, hoveredEdge, setHoveredEdge, hoveredNode, setHoveredNode, highlightedEdges }) {
  return (
    <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
      <svg width="100%" height="100%" viewBox="0 0 710 810" style={{ display: 'block' }}>
        <defs>
          {[['arr-v', '#7F77DD'], ['arr-db', '#E24B4A'], ['arr-asb', '#D4880A'], ['arr-gray', '#8A8880']].map(([id, col]) => (
            <marker key={id} id={id} viewBox="0 0 10 10" refX="8" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
              <path d="M1.5 1.5L8 5L1.5 8.5" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" stroke={col} />
            </marker>
          ))}
          <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
            <feDropShadow dx="0" dy="2" stdDeviation="4" floodColor="#1A1917" floodOpacity="0.10" />
          </filter>
        </defs>

        {/* Domain boxes */}
        <rect x="60" y="32" width="590" height="590" rx="14" fill="none" stroke="#C5C2B8" strokeWidth="0.75" strokeDasharray="7 5" />
        <text x="76" y="52" fill="#A09E97" fontSize="12" fontFamily={FONT}>Orders domain</text>
        <rect x="60" y="636" width="590" height="136" rx="14" fill="none" stroke="#C5C2B8" strokeWidth="0.75" strokeDasharray="7 5" />
        <text x="76" y="655" fill="#A09E97" fontSize="12" fontFamily={FONT}>Cross-domain consumers</text>

        {/* Edges */}
        {edges.map((edge) => {
          const ep = edgePath(edge);
          if (!ep) return null;
          const isHov = hoveredEdge === edge.id;
          const isDim = highlightedEdges.size > 0 && !highlightedEdges.has(edge.id) && !isHov;
          const markId = edge.color === '#7F77DD' ? 'arr-v' : edge.color === '#E24B4A' ? 'arr-db' : edge.color === '#D4880A' ? 'arr-asb' : 'arr-gray';
          return (
            <g key={edge.id}
              onMouseEnter={() => setHoveredEdge(edge.id)}
              onMouseLeave={() => setHoveredEdge(null)}>
              <path d={ep.d} fill="none" stroke="transparent" strokeWidth={14} style={{ cursor: 'pointer' }} />
              <path d={ep.d} fill="none"
                stroke={edge.color} strokeWidth={isHov ? 2.4 : 1.5}
                strokeDasharray={edge.style === 'dashed' ? '7 4' : 'none'}
                markerEnd={`url(#${markId})`}
                opacity={isDim ? 0.2 : 1}
                style={{ transition: 'opacity .2s, stroke-width .15s' }} />
              {(isHov || highlightedEdges.has(edge.id)) && ep.lx && (
                edge.label?.includes('\n') ? (
                  <g>
                    <rect x={ep.lx - 22} y={ep.ly - 20} width={52} height={34} rx={6} fill="#1A1917" opacity={0.82} />
                    {edge.label.split('\n').map((ln, i) => (
                      <text key={i} x={ep.lx + 4} y={ep.ly - 6 + i * 14} textAnchor="middle" fill="#FAF9F7" fontSize={10} fontFamily={FONT}>{ln}</text>
                    ))}
                  </g>
                ) : (
                  <g>
                    <rect x={ep.lx - (edge.label?.length || 0) * 3.3} y={ep.ly - 13} width={(edge.label?.length || 0) * 6.6 + 8} height={20} rx={6} fill="#1A1917" opacity={0.82} />
                    <text x={ep.lx + 4} y={ep.ly + 1} textAnchor="middle" fill="#FAF9F7" fontSize={10} fontFamily={FONT}>{edge.label}</text>
                  </g>
                )
              )}
            </g>
          );
        })}

        <EdgeLabels />

        {/* Nodes */}
        {nodes.map((node) => {
          if (!visibleNodes.includes(node)) return null;
          const pos = POS[node.id];
          if (!pos) return null;
          const isSel = selected?.id === node.id;
          const isHov = hoveredNode === node.id;
          const isDim = highlightedEdges.size > 0
            && !highlightedEdges.has(edges.find((e) => e.src === node.id || e.tgt === node.id)?.id || '__')
            && (hoveredNode || selected?.id) !== node.id && !isSel;

          if (pos.rect) {
            const cx = pos.x + pos.w / 2;
            return (
              <g key={node.id}
                onClick={() => setSelected(node)}
                onMouseEnter={() => setHoveredNode(node.id)}
                onMouseLeave={() => setHoveredNode(null)}
                style={{ cursor: 'pointer' }} opacity={isDim ? 0.3 : 1}>
                {isSel && <rect x={pos.x - 5} y={pos.y - 5} width={pos.w + 10} height={pos.h + 10} rx={15} fill="none" stroke={node.colorRing} strokeWidth={1} strokeDasharray="5 3" />}
                <rect x={pos.x} y={pos.y} width={pos.w} height={pos.h} rx={10}
                  fill={node.colorFill} stroke={isSel || isHov ? node.colorRing : '#D4C5A0'} strokeWidth={isSel ? 2 : 1}
                  filter={isSel ? 'url(#shadow)' : 'none'} style={{ transition: 'filter .2s' }} />
                <foreignObject x={pos.x + 8} y={pos.y + 8} width={22} height={22}>
                  <div xmlns="http://www.w3.org/1999/xhtml" style={{ width: 22, height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <NodeIcon kind={node.kind} size={18} color={node.colorText} />
                  </div>
                </foreignObject>
                <text x={cx} y={pos.y + 22} textAnchor="middle" fill={node.colorText} fontSize={12.5} fontWeight={700} fontFamily={FONT}>{node.name}</text>
                <text x={cx} y={pos.y + 37} textAnchor="middle" fill={node.colorText} fontSize={10.5} fontFamily={FONT} opacity={0.75}>{node.nameLine2 || node.sub}</text>
                {node.channels?.[0] && (
                  <>
                    <rect x={pos.x + 8} y={pos.y + pos.h - 24} width={pos.w - 16} height={19} rx={5} fill="none" stroke={node.colorRing} strokeWidth={0.75} />
                    <text x={cx} y={pos.y + pos.h - 11} textAnchor="middle" fill={node.colorText} fontSize={9.5} fontFamily={FONT}>{node.channels[0].name} [{node.channels[0].kind}]</text>
                  </>
                )}
                <circle cx={pos.x + pos.w - 8} cy={pos.y + 8} r={5} fill={node.health === 'healthy' ? '#52B53A' : '#EF9F27'} stroke="#fff" strokeWidth={1.2} />
              </g>
            );
          }

          const hasBeh = node.behaviours?.length > 0;
          return (
            <g key={node.id}
              onClick={() => setSelected(node)}
              onMouseEnter={() => setHoveredNode(node.id)}
              onMouseLeave={() => setHoveredNode(null)}
              style={{ cursor: 'pointer' }} opacity={isDim ? 0.3 : 1}>
              {(isSel || isHov) && <circle cx={pos.x} cy={pos.y} r={pos.r + 8} fill={node.colorFill} opacity={0.45} />}
              {isSel && <circle cx={pos.x} cy={pos.y} r={pos.r + 14} fill="none" stroke={node.colorRing} strokeWidth={1} strokeDasharray="5 3" />}
              <circle cx={pos.x} cy={pos.y} r={pos.r}
                fill={node.colorFill} stroke={node.colorRing}
                strokeWidth={isSel ? 2.5 : isHov ? 2 : 1.8}
                filter={isSel ? 'url(#shadow)' : 'none'}
                style={{ transition: 'stroke-width .15s, filter .2s' }} />
              <foreignObject x={pos.x - 13} y={pos.y - pos.r + 10} width={26} height={26}>
                <div xmlns="http://www.w3.org/1999/xhtml" style={{ width: 26, height: 26, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <NodeIcon kind={node.kind} size={22} color={node.colorText} />
                </div>
              </foreignObject>
              <text x={pos.x} y={pos.y + (hasBeh ? -8 : -4)} textAnchor="middle" fill={node.colorText} fontSize={13} fontWeight={700} fontFamily={FONT}>{node.name}</text>
              <text x={pos.x} y={pos.y + (hasBeh ? 7 : 11)} textAnchor="middle" fill={node.colorText} fontSize={11} fontFamily={FONT} opacity={0.8}>{node.nameLine2}</text>
              {hasBeh && (
                <g>
                  <rect x={pos.x - 46} y={pos.y + 18} width={92} height={20} rx={10} fill={node.colorRing} opacity={0.9} />
                  <foreignObject x={pos.x - 42} y={pos.y + 21} width={14} height={14}>
                    <div xmlns="http://www.w3.org/1999/xhtml" style={{ width: 14, height: 14, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <IconClock size={11} color="#fff" />
                    </div>
                  </foreignObject>
                  <text x={pos.x + 3} y={pos.y + 31} textAnchor="middle" fill="#fff" fontSize={9.5} fontFamily={FONT} fontWeight={500}> outbox relay</text>
                </g>
              )}
              <circle cx={pos.x + pos.r - 5} cy={pos.y - pos.r + 5} r={5.5}
                fill={node.health === 'healthy' ? '#52B53A' : node.health === 'degraded' ? '#EF9F27' : '#E24B4A'}
                stroke="#fff" strokeWidth={1.5} />
            </g>
          );
        })}

        <Legend />
      </svg>
    </div>
  );
}
