package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une ruche
 */
public class Beehive implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Relations
    private ApiaryySite site;
    private BeehiveBase base; // Socle/Corps obligatoire
    private List<BeehiveExtension> extensions; // Max 5 extensions
    private Agent currentResponsibleAgent;
    private List<Visit> visits;
    private List<SensorMeasurement> measurements;
    
    // Constructeurs
    public Beehive() {
        this.extensions = new ArrayList<>();
        this.visits = new ArrayList<>();
        this.measurements = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    public Beehive(String name) {
        this();
        this.name = name;
        this.base = new BeehiveBase(); // Création automatique du socle
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
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
    
    public ApiaryySite getSite() { return site; }
    public void setSite(ApiaryySite site) { this.site = site; }
    
    public BeehiveBase getBase() { return base; }
    public void setBase(BeehiveBase base) { this.base = base; }
    
    public List<BeehiveExtension> getExtensions() { return extensions; }
    public void setExtensions(List<BeehiveExtension> extensions) { this.extensions = extensions; }
    
    public Agent getCurrentResponsibleAgent() { return currentResponsibleAgent; }
    public void setCurrentResponsibleAgent(Agent currentResponsibleAgent) { 
        this.currentResponsibleAgent = currentResponsibleAgent;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }
    
    public List<SensorMeasurement> getMeasurements() { return measurements; }
    public void setMeasurements(List<SensorMeasurement> measurements) { this.measurements = measurements; }
    
    // Méthodes utilitaires
    public boolean addExtension(BeehiveExtension extension) {
        if (this.extensions.size() >= 5) {
            return false; // Maximum 5 extensions
        }
        if (this.extensions == null) {
            this.extensions = new ArrayList<>();
        }
        this.extensions.add(extension);
        extension.setBeehive(this);
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void removeExtension(BeehiveExtension extension) {
        if (this.extensions != null) {
            this.extensions.remove(extension);
            extension.setBeehive(null);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void addVisit(Visit visit) {
        if (this.visits == null) {
            this.visits = new ArrayList<>();
        }
        this.visits.add(visit);
        visit.setBeehive(this);
    }
    
    public void addMeasurement(SensorMeasurement measurement) {
        if (this.measurements == null) {
            this.measurements = new ArrayList<>();
        }
        this.measurements.add(measurement);
        measurement.setBeehive(this);
    }
    
    public int getTotalFrames() {
        int total = base != null ? base.getFrames().size() : 0;
        if (extensions != null) {
            total += extensions.stream()
                    .mapToInt(ext -> ext.getFrames().size())
                    .sum();
        }
        return total;
    }
    
    public double getCurrentHoneyQuantity() {
        // Calcul basé sur les dernières mesures de poids
        return measurements.stream()
                .filter(m -> m.getIndicatorType() == IndicatorType.WEIGHT)
                .mapToDouble(SensorMeasurement::getValue)
                .max()
                .orElse(0.0);
    }
    
    @Override
    public String toString() {
        return "Beehive{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", extensionsCount=" + (extensions != null ? extensions.size() : 0) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beehive)) return false;
        Beehive beehive = (Beehive) o;
        return id != null && id.equals(beehive.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}