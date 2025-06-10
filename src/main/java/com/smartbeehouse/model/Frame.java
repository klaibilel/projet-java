package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant un cadre de cire
 */
public class Frame implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private int slot; // Position dans le socle ou l'extension (1-10)
    private FrameType type;
    private FrameState state;
    private double honeyWeight; // Poids du miel en grammes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations polymorphes - un cadre peut être dans un socle ou une extension
    private Object container; // BeehiveBase ou BeehiveExtension
    
    // Constructeurs
    public Frame() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.type = FrameType.WAX;
        this.state = FrameState.EMPTY;
        this.honeyWeight = 0.0;
    }
    
    public Frame(String name, int slot) {
        this();
        this.name = name;
        this.slot = slot;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getSlot() { return slot; }
    public void setSlot(int slot) { 
        this.slot = slot;
        this.updatedAt = LocalDateTime.now();
    }
    
    public FrameType getType() { return type; }
    public void setType(FrameType type) { 
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }
    
    public FrameState getState() { return state; }
    public void setState(FrameState state) { 
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getHoneyWeight() { return honeyWeight; }
    public void setHoneyWeight(double honeyWeight) { 
        this.honeyWeight = honeyWeight;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Object getContainer() { return container; }
    public void setContainer(Object container) { this.container = container; }
    
    // Méthodes utilitaires
    public boolean isReadyForHarvest() {
        return state == FrameState.FULL && honeyWeight > 0;
    }
    
    public void harvest() {
        if (isReadyForHarvest()) {
            this.state = FrameState.EMPTY;
            this.honeyWeight = 0.0;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void fillWithHoney(double weight) {
        this.honeyWeight = weight;
        this.state = weight > 0 ? FrameState.PARTIAL : FrameState.EMPTY;
        if (weight >= 2000) { // 2kg considéré comme plein
            this.state = FrameState.FULL;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Frame{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slot=" + slot +
                ", type=" + type +
                ", state=" + state +
                ", honeyWeight=" + honeyWeight +
                '}';
    }
}

/**
 * Énumération des types de cadres
 */
enum FrameType {
    WAX("Cire"),
    PLASTIC("Plastique"),
    WOOD("Bois");
    
    private final String displayName;
    
    FrameType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des états de cadres
 */
enum FrameState {
    EMPTY("Vide"),
    PARTIAL("Partiellement rempli"),
    FULL("Plein"),
    DAMAGED("Endommagé");
    
    private final String displayName;
    
    FrameState(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}