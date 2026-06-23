package com.registry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "registry_node")
public class RegistryNode {

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum NodeKind { SERVICE, API_GATEWAY, MESSAGE_BROKER, DATABASE, EXTERNAL_SYSTEM }
    public enum LifecycleStage { EXPERIMENTAL, ACTIVE, MAINTENANCE, DEPRECATED, RETIRED }
    public enum HealthStatus { HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN }
    public enum ApiProtocol { REST, GRPC, GRAPHQL, SOAP, WEBSOCKET, NONE }
    public enum DeploymentPlatform {
        KUBERNETES, AZURE_CONTAINER_APPS, AWS_ECS, AWS_LAMBDA,
        AZURE_FUNCTIONS, GCP_CLOUD_RUN, MANAGED_SERVICE, ON_PREMISE, UNKNOWN
    }

    // ── Identity ──────────────────────────────────────────────────────────────

    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NodeKind kind;

    @NotBlank
    @Column(nullable = false)
    private String domain;

    @Column(name = "sub_domain")
    private String subDomain;

    // ── Ownership ─────────────────────────────────────────────────────────────

    @Column(name = "owning_team")
    private String owningTeam;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "escalation_policy")
    private String escalationPolicy;

    // ── Lifecycle & Health ────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_stage", length = 20)
    private LifecycleStage lifecycleStage = LifecycleStage.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", length = 20)
    private HealthStatus healthStatus = HealthStatus.UNKNOWN;

    @Column(name = "health_check_url")
    private String healthCheckUrl;

    // ── Version & Deployment ──────────────────────────────────────────────────

    private String version;

    @Column(name = "tech_stack")
    private String techStack;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_protocol", length = 20)
    private ApiProtocol apiProtocol = ApiProtocol.NONE;

    @Column(name = "public_facing")
    private boolean publicFacing;

    @Enumerated(EnumType.STRING)
    @Column(name = "deployment_platform", length = 30)
    private DeploymentPlatform deploymentPlatform;

    @Column(name = "deployment_namespace")
    private String deploymentNamespace;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Column(name = "pipeline_url")
    private String pipelineUrl;

    @Column(name = "docker_image")
    private String dockerImage;

    @Column(name = "api_spec_url")
    private String apiSpecUrl;

    @Column(name = "base_endpoint")
    private String baseEndpoint;

    // ── SLO ───────────────────────────────────────────────────────────────────

    @Column(name = "slo_target")
    private Double sloTarget;

    @Column(name = "slo_window")
    private String sloWindow;

    @Column(name = "error_budget_remaining")
    private Double errorBudgetRemaining;

    // ── Observability ─────────────────────────────────────────────────────────

    @Column(name = "dashboard_url")
    private String dashboardUrl;

    @Column(name = "logs_url")
    private String logsUrl;

    @Column(name = "tracing_url")
    private String tracingUrl;

    @Column(name = "runbook_url")
    private String runbookUrl;

    // ── UI ────────────────────────────────────────────────────────────────────

    @Column(name = "color_ring", length = 10)
    private String colorRing;

    @Column(name = "color_fill", length = 10)
    private String colorFill;

    @Column(name = "color_text", length = 10)
    private String colorText;

    @Column(name = "layout_x")
    private Double layoutX;

    @Column(name = "layout_y")
    private Double layoutY;

    // ── Audit ─────────────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    // ── Relations ─────────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "brokerNode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BrokerChannel> channels = new ArrayList<>();

    @OneToMany(mappedBy = "serviceNode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EmbeddedBehaviour> behaviours = new ArrayList<>();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public NodeKind getKind() { return kind; }
    public void setKind(NodeKind kind) { this.kind = kind; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getSubDomain() { return subDomain; }
    public void setSubDomain(String subDomain) { this.subDomain = subDomain; }
    public String getOwningTeam() { return owningTeam; }
    public void setOwningTeam(String owningTeam) { this.owningTeam = owningTeam; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getEscalationPolicy() { return escalationPolicy; }
    public void setEscalationPolicy(String escalationPolicy) { this.escalationPolicy = escalationPolicy; }
    public LifecycleStage getLifecycleStage() { return lifecycleStage; }
    public void setLifecycleStage(LifecycleStage lifecycleStage) { this.lifecycleStage = lifecycleStage; }
    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
    public String getHealthCheckUrl() { return healthCheckUrl; }
    public void setHealthCheckUrl(String healthCheckUrl) { this.healthCheckUrl = healthCheckUrl; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public ApiProtocol getApiProtocol() { return apiProtocol; }
    public void setApiProtocol(ApiProtocol apiProtocol) { this.apiProtocol = apiProtocol; }
    public boolean isPublicFacing() { return publicFacing; }
    public void setPublicFacing(boolean publicFacing) { this.publicFacing = publicFacing; }
    public DeploymentPlatform getDeploymentPlatform() { return deploymentPlatform; }
    public void setDeploymentPlatform(DeploymentPlatform deploymentPlatform) { this.deploymentPlatform = deploymentPlatform; }
    public String getDeploymentNamespace() { return deploymentNamespace; }
    public void setDeploymentNamespace(String deploymentNamespace) { this.deploymentNamespace = deploymentNamespace; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
    public String getPipelineUrl() { return pipelineUrl; }
    public void setPipelineUrl(String pipelineUrl) { this.pipelineUrl = pipelineUrl; }
    public String getDockerImage() { return dockerImage; }
    public void setDockerImage(String dockerImage) { this.dockerImage = dockerImage; }
    public String getApiSpecUrl() { return apiSpecUrl; }
    public void setApiSpecUrl(String apiSpecUrl) { this.apiSpecUrl = apiSpecUrl; }
    public String getBaseEndpoint() { return baseEndpoint; }
    public void setBaseEndpoint(String baseEndpoint) { this.baseEndpoint = baseEndpoint; }
    public Double getSloTarget() { return sloTarget; }
    public void setSloTarget(Double sloTarget) { this.sloTarget = sloTarget; }
    public String getSloWindow() { return sloWindow; }
    public void setSloWindow(String sloWindow) { this.sloWindow = sloWindow; }
    public Double getErrorBudgetRemaining() { return errorBudgetRemaining; }
    public void setErrorBudgetRemaining(Double errorBudgetRemaining) { this.errorBudgetRemaining = errorBudgetRemaining; }
    public String getDashboardUrl() { return dashboardUrl; }
    public void setDashboardUrl(String dashboardUrl) { this.dashboardUrl = dashboardUrl; }
    public String getLogsUrl() { return logsUrl; }
    public void setLogsUrl(String logsUrl) { this.logsUrl = logsUrl; }
    public String getTracingUrl() { return tracingUrl; }
    public void setTracingUrl(String tracingUrl) { this.tracingUrl = tracingUrl; }
    public String getRunbookUrl() { return runbookUrl; }
    public void setRunbookUrl(String runbookUrl) { this.runbookUrl = runbookUrl; }
    public String getColorRing() { return colorRing; }
    public void setColorRing(String colorRing) { this.colorRing = colorRing; }
    public String getColorFill() { return colorFill; }
    public void setColorFill(String colorFill) { this.colorFill = colorFill; }
    public String getColorText() { return colorText; }
    public void setColorText(String colorText) { this.colorText = colorText; }
    public Double getLayoutX() { return layoutX; }
    public void setLayoutX(Double layoutX) { this.layoutX = layoutX; }
    public Double getLayoutY() { return layoutY; }
    public void setLayoutY(Double layoutY) { this.layoutY = layoutY; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public List<BrokerChannel> getChannels() { return channels; }
    public void setChannels(List<BrokerChannel> channels) { this.channels = channels; }
    public List<EmbeddedBehaviour> getBehaviours() { return behaviours; }
    public void setBehaviours(List<EmbeddedBehaviour> behaviours) { this.behaviours = behaviours; }
}
