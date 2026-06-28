@Entity
@Table(name = "api_endpoints")
public class APIEndpoint {
  
  @Id private String id;                    // e.g., "order-service:get-orders"
  
  @ManyToOne @JoinColumn(name = "node_id")
  private RegistryNode node;
  
  private String method;                    // GET, POST, PUT, DELETE, PUBLISH, SUBSCRIBE
  private String path;                      // /orders/{id}
  private String operationId;               // From OpenAPI
  private String summary;
  private String description;
  
  // API Contract
  @Embedded
  private APIContract contract;
  
  // Deprecation
  private boolean deprecated;
  private String deprecationMessage;
  private LocalDate sunsetDate;
  private String apiVersion;               // v1, v2, etc.
  
  // SLA & Performance
  private Integer p99LatencyMs;
  private Integer rateLimit;                // req/sec
  
  @ElementCollection
  private List<String> authRequired;
  
  // Usage
  private Long callsPerDay;
  private Double errorRate;
  
  @OneToMany(mappedBy = "endpoint")
  private List<ServiceConsumer> consumers = new ArrayList<>();
  
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = LocalDateTime.now();
}

public class APIContract {
  private String requestSchemaRef;          // URL to schema
  private String responseSchemaRef;
  private String openApiPath;               // Pointer in OpenAPI spec
  
  @ElementCollection
  @CollectionTable(name = "response_schemas")
  private Map<Integer, ResponseSchema> responsesByStatusCode;
}

public class ResponseSchema {
  private String schemaRef;
  private String description;
}

public class ServiceConsumer {
  @Id
  @GeneratedValue
  private Long id;
  
  @ManyToOne
  private APIEndpoint endpoint;
  
  @ManyToOne
  private RegistryNode consumer;            // The service calling this endpoint
  
  private Long callsPerDay;
  private Double errorRate;
  private LocalDate lastCalled;
}
