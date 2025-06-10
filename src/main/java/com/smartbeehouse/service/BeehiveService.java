package com.smartbeehouse.service;

import com.smartbeehouse.dao.BeehiveDAO;
import com.smartbeehouse.model.Beehive;
import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des ruches
 */
public class BeehiveService {
    
    private final BeehiveDAO beehiveDAO;
    
    public BeehiveService() {
        this.beehiveDAO = new BeehiveDAO();
    }
    
    public BeehiveService(BeehiveDAO beehiveDAO) {
        this.beehiveDAO = beehiveDAO;
    }
    
    /**
     * Créer une nouvelle ruche
     */
    public Beehive createBeehive(Beehive beehive) throws SQLException {
        validateBeehive(beehive);
        return beehiveDAO.create(beehive);
    }
    
    /**
     * Récupérer une ruche par ID
     */
    public Beehive getBeehiveById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la ruche doit être positif");
        }
        return beehiveDAO.findById(id);
    }
    
    /**
     * Récupérer toutes les ruches
     */
    public List<Beehive> getAllBeehives() throws SQLException {
        return beehiveDAO.findAll();
    }
    
    /**
     * Mettre à jour une ruche
     */
    public Beehive updateBeehive(Beehive beehive) throws SQLException {
        validateBeehive(beehive);
        if (beehive.getId() == null) {
            throw new IllegalArgumentException("L'ID de la ruche est requis pour la mise à jour");
        }
        return beehiveDAO.update(beehive);
    }
    
    /**
     * Supprimer une ruche
     */
    public boolean deleteBeehive(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la ruche doit être positif");
        }
        return beehiveDAO.delete(id);
    }
    
    /**
     * Récupérer les ruches par site
     */
    public List<Beehive> getBeehivesBySite(Long siteId) throws SQLException {
        if (siteId == null || siteId <= 0) {
            throw new IllegalArgumentException("L'ID du site doit être positif");
        }
        return beehiveDAO.findBySite(siteId);
    }
    
    /**
     * Récupérer les ruches par agent responsable
     */
    public List<Beehive> getBeehivesByAgent(Long agentId) throws SQLException {
        if (agentId == null || agentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'agent doit être positif");
        }
        return beehiveDAO.findByAgent(agentId);
    }
    
    /**
     * Récupérer les ruches actives
     */
    public List<Beehive> getActiveBeehives() throws SQLException {
        return beehiveDAO.findActiveBeehives();
    }
    
    /**
     * Récupérer les ruches avec production élevée
     */
    public List<Beehive> getHighProductionBeehives(double threshold) throws SQLException {
        if (threshold < 0) {
            throw new IllegalArgumentException("Le seuil doit être positif");
        }
        return beehiveDAO.findHighProductionBeehives(threshold);
    }
    
    /**
     * Récupérer les ruches avec production faible
     */
    public List<Beehive> getLowProductionBeehives(double threshold) throws SQLException {
        if (threshold < 0) {
            throw new IllegalArgumentException("Le seuil doit être positif");
        }
        return beehiveDAO.findLowProductionBeehives(threshold);
    }
    
    /**
     * Obtenir la quantité actuelle de miel pour une ruche
     * Interface pour le service web
     */
    public float getBeeHouseHoneyActualQuantity(int beehiveId) throws SQLException {
        if (beehiveId <= 0) {
            throw new IllegalArgumentException("L'ID de la ruche doit être positif");
        }
        return beehiveDAO.getBeeHouseHoneyActualQuantity(beehiveId);
    }
    
    /**
     * Vérifier si une ruche existe
     */
    public boolean beehiveExists(Long id) throws SQLException {
        if (id == null || id <= 0) {
            return false;
        }
        return beehiveDAO.exists(id);
    }
    
    /**
     * Compter le nombre total de ruches
     */
    public long countBeehives() throws SQLException {
        return beehiveDAO.count();
    }
    
    /**
     * Activer/désactiver une ruche
     */
    public Beehive toggleBeehiveStatus(Long id) throws SQLException {
        Beehive beehive = getBeehiveById(id);
        if (beehive == null) {
            throw new IllegalArgumentException("Ruche non trouvée avec l'ID: " + id);
        }
        
        beehive.setActive(!beehive.isActive());
        return updateBeehive(beehive);
    }
    
    /**
     * Valider les données d'une ruche
     */
    private void validateBeehive(Beehive beehive) {
        if (beehive == null) {
            throw new IllegalArgumentException("La ruche ne peut pas être nulle");
        }
        
        if (beehive.getName() == null || beehive.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ruche est requis");
        }
        
        if (beehive.getName().length() > 100) {
            throw new IllegalArgumentException("Le nom de la ruche ne peut pas dépasser 100 caractères");
        }
        
        if (beehive.getDescription() != null && beehive.getDescription().length() > 500) {
            throw new IllegalArgumentException("La description ne peut pas dépasser 500 caractères");
        }
    }
}