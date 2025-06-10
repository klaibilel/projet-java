package com.smartbeehouse.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant une mesure de capteur
 */
public class SensorMeasurement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private IndicatorType indicatorType;
    private double value;
    private String unit;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private String sensorId;
    private MeasurementLocation location;
    
    // Relations
    private Beehive beehive;
    
    // Constructeurs
    public SensorMeasurement() {
        this.timestamp = LocalDateTime.now();
    }
    
    public SensorMeasurement(IndicatorType indicatorType, double value, String unit) {
        this();
        this.indicatorType = indicatorType;
        this.value = value;
        this.unit = unit;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public IndicatorType getIndicatorType() { return indicatorType; }
    public void setIndicatorType(IndicatorType indicatorType) { this.indicatorType = indicatorType; }
    
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    
    public MeasurementLocation getLocation() { return location; }
    public void setLocation(MeasurementLocation location) { this.location = location; }
    
    public Beehive getBeehive() { return beehive; }
    public void setBeehive(Beehive beehive) { this.beehive = beehive; }
    
    // Méthodes utilitaires
    public double convertToUnit(String targetUnit) {
        // Logique de conversion d'unités (à implémenter selon les besoins)
        // Par exemple: grammes vers kilogrammes, Celsius vers Fahrenheit, etc.
        return value; // Placeholder
    }
    
    public boolean isAbnormal() {
        // Logique pour détecter des valeurs anormales selon le type d'indicateur
        switch (indicatorType) {
            case TEMPERATURE_INSIDE:
                return value < 10 || value > 50; // Température anormale
            case HUMIDITY_INSIDE:
                return value < 20 || value > 90; // Humidité anormale
            case WEIGHT:
                return value < 0; // Poids négatif impossible
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "SensorMeasurement{" +
                "id=" + id +
                ", indicatorType=" + indicatorType +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", timestamp=" + timestamp +
                ", location=" + location +
                '}';
    }
}

/**
 * Énumération des types d'indicateurs
 */
enum IndicatorType {
    WEIGHT("Poids"),
    TEMPERATURE_INSIDE("Température intérieure"),
    TEMPERATURE_OUTSIDE("Température extérieure"),
    HUMIDITY_INSIDE("Humidité intérieure"),
    HUMIDITY_OUTSIDE("Humidité extérieure"),
    BEE_MOVEMENT_IN("Mouvement d'entrée des abeilles"),
    BEE_MOVEMENT_OUT("Mouvement de sortie des abeilles"),
    SOUND_INSIDE("Niveau sonore intérieur"),
    SOUND_OUTSIDE("Niveau sonore extérieur"),
    LIGHT_INSIDE("Luminosité intérieure"),
    LIGHT_OUTSIDE("Luminosité extérieure"),
    WIND_SPEED("Vitesse du vent"),
    WIND_DIRECTION("Direction du vent"),
    HIVE_OPENED("Ruche ouverte");
    
    private final String displayName;
    
    IndicatorType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Énumération des emplacements de mesure
 */
enum MeasurementLocation {
    INSIDE("Intérieur"),
    OUTSIDE("Extérieur"),
    BASE("Socle"),
    EXTENSION("Extension"),
    FRAME("Cadre");
    
    private final String displayName;
    
    MeasurementLocation(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}