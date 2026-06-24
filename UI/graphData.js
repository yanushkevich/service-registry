// ─── Static graph data ────────────────────────────────────────────────────────
export const GRAPH = {
  nodes: [
    {
      id: 'gw-orders', kind: 'API_GATEWAY', name: 'Orders', nameLine2: 'API Gateway',
      domain: 'Orders', sub: 'APIM · REST', health: 'healthy', version: '3.1.0',
      team: 'Platform Eng', repo: 'github/orders-gateway', sla: '99.99%', tech: 'Azure APIM',
      colorRing: '#1D9E75', colorFill: '#D6F0E7', colorText: '#085041',
      authPolicies: ['JWT_VALIDATION', 'IP_ALLOWLIST'], waf: true, cors: true,
      rateLimitRps: 5000, tlsTermination: true,
      routeRules: [{ methods: '*', path: '/orders/**', backend: 'svc-orders' }],
      uptime: '99.99%', p99: '12ms', incidents: 0,
    },
    {
      id: 'svc-orders', kind: 'SERVICE', name: 'Order', nameLine2: 'Service',
      domain: 'Orders', sub: 'REST · public', health: 'healthy', version: '2.4.1',
      team: 'Orders Squad', repo: 'github/order-service', sla: '99.9%',
      tech: 'Java 21 / Spring Boot 3', colorRing: '#7F77DD', colorFill: '#DDDCF8',
      colorText: '#26215C', protocol: 'REST', publicFacing: true, platform: 'Kubernetes',
      ports: { http: 8080, metrics: 9090 }, errorBudget: 72.3, sloTarget: '99.9%',
      uptime: '99.95%', p99: '48ms', incidents: 0,
    },
    {
      id: 'svc-order-dal', kind: 'SERVICE', name: 'Order DAL', nameLine2: 'gRPC server',
      domain: 'Orders', sub: 'gRPC server', health: 'healthy', version: '1.9.0',
      team: 'Platform Eng', repo: 'github/order-dal', sla: '99.9%',
      tech: 'Java 21 / Hibernate', colorRing: '#7F77DD', colorFill: '#DDDCF8',
      colorText: '#26215C', protocol: 'gRPC', publicFacing: false, platform: 'Kubernetes',
      ports: { grpc: 9090, metrics: 9091 },
      behaviours: [{
        id: 'behaviour-outbox-relay', name: 'Outbox Relay',
        type: 'OUTBOX_RELAY', pattern: 'OUTBOX',
        cron: '*/30 * * * * *', schedule: 'every 30 seconds',
        desc: 'Polls outbox table, publishes events to ASB',
        drivenEdges: ['edge-dal-asb'],
      }],
      errorBudget: 91.5, sloTarget: '99.9%',
      uptime: '99.97%', p99: '38ms', dlq: 2, eventsPerSec: '1.2k/s', incidents: 0,
    },
    {
      id: 'db-orders', kind: 'DATABASE', name: 'Orders DB', nameLine2: 'Azure SQL',
      domain: 'Orders', sub: 'Azure SQL', health: 'healthy', version: null,
      team: 'Platform Eng', repo: null, sla: '99.99%',
      tech: 'Azure SQL · General Purpose 4 vCores', colorRing: '#E24B4A',
      colorFill: '#F8DEDE', colorText: '#501313',
      engine: 'AZURE_SQL', region: 'eastus2', classification: 'CONFIDENTIAL',
      shared: false, rpo: 'RPO 1h, RTO 4h',
      uptime: '99.99%', p99: '4ms', incidents: 0,
    },
    {
      id: 'broker-orders-asb', kind: 'MESSAGE_BROKER', name: 'orders-bus',
      nameLine2: 'ASB · Premium', domain: 'Orders', sub: 'ASB · Premium',
      health: 'healthy', version: null, team: 'Platform Eng', repo: null, sla: '99.9%',
      tech: 'Azure Service Bus · Premium · eastus2', colorRing: '#D4880A',
      colorFill: '#FAF0DC', colorText: '#412402',
      brokerType: 'AZURE_SERVICE_BUS', tier: 'Premium', region: 'eastus2',
      schemaRegistry: 'https://schema-registry.example.com',
      autoDiscover: true, discoverCron: '0 */5 * * * *',
      channels: [
        {
          id: 'ch-order-events', name: 'order-events', kind: 'TOPIC',
          eventType: 'OrderPlaced | OrderCancelled | OrderShipped',
          schema: 'Avro', schemaVersion: '2.1.0',
          retention: 'P7D', dlq: 'order-events/$DeadLetterQueue', maxDelivery: 10,
          dlqCount: 2, mode: 'CODE_DECLARED',
          subscriptions: [
            { id: 'ch-notif-sub', name: 'notifications', mode: 'CODE_DECLARED' },
            { id: 'ch-analytics-sub', name: 'analytics', mode: 'AUTO_DISCOVERED' },
          ],
        },
        {
          id: 'ch-dlq', name: 'order-events/$DeadLetterQueue', kind: 'QUEUE',
          dlqCount: 2, mode: 'AUTO_DISCOVERED', subscriptions: [],
        },
      ],
      uptime: '99.98%', incidents: 0,
    },
    {
      id: 'svc-notifications', kind: 'SERVICE', name: 'Notif.', nameLine2: 'Service',
      domain: 'Notifications', sub: 'event consumer', health: 'healthy', version: '1.3.2',
      team: 'Comms Squad', repo: 'github/notification-service', sla: '99.5%',
      tech: 'Node.js 20', colorRing: '#8A8880', colorFill: '#EDECE8', colorText: '#2C2C2A',
      protocol: 'NONE', publicFacing: false, platform: 'Kubernetes',
      uptime: '99.92%', p99: '90ms', incidents: 0,
    },
    {
      id: 'svc-analytics', kind: 'SERVICE', name: 'Analytics', nameLine2: 'Service',
      domain: 'Analytics', sub: 'event consumer', health: 'healthy', version: '0.8.1',
      team: 'Data Squad', repo: 'github/analytics-service', sla: '99%',
      tech: 'Python 3.12 / FastAPI', colorRing: '#8A8880', colorFill: '#EDECE8',
      colorText: '#2C2C2A', protocol: 'NONE', publicFacing: false, platform: 'Kubernetes',
      uptime: '99.85%', p99: '210ms', incidents: 0,
    },
  ],
  edges: [
    { id: 'edge-gw-svc', src: 'gw-orders', tgt: 'svc-orders', type: 'REST', role: 'CALL', pattern: 'REQUEST_RESPONSE', auth: 'JWT', tls: 'MTLS', label: 'REST', style: 'solid', color: '#7F77DD' },
    { id: 'edge-svc-dal', src: 'svc-orders', tgt: 'svc-order-dal', type: 'gRPC', role: 'CALL', pattern: 'REQUEST_RESPONSE', auth: 'MUTUAL_TLS', tls: 'MTLS', label: 'gRPC · mTLS', style: 'solid', color: '#7F77DD', cb: true, timeout: 3000 },
    { id: 'edge-dal-db', src: 'svc-order-dal', tgt: 'db-orders', type: 'JDBC', role: 'READ_WRITE', pattern: 'READ_WRITE', auth: 'MANAGED_IDENTITY', label: 'JDBC', style: 'solid', color: '#E24B4A' },
    { id: 'edge-dal-asb', src: 'svc-order-dal', tgt: 'broker-orders-asb', type: 'AZURE_SERVICE_BUS', role: 'PRODUCE', pattern: 'OUTBOX', auth: 'MANAGED_IDENTITY', channelRef: 'ch-order-events', drivenBy: 'behaviour-outbox-relay', label: 'ASB\nOUTBOX', style: 'dashed', color: '#D4880A' },
    { id: 'edge-asb-notif', src: 'broker-orders-asb', tgt: 'svc-notifications', type: 'AZURE_SERVICE_BUS', role: 'CONSUME', pattern: 'PUBLISH_SUBSCRIBE', channelRef: 'ch-order-events', subRef: 'notifications', label: 'notifications sub', style: 'dashed', color: '#8A8880', crossDomain: true },
    { id: 'edge-asb-analytics', src: 'broker-orders-asb', tgt: 'svc-analytics', type: 'AZURE_SERVICE_BUS', role: 'CONSUME', pattern: 'PUBLISH_SUBSCRIBE', channelRef: 'ch-order-events', subRef: 'analytics', label: 'analytics sub · auto-disc', style: 'dashed', color: '#8A8880', crossDomain: true },
  ],
};

// ─── Event index ──────────────────────────────────────────────────────────────
export function buildEventIndex(graph) {
  const idx = {};
  graph.nodes.filter((n) => n.kind === 'MESSAGE_BROKER').forEach((broker) => {
    (broker.channels || []).forEach((ch) => {
      if (!ch.eventType) return;
      ch.eventType.split('|').forEach((raw) => {
        const evt = raw.trim();
        if (!idx[evt]) idx[evt] = { broker, channel: ch, producers: [], consumers: [] };
        graph.edges
          .filter((e) => e.channelRef === ch.id && e.role === 'PRODUCE')
          .forEach((e) => {
            const src = graph.nodes.find((n) => n.id === e.src);
            if (src) {
              const driven = src.behaviours?.find((b) => b.id === e.drivenBy);
              idx[evt].producers.push({ node: src, edge: e, driven });
            }
          });
        graph.edges
          .filter((e) => e.channelRef === ch.id && e.role === 'CONSUME')
          .forEach((e) => {
            const tgt = graph.nodes.find((n) => n.id === e.tgt);
            if (tgt) idx[evt].consumers.push({ node: tgt, edge: e, sub: e.subRef });
          });
      });
    });
  });
  return idx;
}

// ─── Layout positions ─────────────────────────────────────────────────────────
export const POS = {
  'gw-orders':         { x: 310, y: 105, r: 56 },
  'svc-orders':        { x: 310, y: 255, r: 54 },
  'svc-order-dal':     { x: 310, y: 415, r: 56 },
  'db-orders':         { x: 310, y: 555, rect: true, w: 130, h: 64 },
  'broker-orders-asb': { x: 545, y: 230, rect: true, w: 148, h: 86 },
  'svc-notifications': { x: 175, y: 695, r: 48 },
  'svc-analytics':     { x: 445, y: 695, r: 48 },
};

export function nodeCenter(id) {
  const p = POS[id];
  if (!p) return [0, 0];
  if (p.rect) return [p.x + p.w / 2, p.y + p.h / 2];
  return [p.x, p.y];
}

export function edgePath(edge) {
  const [sx, sy] = nodeCenter(edge.src);
  const [tx, ty] = nodeCenter(edge.tgt);
  const sp = POS[edge.src];
  const tp = POS[edge.tgt];

  if (edge.id === 'edge-dal-asb') {
    const fromY = sp.y - sp.r;
    return {
      d: `M ${sx} ${fromY} C ${sx + 60} ${fromY - 60} ${tp.x + tp.w} ${tp.y + 20} ${tp.x + tp.w} ${tp.y + tp.h / 2}`,
      lx: sx + 95, ly: tp.y + 20,
    };
  }
  if (edge.id === 'edge-asb-notif') {
    return { d: `M ${tp.x + 40} ${tp.y + tp.h} Q ${tp.x - 40} 630 175 ${695 - 48}`, lx: 230, ly: 630 };
  }
  if (edge.id === 'edge-asb-analytics') {
    return { d: `M ${tp.x + tp.w - 40} ${tp.y + tp.h} Q ${tp.x + tp.w + 20} 630 445 ${695 - 48}`, lx: 510, ly: 630 };
  }

  const angle = Math.atan2(ty - sy, tx - sx);
  const sr = sp?.r || 0;
  const tr = tp?.r || 0;
  const fx = sx + Math.cos(angle) * sr;
  const fy = sy + Math.sin(angle) * sr;
  const tx2 = tx - Math.cos(angle) * tr;
  const ty2 = ty - Math.sin(angle) * tr;
  return { d: `M ${fx} ${fy} L ${tx2} ${ty2}`, lx: (fx + tx2) / 2 + 12, ly: (fy + ty2) / 2 };
}

// ─── Constants ────────────────────────────────────────────────────────────────
export const KIND_META = {
  SERVICE:         { dot: '#7F77DD', label: 'Service' },
  API_GATEWAY:     { dot: '#1D9E75', label: 'API Gateway' },
  MESSAGE_BROKER:  { dot: '#D4880A', label: 'Message Broker' },
  DATABASE:        { dot: '#E24B4A', label: 'Database' },
  EXTERNAL_SYSTEM: { dot: '#8A8880', label: 'External' },
};

export const DOMAIN_COLORS = {
  Orders: '#7F77DD',
  Notifications: '#1D9E75',
  Analytics: '#8A8880',
};

export const EDGE_TYPE_COLOR = {
  gRPC: '#534AB7', REST: '#0C447C', AZURE_SERVICE_BUS: '#854F0B', JDBC: '#7A1F1F',
};
export const EDGE_TYPE_BG = {
  gRPC: '#EEEDFE', REST: '#E6F1FB', AZURE_SERVICE_BUS: '#FAEEDA', JDBC: '#FCEBEB',
};
