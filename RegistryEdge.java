package com.registry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "registry_edge")
public class RegistryEdge {

    public enum ConnectionType {
        REST, GRPC, GRAPHQL, SOAP, WEBSOCKET, IN_PROCESS,
        AZURE_SERVICE_BUS, KAFKA, AZURE_EVENT_HUBS, RABBITMQ, AWS_SQS, AWS_SNS,
        JDBC, COSMOS_DB, REDIS, ELASTIC_SEARCH, BLOB_STORAGE,
        SHARED_DATABASE, SHARED_FILESYSTEM, OTHER
    }
    public enum InteractionPattern {
        REQUEST_RESPONSE, ASYNC_FIRE_AND_FORGET, PUBLISH_SUBSCRIBE,
        POINT_TO_POINT, STREAMING, BIDIRECTIONAL_STREAMING,
        POLLING, EVENT_SOURCING, SAGA_ORCHESTRATION, SAGA_CHOREOGRAPHY,
        CHANGE_DATA_CAPTURE, OUTBOX, READ_WRITE
    }
    public enum EdgeRole {
        CALL, PRODUCE, CONSUME, READ, WRITE, READ_WRITE,
        AUTHENTICATE_VIA, STREAM_TO, WEBHOOK_RECEIVE
    }
    public enum AuthMechanism {
        NONE, API_KEY, BASIC_AUTH, OAUTH2_CLIENT_CREDENTIALS, OAUTH2_BEARER,
        JWT, MUTUAL_TLS, MANAGED_IDENTITY, SERVICE_ACCOUNT, SHARED_ACCESS_SIGNATURE
    }
    public enum TransportSecurity { NONE, TLS, MTLS }
    public enum EdgeStyle { SOLID, DASHED, DOTTED, DOUBLE }

    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @NotBlank
    @Column(name = "source_node_id", nullable = false)
    private String sourceNodeId;

    @NotBlank
    @Column(name = "target_node_id", nullable = false)
    private String targetNodeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false, length = 30)
    private ConnectionType connectionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_pattern", length = 30)
    private InteractionPattern interactionPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "edge_role", length = 20)
    private EdgeRole edgeRole;

    private String label;
    private String description;
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_mechanism", length = 30)
    private AuthMechanism authMechanism;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_security", length = 10)
    private TransportSecurity transportSecurity;

    @Column(name = "circuit_breaker_enabled")
    private boolean circuitBreakerEnabled;

    @Column(name = "retry_enabled")
    private boolean retryEnabled;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Column(name = "latency_sla")
    private String latencySla;

    @Column(name = "channel_ref")
    private String channelRef;

    @Column(name = "subscription_ref")
    private String subscriptionRef;

    @Column(name = "driven_by_behaviour_id")
    private String drivenByBehaviourId;

    @Column(name = "cross_domain")
    private boolean crossDomain;

    @Column(name = "optional_edge")
    private boolean optionalEdge;

    @Enumerated(EnumType.STRING)
    @Column(name = "edge_style", length = 10)
    private EdgeStyle edgeStyle = EdgeStyle.SOLID;

    @Column(name = "color_hex", length = 10)
    private String colorHex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    // Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSourceNodeId() { return sourceNodeId; }
    public void setSourceNodeId(String sourceNodeId) { this.sourceNodeId = sourceNodeId; }
    public String getTargetNodeId() { return targetNodeId; }
    public void setTargetNodeId(String targetNodeId) { this.targetNodeId = targetNodeId; }
    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }
    public InteractionPattern getInteractionPattern() { return interactionPattern; }
    public void setInteractionPattern(InteractionPattern interactionPattern) { this.interactionPattern = interactionPattern; }
    public EdgeRole getEdgeRole() { return edgeRole; }
    public void setEdgeRole(EdgeRole edgeRole) { this.edgeRole = edgeRole; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public AuthMechanism getAuthMechanism() { return authMechanism; }
    public void setAuthMechanism(AuthMechanism authMechanism) { this.authMechanism = authMechanism; }
    public TransportSecurity getTransportSecurity() { return transportSecurity; }
    public void setTransportSecurity(TransportSecurity transportSecurity) { this.transportSecurity = transportSecurity; }
    public boolean isCircuitBreakerEnabled() { return circuitBreakerEnabled; }
    public void setCircuitBreakerEnabled(boolean circuitBreakerEnabled) { this.circuitBreakerEnabled = circuitBreakerEnabled; }
    public boolean isRetryEnabled() { return retryEnabled; }
    public void setRetryEnabled(boolean retryEnabled) { this.retryEnabled = retryEnabled; }
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }
    public String getLatencySla() { return latencySla; }
    public void setLatencySla(String latencySla) { this.latencySla = latencySla; }
    public String getChannelRef() { return channelRef; }
    public void setChannelRef(String channelRef) { this.channelRef = channelRef; }
    public String getSubscriptionRef() { return subscriptionRef; }
    public void setSubscriptionRef(String subscriptionRef) { this.subscriptionRef = subscriptionRef; }
    public String getDrivenByBehaviourId() { return drivenByBehaviourId; }
    public void setDrivenByBehaviourId(String drivenByBehaviourId) { this.drivenByBehaviourId = drivenByBehaviourId; }
    public boolean isCrossDomain() { return crossDomain; }
    public void setCrossDomain(boolean crossDomain) { this.crossDomain = crossDomain; }
    public boolean isOptionalEdge() { return optionalEdge; }
    public void setOptionalEdge(boolean optionalEdge) { this.optionalEdge = optionalEdge; }
    public EdgeStyle getEdgeStyle() { return edgeStyle; }
    public void setEdgeStyle(EdgeStyle edgeStyle) { this.edgeStyle = edgeStyle; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
