-- Seed: Orders domain example loaded on startup
-- Nodes
INSERT INTO registry_node (id, kind, name, domain, sub_domain, owning_team, health_status, lifecycle_stage, version, tech_stack, api_protocol, public_facing, deployment_platform, slo_target, color_ring, color_fill, color_text, repository_url, dashboard_url, created_at, updated_at)
VALUES
  ('gw-orders',          'API_GATEWAY',    'Orders API Gateway',  'Orders',       null, 'Platform Eng',  'HEALTHY', 'ACTIVE', '3.1.0',  'Azure APIM',                  'REST',  true,  'MANAGED_SERVICE', 99.99, '#1D9E75', '#D6F0E7', '#085041', 'https://github.com/example/orders-gateway', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('svc-orders',         'SERVICE',        'Order Service',       'Orders',       null, 'Orders Squad',  'HEALTHY', 'ACTIVE', '2.4.1',  'Java 21 / Spring Boot 3',     'REST',  true,  'KUBERNETES',      99.9,  '#7F77DD', '#DDDCF8', '#26215C', 'https://github.com/example/order-service',  null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('svc-order-dal',      'SERVICE',        'Order DAL',           'Orders',       null, 'Platform Eng',  'HEALTHY', 'ACTIVE', '1.9.0',  'Java 21 / Hibernate',         'GRPC',  false, 'KUBERNETES',      99.9,  '#7F77DD', '#DDDCF8', '#26215C', 'https://github.com/example/order-dal',      null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('db-orders',          'DATABASE',       'Orders DB',           'Orders',       null, 'Platform Eng',  'HEALTHY', 'ACTIVE', null,     'Azure SQL · 4 vCores',        'NONE',  false, 'MANAGED_SERVICE', 99.99, '#E24B4A', '#F8DEDE', '#501313', null,                                        null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('broker-orders-asb',  'MESSAGE_BROKER', 'orders-bus',          'Orders',       null, 'Platform Eng',  'HEALTHY', 'ACTIVE', null,     'Azure Service Bus · Premium',  'NONE',  false, 'MANAGED_SERVICE', 99.9,  '#D4880A', '#FAF0DC', '#412402', null,                                        null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('svc-notifications',  'SERVICE',        'Notification Service','Notifications',null, 'Comms Squad',   'HEALTHY', 'ACTIVE', '1.3.2',  'Node.js 20',                  'NONE',  false, 'KUBERNETES',      99.5,  '#8A8880', '#EDECE8', '#2C2C2A', 'https://github.com/example/notification-svc', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('svc-analytics',      'SERVICE',        'Analytics Service',   'Analytics',    null, 'Data Squad',    'HEALTHY', 'ACTIVE', '0.8.1',  'Python 3.12 / FastAPI',       'NONE',  false, 'KUBERNETES',      99.0,  '#8A8880', '#EDECE8', '#2C2C2A', 'https://github.com/example/analytics-svc',  null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Edges
INSERT INTO registry_edge (id, source_node_id, target_node_id, connection_type, interaction_pattern, edge_role, label, auth_mechanism, transport_security, circuit_breaker_enabled, timeout_ms, channel_ref, subscription_ref, driven_by_behaviour_id, cross_domain, edge_style, created_at, updated_at)
VALUES
  ('edge-gw-svc',       'gw-orders',        'svc-orders',        'REST',              'REQUEST_RESPONSE', 'CALL',      'REST',        'JWT',              'MTLS',  false, null, null, null, null, false, 'SOLID',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('edge-svc-dal',      'svc-orders',        'svc-order-dal',     'GRPC',             'REQUEST_RESPONSE', 'CALL',      'gRPC · mTLS', 'MUTUAL_TLS',       'MTLS',  true,  3000, null, null, null, false, 'SOLID',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('edge-dal-db',       'svc-order-dal',     'db-orders',         'JDBC',             'READ_WRITE',       'READ_WRITE','JDBC',        'MANAGED_IDENTITY', 'TLS',   false, null, null, null, null, false, 'SOLID',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('edge-dal-asb',      'svc-order-dal',     'broker-orders-asb', 'AZURE_SERVICE_BUS','OUTBOX',           'PRODUCE',   'ASB OUTBOX',  'MANAGED_IDENTITY', 'TLS',   false, null, 'ch-order-events', null, 'behaviour-outbox-relay', false, 'DASHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('edge-asb-notif',    'broker-orders-asb', 'svc-notifications', 'AZURE_SERVICE_BUS','PUBLISH_SUBSCRIBE','CONSUME',   'notifications sub', 'MANAGED_IDENTITY','TLS', false, null,'ch-order-events','notifications', null, true, 'DASHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('edge-asb-analytics','broker-orders-asb', 'svc-analytics',     'AZURE_SERVICE_BUS','PUBLISH_SUBSCRIBE','CONSUME',   'analytics sub','MANAGED_IDENTITY','TLS',   false, null, 'ch-order-events', 'analytics', null, true, 'DASHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Channels
INSERT INTO broker_channel (id, broker_node_id, name, kind, event_type, schema_format, schema_version, retention_period, dlq_message_count, registration_mode, active_on_broker, created_at)
VALUES
  ('ch-order-events', 'broker-orders-asb', 'order-events',                  'TOPIC',        'OrderPlaced | OrderCancelled | OrderShipped', 'AVRO',         '2.1.0', 'P7D',  2, 'CODE_DECLARED',  true, CURRENT_TIMESTAMP),
  ('ch-notif-sub',    'broker-orders-asb', 'order-events/notifications',     'SUBSCRIPTION', 'OrderPlaced | OrderShipped',                  'AVRO',         '2.1.0', null,   0, 'CODE_DECLARED',  true, CURRENT_TIMESTAMP),
  ('ch-analytics-sub','broker-orders-asb', 'order-events/analytics',         'SUBSCRIPTION', 'OrderPlaced | OrderCancelled | OrderShipped', 'AVRO',         '2.1.0', null,   0, 'AUTO_DISCOVERED',true, CURRENT_TIMESTAMP),
  ('ch-dlq',          'broker-orders-asb', 'order-events/$DeadLetterQueue',  'QUEUE',        null,                                          'PLAIN_JSON',   null,    'P14D', 2, 'AUTO_DISCOVERED',true, CURRENT_TIMESTAMP);

-- Embedded behaviours
INSERT INTO embedded_behaviour (id, service_node_id, name, behaviour_type, pattern, cron_expression, schedule_description, active, notes)
VALUES
  ('behaviour-outbox-relay', 'svc-order-dal', 'Outbox Relay', 'OUTBOX_RELAY', 'OUTBOX', '*/30 * * * * *', 'every 30 seconds', true, 'Polls outbox table and publishes pending events to ASB');
