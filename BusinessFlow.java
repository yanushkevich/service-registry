@Entity
@Table(name = "business_flows")
public class BusinessFlow {
  
  @Id private String id;                    // e.g., "place-order"
  private String name;                      // "Place Order"
  private String description;               // "Customer submits order"
  private String domain;                    // "Orders"
  
  @Enumerated(EnumType.STRING)
  private FlowVariant variant;              // HAPPY_PATH, DEGRADED_MODE, COMPENSATING, etc.
  
  private String parentFlowId;              // For variants: reference to parent HAPPY_PATH
  
  @ElementCollection
  @CollectionTable(name = "flow_steps", joinColumns = @JoinColumn(name = "flow_id"))
  private List<FlowStep> steps = new ArrayList<>();
  
  // SLO & Performance
  private String endToEndSlo;               // "p99 < 800ms"
  
  @ElementCollection
  private List<String> criticalPathNodeIds = new ArrayList<>();
  
  @ElementCollection
  private List<String> asyncLegNodeIds = new ArrayList<>();
  
  // Saga
  private boolean isSaga;
  
  @Enumerated(EnumType.STRING)
  private SagaStyle sagaStyle;              // ORCHESTRATION, CHOREOGRAPHY
  
  private String sagaOrchestratorNodeId;
  
  // Regulatory
  @ElementCollection
  private List<String> regulatoryFlags = new ArrayList<>();
  
  private boolean securityReviewCompleted;
  private String lastSecurityReviewDate;
  
  // Ownership & Metadata
  private String owningTeam;
  private String productOwner;
  private String epicRef;                   // Jira epic
  
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = LocalDateTime.now();
}

// Nested class
public static class FlowStep {
  private int sequence;
  private String nodeId;
  private String action;
  private String inboundEdgeId;
  
  @Enumerated(EnumType.STRING)
  private StepKind stepKind;                // SYNCHRONOUS, ASYNC, ASYNC_CRITICAL
  
  private String parallelGroupId;
  private Integer p99LatencyMs;
  private boolean optional;
  
  // Saga fields
  private String localTransaction;
  private String compensatingAction;
  
  // Degraded mode
  private String degradedModeBehaviour;
}
