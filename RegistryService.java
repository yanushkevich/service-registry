package com.registry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.registry.model.*;
import com.registry.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistryService {

    private final NodeRepository nodeRepo;
    private final EdgeRepository edgeRepo;
    private final ChannelRepository channelRepo;
    private final BehaviourRepository behaviourRepo;
    private final ObjectMapper yamlMapper;

    public RegistryService(NodeRepository nodeRepo, EdgeRepository edgeRepo,
                           ChannelRepository channelRepo, BehaviourRepository behaviourRepo) {
        this.nodeRepo      = nodeRepo;
        this.edgeRepo      = edgeRepo;
        this.channelRepo   = channelRepo;
        this.behaviourRepo = behaviourRepo;
        this.yamlMapper    = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.findAndRegisterModules();
    }

    // ── Node CRUD ─────────────────────────────────────────────────────────────

    public List<RegistryNode> getAllNodes() {
        return nodeRepo.findAll();
    }

    public Optional<RegistryNode> getNode(String id) {
        return nodeRepo.findById(id);
    }

    public RegistryNode saveNode(RegistryNode node) {
        if (node.getId() == null || node.getId().isBlank()) {
            node.setId(slugify(node.getName()) + "-" + System.currentTimeMillis());
        }
        node.setUpdatedAt(Instant.now());
        if (node.getCreatedAt() == null) node.setCreatedAt(Instant.now());
        // auto-assign default colours by kind if missing
        if (node.getColorRing() == null) {
            String[] colors = defaultColors(node.getKind());
            node.setColorRing(colors[0]);
            node.setColorFill(colors[1]);
            node.setColorText(colors[2]);
        }
        return nodeRepo.save(node);
    }

    public void deleteNode(String id) {
        edgeRepo.deleteBySourceNodeIdOrTargetNodeId(id, id);
        nodeRepo.deleteById(id);
    }

    // ── Edge CRUD ─────────────────────────────────────────────────────────────

    public List<RegistryEdge> getAllEdges() { return edgeRepo.findAll(); }

    public Optional<RegistryEdge> getEdge(String id) { return edgeRepo.findById(id); }

    public RegistryEdge saveEdge(RegistryEdge edge) {
        if (edge.getId() == null || edge.getId().isBlank()) {
            edge.setId("edge-" + System.currentTimeMillis());
        }
        edge.setUpdatedAt(Instant.now());
        if (edge.getCreatedAt() == null) edge.setCreatedAt(Instant.now());
        // auto-compute cross-domain
        nodeRepo.findById(edge.getSourceNodeId()).ifPresent(src ->
            nodeRepo.findById(edge.getTargetNodeId()).ifPresent(tgt ->
                edge.setCrossDomain(!Objects.equals(src.getDomain(), tgt.getDomain()))
            )
        );
        return edgeRepo.save(edge);
    }

    public void deleteEdge(String id) { edgeRepo.deleteById(id); }

    // ── Channel CRUD ──────────────────────────────────────────────────────────

    public List<BrokerChannel> getChannelsByBroker(String brokerId) {
        return channelRepo.findByBrokerNode_Id(brokerId);
    }

    public BrokerChannel saveChannel(String brokerId, BrokerChannel channel) {
        RegistryNode broker = nodeRepo.findById(brokerId)
            .orElseThrow(() -> new IllegalArgumentException("Broker not found: " + brokerId));
        channel.setBrokerNode(broker);
        if (channel.getId() == null || channel.getId().isBlank()) {
            channel.setId("ch-" + System.currentTimeMillis());
        }
        if (channel.getCreatedAt() == null) channel.setCreatedAt(Instant.now());
        return channelRepo.save(channel);
    }

    public void deleteChannel(String id) { channelRepo.deleteById(id); }

    // ── Behaviour CRUD ────────────────────────────────────────────────────────

    public List<EmbeddedBehaviour> getBehavioursByNode(String nodeId) {
        return behaviourRepo.findByServiceNode_Id(nodeId);
    }

    public EmbeddedBehaviour saveBehaviour(String nodeId, EmbeddedBehaviour b) {
        RegistryNode node = nodeRepo.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        b.setServiceNode(node);
        if (b.getId() == null || b.getId().isBlank()) {
            b.setId("beh-" + System.currentTimeMillis());
        }
        return behaviourRepo.save(b);
    }

    public void deleteBehaviour(String id) { behaviourRepo.deleteById(id); }

    // ── Graph Snapshot (UI payload) ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getGraphSnapshot() {
        List<RegistryNode> nodes = nodeRepo.findAll();
        List<RegistryEdge> edges = edgeRepo.findAll();

        // Enrich nodes with channels and behaviours
        List<Map<String, Object>> nodeList = nodes.stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("kind", n.getKind());
            m.put("name", n.getName());
            m.put("domain", n.getDomain());
            m.put("subDomain", n.getSubDomain());
            m.put("health", n.getHealthStatus());
            m.put("lifecycleStage", n.getLifecycleStage());
            m.put("version", n.getVersion());
            m.put("techStack", n.getTechStack());
            m.put("team", n.getOwningTeam());
            m.put("repo", n.getRepositoryUrl());
            m.put("sla", n.getSloTarget() != null ? n.getSloTarget() + "%" : null);
            m.put("apiProtocol", n.getApiProtocol());
            m.put("publicFacing", n.isPublicFacing());
            m.put("platform", n.getDeploymentPlatform());
            m.put("colorRing", n.getColorRing());
            m.put("colorFill", n.getColorFill());
            m.put("colorText", n.getColorText());
            m.put("errorBudget", n.getErrorBudgetRemaining());
            m.put("dashboardUrl", n.getDashboardUrl());
            m.put("logsUrl", n.getLogsUrl());
            m.put("tracingUrl", n.getTracingUrl());
            m.put("runbookUrl", n.getRunbookUrl());
            m.put("description", n.getDescription());
            m.put("updatedAt", n.getUpdatedAt());
            // channels for broker nodes
            List<BrokerChannel> channels = channelRepo.findByBrokerNode_Id(n.getId());
            if (!channels.isEmpty()) m.put("channels", channels);
            // behaviours for service nodes
            List<EmbeddedBehaviour> beh = behaviourRepo.findByServiceNode_Id(n.getId());
            if (!beh.isEmpty()) m.put("behaviours", beh);
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> edgeList = edges.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("src", e.getSourceNodeId());
            m.put("tgt", e.getTargetNodeId());
            m.put("type", e.getConnectionType());
            m.put("role", e.getEdgeRole());
            m.put("pattern", e.getInteractionPattern());
            m.put("label", e.getLabel());
            m.put("auth", e.getAuthMechanism());
            m.put("tls", e.getTransportSecurity());
            m.put("circuitBreaker", e.isCircuitBreakerEnabled());
            m.put("timeoutMs", e.getTimeoutMs());
            m.put("channelRef", e.getChannelRef());
            m.put("subscriptionRef", e.getSubscriptionRef());
            m.put("drivenBy", e.getDrivenByBehaviourId());
            m.put("crossDomain", e.isCrossDomain());
            m.put("style", e.getEdgeStyle());
            return m;
        }).collect(Collectors.toList());

        List<String> domains = nodeRepo.findAllDomains();

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("nodes", nodeList);
        snapshot.put("edges", edgeList);
        snapshot.put("domains", domains);
        snapshot.put("totalNodes", nodeList.size());
        snapshot.put("totalEdges", edgeList.size());
        snapshot.put("crossDomainEdges", edges.stream().filter(RegistryEdge::isCrossDomain).count());
        return snapshot;
    }

    // ── Event Search ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchEvents(String eventType) {
        List<BrokerChannel> channels = channelRepo.findByEventTypeContaining(eventType);
        List<Map<String, Object>> results = new ArrayList<>();

        for (BrokerChannel ch : channels) {
            // only match actual event type parts
            if (ch.getEventType() == null) continue;
            boolean matches = Arrays.stream(ch.getEventType().split("\\|"))
                .map(String::trim)
                .anyMatch(e -> e.toLowerCase().contains(eventType.toLowerCase()));
            if (!matches) continue;

            List<RegistryEdge> chEdges = edgeRepo.findByChannelRef(ch.getId());

            List<Map<String, Object>> producers = chEdges.stream()
                .filter(e -> e.getEdgeRole() == RegistryEdge.EdgeRole.PRODUCE)
                .map(e -> {
                    Map<String, Object> p = new LinkedHashMap<>();
                    nodeRepo.findById(e.getSourceNodeId()).ifPresent(n -> {
                        p.put("nodeId", n.getId());
                        p.put("nodeName", n.getName());
                        p.put("domain", n.getDomain());
                        p.put("team", n.getOwningTeam());
                        p.put("health", n.getHealthStatus());
                        p.put("drivenByBehaviourId", e.getDrivenByBehaviourId());
                        if (e.getDrivenByBehaviourId() != null) {
                            behaviourRepo.findById(e.getDrivenByBehaviourId())
                                .ifPresent(b -> p.put("drivenByBehaviour", b.getName() + " (" + b.getScheduleDescription() + ")"));
                        }
                    });
                    p.put("channelName", ch.getName());
                    p.put("edgeId", e.getId());
                    return p;
                })
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());

            List<Map<String, Object>> consumers = chEdges.stream()
                .filter(e -> e.getEdgeRole() == RegistryEdge.EdgeRole.CONSUME)
                .map(e -> {
                    Map<String, Object> c = new LinkedHashMap<>();
                    nodeRepo.findById(e.getTargetNodeId()).ifPresent(n -> {
                        c.put("nodeId", n.getId());
                        c.put("nodeName", n.getName());
                        c.put("domain", n.getDomain());
                        c.put("team", n.getOwningTeam());
                        c.put("health", n.getHealthStatus());
                    });
                    c.put("channelName", ch.getName());
                    c.put("subscriptionRef", e.getSubscriptionRef());
                    c.put("edgeId", e.getId());
                    return c;
                })
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("eventType", eventType);
            result.put("channelId", ch.getId());
            result.put("channelName", ch.getName());
            result.put("channelKind", ch.getKind());
            result.put("brokerNodeId", ch.getBrokerNode().getId());
            result.put("brokerNodeName", ch.getBrokerNode().getName());
            result.put("schemaFormat", ch.getSchemaFormat());
            result.put("schemaVersion", ch.getSchemaVersion());
            result.put("producers", producers);
            result.put("consumers", consumers);
            results.add(result);
        }
        return results;
    }

    // ── YAML Export / Import ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public String exportYaml() throws Exception {
        Map<String, Object> export = new LinkedHashMap<>();
        export.put("apiVersion", "registry/v1");
        export.put("kind", "ServiceRegistryConfig");
        export.put("metadata", Map.of("exportedAt", Instant.now().toString()));

        export.put("nodes", nodeRepo.findAll().stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("kind", n.getKind());
            m.put("name", n.getName());
            m.put("domain", n.getDomain());
            if (n.getSubDomain() != null)      m.put("subDomain", n.getSubDomain());
            if (n.getOwningTeam() != null)     m.put("owningTeam", n.getOwningTeam());
            if (n.getVersion() != null)        m.put("version", n.getVersion());
            if (n.getTechStack() != null)      m.put("techStack", n.getTechStack());
            if (n.getApiProtocol() != null)    m.put("apiProtocol", n.getApiProtocol());
            m.put("publicFacing", n.isPublicFacing());
            if (n.getDeploymentPlatform() != null) m.put("deploymentPlatform", n.getDeploymentPlatform());
            if (n.getRepositoryUrl() != null)  m.put("repositoryUrl", n.getRepositoryUrl());
            if (n.getSloTarget() != null)      m.put("sloTarget", n.getSloTarget());
            m.put("healthStatus", n.getHealthStatus());
            m.put("lifecycleStage", n.getLifecycleStage());
            if (n.getColorRing() != null)      m.put("colorRing", n.getColorRing());
            if (n.getColorFill() != null)      m.put("colorFill", n.getColorFill());
            if (n.getColorText() != null)      m.put("colorText", n.getColorText());

            List<BrokerChannel> channels = channelRepo.findByBrokerNode_Id(n.getId());
            if (!channels.isEmpty()) m.put("channels", channels.stream().map(c -> {
                Map<String, Object> cm = new LinkedHashMap<>();
                cm.put("id", c.getId()); cm.put("name", c.getName());
                cm.put("kind", c.getKind());
                if (c.getEventType() != null) cm.put("eventType", c.getEventType());
                if (c.getSchemaFormat() != null) cm.put("schemaFormat", c.getSchemaFormat());
                if (c.getSchemaVersion() != null) cm.put("schemaVersion", c.getSchemaVersion());
                if (c.getRetentionPeriod() != null) cm.put("retentionPeriod", c.getRetentionPeriod());
                cm.put("registrationMode", c.getRegistrationMode());
                return cm;
            }).collect(Collectors.toList()));

            List<EmbeddedBehaviour> beh = behaviourRepo.findByServiceNode_Id(n.getId());
            if (!beh.isEmpty()) m.put("behaviours", beh.stream().map(b -> {
                Map<String, Object> bm = new LinkedHashMap<>();
                bm.put("id", b.getId()); bm.put("name", b.getName());
                bm.put("type", b.getBehaviourType()); bm.put("pattern", b.getPattern());
                if (b.getCronExpression() != null) bm.put("cronExpression", b.getCronExpression());
                if (b.getScheduleDescription() != null) bm.put("scheduleDescription", b.getScheduleDescription());
                return bm;
            }).collect(Collectors.toList()));

            return m;
        }).collect(Collectors.toList()));

        export.put("edges", edgeRepo.findAll().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("sourceNodeId", e.getSourceNodeId());
            m.put("targetNodeId", e.getTargetNodeId());
            m.put("connectionType", e.getConnectionType());
            if (e.getInteractionPattern() != null) m.put("interactionPattern", e.getInteractionPattern());
            if (e.getEdgeRole() != null)           m.put("edgeRole", e.getEdgeRole());
            if (e.getLabel() != null)              m.put("label", e.getLabel());
            if (e.getAuthMechanism() != null)      m.put("authMechanism", e.getAuthMechanism());
            if (e.getTransportSecurity() != null)  m.put("transportSecurity", e.getTransportSecurity());
            m.put("circuitBreakerEnabled", e.isCircuitBreakerEnabled());
            if (e.getTimeoutMs() != null)          m.put("timeoutMs", e.getTimeoutMs());
            if (e.getChannelRef() != null)         m.put("channelRef", e.getChannelRef());
            if (e.getSubscriptionRef() != null)    m.put("subscriptionRef", e.getSubscriptionRef());
            if (e.getDrivenByBehaviourId() != null) m.put("drivenByBehaviourId", e.getDrivenByBehaviourId());
            m.put("crossDomain", e.isCrossDomain());
            return m;
        }).collect(Collectors.toList()));

        return yamlMapper.writeValueAsString(export);
    }

    @SuppressWarnings("unchecked")
    public void importYaml(String yaml) throws Exception {
        Map<String, Object> doc = yamlMapper.readValue(yaml, Map.class);
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) doc.getOrDefault("nodes", List.of());
        List<Map<String, Object>> edges = (List<Map<String, Object>>) doc.getOrDefault("edges", List.of());

        for (Map<String, Object> nm : nodes) {
            RegistryNode n = new RegistryNode();
            n.setId((String) nm.get("id"));
            n.setName((String) nm.get("name"));
            n.setDomain((String) nm.getOrDefault("domain", "Unknown"));
            n.setKind(RegistryNode.NodeKind.valueOf((String) nm.getOrDefault("kind", "SERVICE")));
            n.setOwningTeam((String) nm.get("owningTeam"));
            n.setVersion((String) nm.get("version"));
            n.setTechStack((String) nm.get("techStack"));
            n.setRepositoryUrl((String) nm.get("repositoryUrl"));
            if (nm.get("sloTarget") != null) n.setSloTarget(((Number) nm.get("sloTarget")).doubleValue());
            if (nm.get("apiProtocol") != null) n.setApiProtocol(RegistryNode.ApiProtocol.valueOf((String) nm.get("apiProtocol")));
            if (nm.get("deploymentPlatform") != null) n.setDeploymentPlatform(RegistryNode.DeploymentPlatform.valueOf((String) nm.get("deploymentPlatform")));
            if (nm.get("healthStatus") != null) n.setHealthStatus(RegistryNode.HealthStatus.valueOf((String) nm.get("healthStatus")));
            if (nm.get("lifecycleStage") != null) n.setLifecycleStage(RegistryNode.LifecycleStage.valueOf((String) nm.get("lifecycleStage")));
            n.setColorRing((String) nm.get("colorRing"));
            n.setColorFill((String) nm.get("colorFill"));
            n.setColorText((String) nm.get("colorText"));
            saveNode(n);

            // channels
            List<Map<String, Object>> channels = (List<Map<String, Object>>) nm.getOrDefault("channels", List.of());
            for (Map<String, Object> cm : channels) {
                BrokerChannel ch = new BrokerChannel();
                ch.setId((String) cm.get("id"));
                ch.setName((String) cm.get("name"));
                ch.setKind(BrokerChannel.ChannelKind.valueOf((String) cm.getOrDefault("kind", "TOPIC")));
                ch.setEventType((String) cm.get("eventType"));
                if (cm.get("schemaFormat") != null) ch.setSchemaFormat(BrokerChannel.SchemaFormat.valueOf((String) cm.get("schemaFormat")));
                ch.setSchemaVersion((String) cm.get("schemaVersion"));
                ch.setRetentionPeriod((String) cm.get("retentionPeriod"));
                if (cm.get("registrationMode") != null) ch.setRegistrationMode(BrokerChannel.RegistrationMode.valueOf((String) cm.get("registrationMode")));
                saveChannel(n.getId(), ch);
            }
        }

        for (Map<String, Object> em : edges) {
            RegistryEdge e = new RegistryEdge();
            e.setId((String) em.get("id"));
            e.setSourceNodeId((String) em.get("sourceNodeId"));
            e.setTargetNodeId((String) em.get("targetNodeId"));
            e.setConnectionType(RegistryEdge.ConnectionType.valueOf((String) em.get("connectionType")));
            if (em.get("interactionPattern") != null) e.setInteractionPattern(RegistryEdge.InteractionPattern.valueOf((String) em.get("interactionPattern")));
            if (em.get("edgeRole") != null) e.setEdgeRole(RegistryEdge.EdgeRole.valueOf((String) em.get("edgeRole")));
            e.setLabel((String) em.get("label"));
            if (em.get("authMechanism") != null) e.setAuthMechanism(RegistryEdge.AuthMechanism.valueOf((String) em.get("authMechanism")));
            if (em.get("transportSecurity") != null) e.setTransportSecurity(RegistryEdge.TransportSecurity.valueOf((String) em.get("transportSecurity")));
            e.setCircuitBreakerEnabled(Boolean.TRUE.equals(em.get("circuitBreakerEnabled")));
            if (em.get("timeoutMs") != null) e.setTimeoutMs((Integer) em.get("timeoutMs"));
            e.setChannelRef((String) em.get("channelRef"));
            e.setSubscriptionRef((String) em.get("subscriptionRef"));
            e.setDrivenByBehaviourId((String) em.get("drivenByBehaviourId"));
            saveEdge(e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String slugify(String name) {
        return name == null ? "node" : name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private String[] defaultColors(RegistryNode.NodeKind kind) {
        if (kind == null) return new String[]{"#7F77DD", "#DDDCF8", "#26215C"};
        return switch (kind) {
            case API_GATEWAY    -> new String[]{"#1D9E75", "#D6F0E7", "#085041"};
            case MESSAGE_BROKER -> new String[]{"#D4880A", "#FAF0DC", "#412402"};
            case DATABASE       -> new String[]{"#E24B4A", "#F8DEDE", "#501313"};
            case EXTERNAL_SYSTEM-> new String[]{"#8A8880", "#EDECE8", "#2C2C2A"};
            default             -> new String[]{"#7F77DD", "#DDDCF8", "#26215C"};
        };
    }
}
