package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une extension/hausse d'une ruche
 */
public class BeehiveExtension implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private int level; // Niveau de l'extension (1-5)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations
    private Beehive beehive;
    private List<Frame> frames; // Max 10 cadres
    
    // Constructeurs
    public BeehiveExtension() {
        this.frames = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BeehiveExtension(String name, int level) {
        this();
        this.name = name;
        this.level = level;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { 
        this.level = level;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Beehive getBeehive() { return beehive; }
    public void setBeehive(Beehive beehive) { this.beehive = beehive; }
    
    public List<Frame> getFrames() { return frames; }
    public void setFrames(List<Frame> frames) { this.frames = frames; }
    
    // Méthodes utilitaires
    public boolean addFrame(Frame frame, int slot) {
        if (slot < 1 || slot > 10) {
            return false; // Slot invalide
        }
        if (this.frames.size() >= 10) {
            return false; // Maximum 10 cadres
        }
        
        if (this.frames == null) {
            this.frames = new ArrayList<>();
        }
        
        frame.setSlot(slot);
        frame.setContainer(this);
        this.frames.add(frame);
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void removeFrame(Frame frame) {
        if (this.frames != null) {
            this.frames.remove(frame);
            frame.setContainer(null);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public boolean isSlotAvailable(int slot) {
        if (frames == null) return true;
        return frames.stream().noneMatch(f -> f.getSlot() == slot);
    }
    
    public Frame getFrameAtSlot(int slot) {
        if (frames == null) return null;
        return frames.stream()
                .filter(f -> f.getSlot() == slot)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return "BeehiveExtension{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", framesCount=" + (frames != null ? frames.size() : 0) +
                '}';
    }
}