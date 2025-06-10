package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une ferme apicole
 */
public class Farm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Relations
    private Farmer owner;
    private List<ApiaryySite> sites;
    
    // Constructeurs
    public Farm() {
        this.sites = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    public Farm(String name, String description) {
        this();
        this.name = name;
        this.description = description;
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
    
    public String getAddress() { return address; }
    public void setAddress(String address) { 
        this.address = address;
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
    
    public Farmer getOwner() { return owner; }
    public void setOwner(Farmer owner) { this.owner = owner; }
    
    public List<ApiaryySite> getSites() { return sites; }
    public void setSites(List<ApiaryySite> sites) { this.sites = sites; }
    
    // Méthodes utilitaires
    public void addSite(ApiaryySite site) {
        if (this.sites == null) {
            this.sites = new ArrayList<>();
        }
        this.sites.add(site);
        site.setFarm(this);
    }
    
    public void removeSite(ApiaryySite site) {
        if (this.sites != null) {
            this.sites.remove(site);
            site.setFarm(null);
        }
    }
    
    public int getTotalBeehives() {
        return sites.stream()
                .mapToInt(site -> site.getBeehives().size())
                .sum();
    }
    
    @Override
    public String toString() {
        return "Farm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Farm)) return false;
        Farm farm = (Farm) o;
        return id != null && id.equals(farm.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}