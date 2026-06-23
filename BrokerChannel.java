package com.registry.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "broker_channel")
public class BrokerChannel {

    public enum ChannelKind { TOPIC, QUEUE, SUBSCRIPTION }
    public enum SchemaFormat { AVRO, JSON_SCHEMA, PROTOBUF, XML_SCHEMA, PLAIN_JSON, NONE }
    public enum RegistrationMode { MANUAL, AUTO_DISCOVERED, CODE_DECLARED }

    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_node_id", nullable = false)
    private RegistryNode brokerNode;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ChannelKind kind;

    @Column(name = "parent_channel_id")
    private String parentChannelId;

    @Column(name = "event_type", length = 500)
    private String eventType;

    @Column(name = "schema_ref")
    private String schemaRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "schema_format", length = 15)
    private SchemaFormat schemaFormat;

    @Column(name = "schema_version")
    private String schemaVersion;

    @Column(name = "retention_period")
    private String retentionPeriod;

    @Column(name = "max_delivery_count")
    private Integer maxDeliveryCount;

    @Column(name = "dead_letter_channel_name")
    private String deadLetterChannelName;

    @Column(name = "dlq_message_count")
    private Integer dlqMessageCount;

    @Column(name = "filter_expression")
    private String filterExpression;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_mode", length = 20)
    private RegistrationMode registrationMode;

    @Column(name = "active_on_broker")
    private boolean activeOnBroker = true;

    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public RegistryNode getBrokerNode() { return brokerNode; }
    public void setBrokerNode(RegistryNode brokerNode) { this.brokerNode = brokerNode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ChannelKind getKind() { return kind; }
    public void setKind(ChannelKind kind) { this.kind = kind; }
    public String getParentChannelId() { return parentChannelId; }
    public void setParentChannelId(String parentChannelId) { this.parentChannelId = parentChannelId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSchemaRef() { return schemaRef; }
    public void setSchemaRef(String schemaRef) { this.schemaRef = schemaRef; }
    public SchemaFormat getSchemaFormat() { return schemaFormat; }
    public void setSchemaFormat(SchemaFormat schemaFormat) { this.schemaFormat = schemaFormat; }
    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }
    public String getRetentionPeriod() { return retentionPeriod; }
    public void setRetentionPeriod(String retentionPeriod) { this.retentionPeriod = retentionPeriod; }
    public Integer getMaxDeliveryCount() { return maxDeliveryCount; }
    public void setMaxDeliveryCount(Integer maxDeliveryCount) { this.maxDeliveryCount = maxDeliveryCount; }
    public String getDeadLetterChannelName() { return deadLetterChannelName; }
    public void setDeadLetterChannelName(String deadLetterChannelName) { this.deadLetterChannelName = deadLetterChannelName; }
    public Integer getDlqMessageCount() { return dlqMessageCount; }
    public void setDlqMessageCount(Integer dlqMessageCount) { this.dlqMessageCount = dlqMessageCount; }
    public String getFilterExpression() { return filterExpression; }
    public void setFilterExpression(String filterExpression) { this.filterExpression = filterExpression; }
    public RegistrationMode getRegistrationMode() { return registrationMode; }
    public void setRegistrationMode(RegistrationMode registrationMode) { this.registrationMode = registrationMode; }
    public boolean isActiveOnBroker() { return activeOnBroker; }
    public void setActiveOnBroker(boolean activeOnBroker) { this.activeOnBroker = activeOnBroker; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
