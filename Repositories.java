package com.registry.repository;

import com.registry.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<RegistryNode, String> {
    List<RegistryNode> findByDomain(String domain);
    List<RegistryNode> findByKind(RegistryNode.NodeKind kind);
    @Query("SELECT DISTINCT n.domain FROM RegistryNode n ORDER BY n.domain")
    List<String> findAllDomains();
    List<RegistryNode> findByNameContainingIgnoreCase(String name);
}

@Repository
interface EdgeRepository extends JpaRepository<RegistryEdge, String> {
    List<RegistryEdge> findBySourceNodeId(String sourceNodeId);
    List<RegistryEdge> findByTargetNodeId(String targetNodeId);
    List<RegistryEdge> findBySourceNodeIdOrTargetNodeId(String sourceNodeId, String targetNodeId);
    List<RegistryEdge> findByChannelRef(String channelRef);
    void deleteBySourceNodeIdOrTargetNodeId(String sourceNodeId, String targetNodeId);
}

@Repository
interface ChannelRepository extends JpaRepository<BrokerChannel, String> {
    List<BrokerChannel> findByBrokerNode_Id(String brokerNodeId);
    @Query("SELECT c FROM BrokerChannel c WHERE c.eventType LIKE %:eventType%")
    List<BrokerChannel> findByEventTypeContaining(@Param("eventType") String eventType);
}

@Repository
interface BehaviourRepository extends JpaRepository<EmbeddedBehaviour, String> {
    List<EmbeddedBehaviour> findByServiceNode_Id(String serviceNodeId);
}
