package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un agent apiculteur
 */
public class Agent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AgentRole role;
    private String specialization;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Relations
    private Agent supervisor; // Agent superviseur
    private List<Agent> subordinates; // Agents sous sa supervision
    private List<Beehive> responsibleBeehives; // Ruches sous sa responsabilité
    private List<Visit> visits; // Visites effectuées
    
    // Constructeurs
    public Agent() {
        this.subordinates = new ArrayList<>();
        this.responsibleBeehives = new ArrayList<>();
        this.visits = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
        this.role = AgentRole.TECHNICIAN;
    }
    
    public Agent(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { 
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }
    
    public AgentRole getRole() { return role; }
    public void setRole(AgentRole role) { 
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { 
        this.specialization = specialization;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { 
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Agent getSupervisor() { return supervisor; }
    public void setSupervisor(Agent supervisor) { this.supervisor = supervisor; }
    
    public List<Agent> getSubordinates() { return subordinates; }
    public void setSubordinates(List<Agent> subordinates) { this.subordinates = subordinates; }
    
    public List<Beehive> getResponsibleBeehives() { return responsibleBeehives; }
    public void setResponsibleBeehives(List<Beehive> responsibleBeehives) { this.responsibleBeehives = responsibleBeehives; }
    
    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }
    
    // Méthodes utilitaires
    public void addSubordinate(Agent subordinate) {
        if (this.subordinates == null) {
            this.subordinates = new ArrayList<>();
        }
        this.subordinates.add(subordinate);
        subordinate.setSupervisor(this);
    }
    
    public void removeSubordinate(Agent subordinate) {
        if (this.subordinates != null) {
            this.subordinates.remove(subordinate);
            subordinate.setSupervisor(null);
        }
    }
    
    public void assignBeehive(Beehive beehive) {
        if (this.responsibleBeehives == null) {
            this.responsibleBeehives = new ArrayList<>();
        }
        this.responsibleBeehives.add(beehive);
        beehive.setCurrentResponsibleAgent(this);
    }
    
    public void unassignBeehive(Beehive beehive) {
        if (this.responsibleBeehives != null) {
            this.responsibleBeehives.remove(beehive);
            beehive.setCurrentResponsibleAgent(null);
        }
    }
    
    public void addVisit(Visit visit) {
        if (this.visits == null) {
            this.visits = new ArrayList<>();
        }
        this.visits.add(visit);
        visit.setAgent(this);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean canSupervise() {
        return role == AgentRole.SUPERVISOR || role == AgentRole.MANAGER;
    }
    
    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;
        Agent agent = (Agent) o;
        return id != null && id.equals(agent.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

/**
 * Énumération des rôles d'agents
 */
enum AgentRole {
    TECHNICIAN("Technicien"),
    SUPERVISOR("Superviseur"),
    MANAGER("Gestionnaire"),
    SPECIALIST("Spécialiste");
    
    private final String displayName;
    
    AgentRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}