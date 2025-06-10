package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un site d'apiculture
 */
public class ApiaryySite implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private double altitude;
    private LocalDateTime implementationDate;
    private LocalDateTime relocationDate;
    private LocalDateTime closureDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Relations
    private Farm farm;
    private List<Beehive> beehives;
    
    // Constructeurs
    public ApiaryySite() {
        this.beehives = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.implementationDate = LocalDateTime.now();
        this.active = true;
    }
    
    public ApiaryySite(String name, double latitude, double longitude, double altitude) {
        this();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
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
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { 
        this.latitude = latitude;
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { 
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { 
        this.altitude = altitude;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getImplementationDate() { return implementationDate; }
    public void setImplementationDate(LocalDateTime implementationDate) { this.implementationDate = implementationDate; }
    
    public LocalDateTime getRelocationDate() { return relocationDate; }
    public void setRelocationDate(LocalDateTime relocationDate) { this.relocationDate = relocationDate; }
    
    public LocalDateTime getClosureDate() { return closureDate; }
    public void setClosureDate(LocalDateTime closureDate) { this.closureDate = closureDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { 
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Farm getFarm() { return farm; }
    public void setFarm(Farm farm) { this.farm = farm; }
    
    public List<Beehive> getBeehives() { return beehives; }
    public void setBeehives(List<Beehive> beehives) { this.beehives = beehives; }
    
    // Méthodes utilitaires
    public void addBeehive(Beehive beehive) {
        if (this.beehives == null) {
            this.beehives = new ArrayList<>();
        }
        this.beehives.add(beehive);
        beehive.setSite(this);
    }
    
    public void removeBeehive(Beehive beehive) {
        if (this.beehives != null) {
            this.beehives.remove(beehive);
            beehive.setSite(null);
        }
    }
    
    public void relocate(double newLatitude, double newLongitude, double newAltitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
        this.altitude = newAltitude;
        this.relocationDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void close() {
        this.active = false;
        this.closureDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ApiaryySite{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiaryySite)) return false;
        ApiaryySite site = (ApiaryySite) o;
        return id != null && id.equals(site.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}