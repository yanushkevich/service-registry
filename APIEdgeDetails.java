@Entity
@Table(name = "api_edge_details")
public class APIEdgeDetails {
  
  @Id
  @OneToOne @JoinColumn(name = "edge_id")
  private RegistryEdge edge;
  
  private String callingEndpointId;
  private String callingMethod;
  private String callingPath;
  
  private String targetEndpointId;
  private String targetQuery;               // SQL query for databases
  private String targetMethod;
  
  private Integer expectedLatencyMs;
  private Integer sampleCount;              // Number of times traced
  private Integer averageLatencyMs;         // Actual from tracing
  private Double errorRate;
  
  private boolean active;
  private LocalDate firstObserved;
  private LocalDate lastObserved;
}
