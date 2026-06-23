package com.registry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "embedded_behaviour")
public class EmbeddedBehaviour {

    public enum BehaviourType {
        CRON_JOB, BACKGROUND_WORKER, POLLING_CONSUMER,
        OUTBOX_RELAY, SAGA_ORCHESTRATOR, CACHE_WARMER, HEALTH_PROBE, OTHER
    }
    public enum BehaviourPattern {
        OUTBOX, SAGA, POLLING, CHANGE_DATA_CAPTURE, INBOX, NONE
    }

    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_node_id", nullable = false)
    private RegistryNode serviceNode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "behaviour_type", length = 25)
    private BehaviourType behaviourType;

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern", length = 25)
    private BehaviourPattern pattern;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "schedule_description")
    private String scheduleDescription;

    private boolean active = true;
    private String notes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public RegistryNode getServiceNode() { return serviceNode; }
    public void setServiceNode(RegistryNode serviceNode) { this.serviceNode = serviceNode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BehaviourType getBehaviourType() { return behaviourType; }
    public void setBehaviourType(BehaviourType behaviourType) { this.behaviourType = behaviourType; }
    public BehaviourPattern getPattern() { return pattern; }
    public void setPattern(BehaviourPattern pattern) { this.pattern = pattern; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public String getScheduleDescription() { return scheduleDescription; }
    public void setScheduleDescription(String scheduleDescription) { this.scheduleDescription = scheduleDescription; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
