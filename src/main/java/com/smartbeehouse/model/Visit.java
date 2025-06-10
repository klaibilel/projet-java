package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une visite d'inspection d'une ruche
 */
public class Visit implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private LocalDateTime scheduledDate;
    private LocalDateTime actualDate;
    private int duration; // Durée en minutes
    private VisitReason reason;
    private VisitStatus status;
    private String observations;
    private String plannedActions;
    private String performedActions;
    private String recommendations;
    private int swarmPopulationRating; // 1-3
    private int healthRating; // 1-3
    private int productivityRating; // 1-3
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations
    private Beehive beehive;
    private Agent agent;
    private Agent approvedBy; // Superviseur qui a approuvé
    
    // Constructeurs
    public Visit() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = VisitStatus.PLANNED;
    }
    
    public Visit(Beehive beehive, Agent agent, LocalDateTime scheduledDate, VisitReason reason) {
        this();
        this.beehive = beehive;
        this.agent = agent;
        this.scheduledDate = scheduledDate;
        this.reason = reason;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { 
        this.scheduledDate = scheduledDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getActualDate() { return actualDate; }
    public void setActualDate(LocalDateTime actualDate) { 
        this.actualDate = actualDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { 
        this.duration = duration;
        this.updatedAt = LocalDateTime.now();
    }
    
    public VisitReason getReason() { return reason; }
    public void setReason(VisitReason reason) { 
        this.reason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public VisitStatus getStatus() { return status; }
    public void setStatus(VisitStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { 
        this.observations = observations;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPlannedActions() { return plannedActions; }
    public void setPlannedActions(String plannedActions) { 
        this.plannedActions = plannedActions;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPerformedActions() { return performedActions; }
    public void setPerformedActions(String performedActions) { 
        this.performedActions = performedActions;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { 
        this.recommendations = recommendations;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getSwarmPopulationRating() { return swarmPopulationRating; }
    public void setSwarmPopulationRating(int swarmPopulationRating) { 
        if (swarmPopulationRating >= 1 && swarmPopulationRating <= 3) {
            this.swarmPopulationRating = swarmPopulationRating;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public int getHealthRating() { return healthRating; }
    public void setHealthRating(int healthRating) { 
        if (healthRating >= 1 && healthRating <= 3) {
            this.healthRating = healthRating;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public int getProductivityRating() { return productivityRating; }
    public void setProductivityRating(int productivityRating) { 
        if (productivityRating >= 1 && productivityRating <= 3) {
            this.productivityRating = productivityRating;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Beehive getBeehive() { return beehive; }
    public void setBeehive(Beehive beehive) { this.beehive = beehive; }
    
    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
    
    public Agent getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Agent approvedBy) { this.approvedBy = approvedBy; }
    
    // Méthodes utilitaires
    public void approve(Agent supervisor) {
        if (supervisor.canSupervise()) {
            this.approvedBy = supervisor;
            this.status = VisitStatus.APPROVED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void complete() {
        this.status = VisitStatus.COMPLETED;
        this.actualDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = VisitStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isOverdue() {
        return status == VisitStatus.PLANNED && 
               scheduledDate != null && 
               scheduledDate.isBefore(LocalDateTime.now());
    }
    
    public double getOverallRating() {
        return (swarmPopulationRating + healthRating + productivityRating) / 3.0;
    }
    
    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", scheduledDate=" + scheduledDate +
                ", actualDate=" + actualDate +
                ", reason=" + reason +
                ", status=" + status +
                ", overallRating=" + getOverallRating() +
                '}';
    }
}

/**
 * Énumération des raisons de visite
 */
enum VisitReason {
    HEALTH_INSPECTION("Inspection sanitaire"),
    NEW_SWARM("Peuplement par nouveau essaim"),
    QUEEN_CHANGE("Changement de reine"),
    FEEDING("Ravitaillement de nourriture"),
    MEDICATION("Prescription de médicament"),
    SWARM_DIVISION("Subdivision d'essaim"),
    FRAME_ADDITION("Ajout de nouveau cadre"),
    EXTENSION_ADDITION("Ajout d'étages d'extension"),
    HONEY_COLLECTION("Collecte de cadre de miel"),
    PRODUCTION_EVALUATION("Évaluation de la production"),
    POPULATION_EVALUATION("Évaluation de l'effectif"),
    ROUTINE_CHECK("Contrôle de routine");
    
    private final String displayName;
    
    VisitReason(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des statuts de visite
 */
enum VisitStatus {
    PLANNED("Planifiée"),
    APPROVED("Approuvée"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    POSTPONED("Reportée");
    
    private final String displayName;
    
    VisitStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}