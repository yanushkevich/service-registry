package com.registry.controller;

import com.registry.model.*;
import com.registry.service.RegistryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RegistryController {

    private final RegistryService svc;
    public RegistryController(RegistryService svc) { this.svc = svc; }

    // ── Graph snapshot ────────────────────────────────────────────────────────

    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> graph() {
        return ResponseEntity.ok(svc.getGraphSnapshot());
    }

    // ── Nodes ─────────────────────────────────────────────────────────────────

    @GetMapping("/nodes")
    public ResponseEntity<List<RegistryNode>> nodes() {
        return ResponseEntity.ok(svc.getAllNodes());
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<RegistryNode> node(@PathVariable String id) {
        return svc.getNode(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/nodes")
    public ResponseEntity<RegistryNode> createNode(@RequestBody RegistryNode node) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveNode(node));
    }

    @PutMapping("/nodes/{id}")
    public ResponseEntity<RegistryNode> updateNode(@PathVariable String id, @RequestBody RegistryNode node) {
        if (!svc.getNode(id).isPresent()) return ResponseEntity.notFound().build();
        node.setId(id);
        return ResponseEntity.ok(svc.saveNode(node));
    }

    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable String id) {
        svc.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    // ── Edges ─────────────────────────────────────────────────────────────────

    @GetMapping("/edges")
    public ResponseEntity<List<RegistryEdge>> edges() {
        return ResponseEntity.ok(svc.getAllEdges());
    }

    @GetMapping("/edges/{id}")
    public ResponseEntity<RegistryEdge> edge(@PathVariable String id) {
        return svc.getEdge(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/edges")
    public ResponseEntity<RegistryEdge> createEdge(@RequestBody RegistryEdge edge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveEdge(edge));
    }

    @PutMapping("/edges/{id}")
    public ResponseEntity<RegistryEdge> updateEdge(@PathVariable String id, @RequestBody RegistryEdge edge) {
        if (!svc.getEdge(id).isPresent()) return ResponseEntity.notFound().build();
        edge.setId(id);
        return ResponseEntity.ok(svc.saveEdge(edge));
    }

    @DeleteMapping("/edges/{id}")
    public ResponseEntity<Void> deleteEdge(@PathVariable String id) {
        svc.deleteEdge(id);
        return ResponseEntity.noContent().build();
    }

    // ── Channels ──────────────────────────────────────────────────────────────

    @GetMapping("/nodes/{brokerId}/channels")
    public ResponseEntity<List<BrokerChannel>> channels(@PathVariable String brokerId) {
        return ResponseEntity.ok(svc.getChannelsByBroker(brokerId));
    }

    @PostMapping("/nodes/{brokerId}/channels")
    public ResponseEntity<BrokerChannel> createChannel(@PathVariable String brokerId, @RequestBody BrokerChannel channel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveChannel(brokerId, channel));
    }

    @DeleteMapping("/channels/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable String id) {
        svc.deleteChannel(id);
        return ResponseEntity.noContent().build();
    }

    // ── Behaviours ────────────────────────────────────────────────────────────

    @GetMapping("/nodes/{nodeId}/behaviours")
    public ResponseEntity<List<EmbeddedBehaviour>> behaviours(@PathVariable String nodeId) {
        return ResponseEntity.ok(svc.getBehavioursByNode(nodeId));
    }

    @PostMapping("/nodes/{nodeId}/behaviours")
    public ResponseEntity<EmbeddedBehaviour> createBehaviour(@PathVariable String nodeId, @RequestBody EmbeddedBehaviour b) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveBehaviour(nodeId, b));
    }

    @DeleteMapping("/behaviours/{id}")
    public ResponseEntity<Void> deleteBehaviour(@PathVariable String id) {
        svc.deleteBehaviour(id);
        return ResponseEntity.noContent().build();
    }

    // ── Event search ──────────────────────────────────────────────────────────

    @GetMapping("/events/search")
    public ResponseEntity<List<Map<String, Object>>> searchEvents(@RequestParam String q) {
        return ResponseEntity.ok(svc.searchEvents(q));
    }

    // ── YAML export / import ──────────────────────────────────────────────────

    @GetMapping(value = "/config/yaml", produces = "text/yaml")
    public ResponseEntity<String> exportYaml() {
        try {
            String yaml = svc.exportYaml();
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registry-config.yaml")
                .contentType(MediaType.valueOf("text/yaml"))
                .body(yaml);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("# Export error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/config/yaml", consumes = {"text/yaml", "text/plain", "application/octet-stream"})
    public ResponseEntity<Map<String, Object>> importYaml(@RequestBody String yaml) {
        try {
            svc.importYaml(yaml);
            return ResponseEntity.ok(Map.of("status", "ok", "message", "YAML imported successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // ── Enum metadata (for UI dropdowns) ─────────────────────────────────────

    @GetMapping("/meta/enums")
    public ResponseEntity<Map<String, Object>> enums() {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("nodeKinds",            Arrays.stream(RegistryNode.NodeKind.values()).map(Enum::name).toList());
        meta.put("healthStatuses",       Arrays.stream(RegistryNode.HealthStatus.values()).map(Enum::name).toList());
        meta.put("lifecycleStages",      Arrays.stream(RegistryNode.LifecycleStage.values()).map(Enum::name).toList());
        meta.put("apiProtocols",         Arrays.stream(RegistryNode.ApiProtocol.values()).map(Enum::name).toList());
        meta.put("deploymentPlatforms",  Arrays.stream(RegistryNode.DeploymentPlatform.values()).map(Enum::name).toList());
        meta.put("connectionTypes",      Arrays.stream(RegistryEdge.ConnectionType.values()).map(Enum::name).toList());
        meta.put("interactionPatterns",  Arrays.stream(RegistryEdge.InteractionPattern.values()).map(Enum::name).toList());
        meta.put("edgeRoles",            Arrays.stream(RegistryEdge.EdgeRole.values()).map(Enum::name).toList());
        meta.put("authMechanisms",       Arrays.stream(RegistryEdge.AuthMechanism.values()).map(Enum::name).toList());
        meta.put("channelKinds",         Arrays.stream(BrokerChannel.ChannelKind.values()).map(Enum::name).toList());
        meta.put("registrationModes",    Arrays.stream(BrokerChannel.RegistrationMode.values()).map(Enum::name).toList());
        meta.put("behaviourTypes",       Arrays.stream(EmbeddedBehaviour.BehaviourType.values()).map(Enum::name).toList());
        return ResponseEntity.ok(meta);
    }
}
