package com.smartbeehouse.dao;

import com.smartbeehouse.model.Beehive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des ruches
 */
public class BeehiveDAO extends AbstractDAO<Beehive, Long> {
    
    private static final String INSERT_SQL = 
        "INSERT INTO beehives (name, description, site_id, current_agent_id, created_at, updated_at, active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT * FROM beehives WHERE id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT * FROM beehives ORDER BY name";
    
    private static final String UPDATE_SQL = 
        "UPDATE beehives SET name = ?, description = ?, site_id = ?, current_agent_id = ?, " +
        "updated_at = ?, active = ? WHERE id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM beehives WHERE id = ?";
    
    private static final String EXISTS_SQL = 
        "SELECT COUNT(*) FROM beehives WHERE id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM beehives";
    
    private static final String FIND_BY_SITE_SQL = 
        "SELECT * FROM beehives WHERE site_id = ? ORDER BY name";
    
    private static final String FIND_BY_AGENT_SQL = 
        "SELECT * FROM beehives WHERE current_agent_id = ? ORDER BY name";
    
    private static final String FIND_ACTIVE_SQL = 
        "SELECT * FROM beehives WHERE active = true ORDER BY name";
    
    private static final String FIND_HIGH_PRODUCTION_SQL = 
        "SELECT b.*, COALESCE(MAX(sm.value), 0) as current_weight " +
        "FROM beehives b " +
        "LEFT JOIN sensor_measurements sm ON b.id = sm.beehive_id AND sm.indicator_type = 'WEIGHT' " +
        "GROUP BY b.id " +
        "HAVING current_weight > ? " +
        "ORDER BY current_weight DESC";
    
    private static final String FIND_LOW_PRODUCTION_SQL = 
        "SELECT b.*, COALESCE(MAX(sm.value), 0) as current_weight " +
        "FROM beehives b " +
        "LEFT JOIN sensor_measurements sm ON b.id = sm.beehive_id AND sm.indicator_type = 'WEIGHT' " +
        "GROUP BY b.id " +
        "HAVING current_weight < ? " +
        "ORDER BY current_weight ASC";
    
    @Override
    public Beehive create(Beehive beehive) throws SQLException {
        return executeInTransaction(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, beehive.getName());
                stmt.setString(2, beehive.getDescription());
                stmt.setLong(3, beehive.getSite() != null ? beehive.getSite().getId() : null);
                stmt.setObject(4, beehive.getCurrentResponsibleAgent() != null ? 
                              beehive.getCurrentResponsibleAgent().getId() : null);
                stmt.setTimestamp(5, Timestamp.valueOf(beehive.getCreatedAt()));
                stmt.setTimestamp(6, Timestamp.valueOf(beehive.getUpdatedAt()));
                stmt.setBoolean(7, beehive.isActive());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la création de la ruche, aucune ligne affectée.");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        beehive.setId(generatedKeys.getLong(1));
                        return beehive;
                    } else {
                        throw new SQLException("Échec de la création de la ruche, aucun ID généré.");
                    }
                }
            }
        });
    }
    
    @Override
    public Beehive findById(Long id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBeehive(rs);
                }
                return null;
            }
        }
    }
    
    @Override
    public List<Beehive> findAll() throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                beehives.add(mapResultSetToBeehive(rs));
            }
        }
        
        return beehives;
    }
    
    @Override
    public Beehive update(Beehive beehive) throws SQLException {
        return executeInTransaction(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, beehive.getName());
                stmt.setString(2, beehive.getDescription());
                stmt.setLong(3, beehive.getSite() != null ? beehive.getSite().getId() : null);
                stmt.setObject(4, beehive.getCurrentResponsibleAgent() != null ? 
                              beehive.getCurrentResponsibleAgent().getId() : null);
                stmt.setTimestamp(5, Timestamp.valueOf(beehive.getUpdatedAt()));
                stmt.setBoolean(6, beehive.isActive());
                stmt.setLong(7, beehive.getId());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la mise à jour de la ruche, aucune ligne affectée.");
                }
                
                return beehive;
            }
        });
    }
    
    @Override
    public boolean delete(Long id) throws SQLException {
        return executeInTransaction(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                return stmt.executeUpdate() > 0;
            }
        });
    }
    
    @Override
    public boolean exists(Long id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(EXISTS_SQL)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    @Override
    public long count() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }
    
    /**
     * Trouver les ruches par site
     */
    public List<Beehive> findBySite(Long siteId) throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_SITE_SQL)) {
            
            stmt.setLong(1, siteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    beehives.add(mapResultSetToBeehive(rs));
                }
            }
        }
        
        return beehives;
    }
    
    /**
     * Trouver les ruches par agent responsable
     */
    public List<Beehive> findByAgent(Long agentId) throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_AGENT_SQL)) {
            
            stmt.setLong(1, agentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    beehives.add(mapResultSetToBeehive(rs));
                }
            }
        }
        
        return beehives;
    }
    
    /**
     * Trouver toutes les ruches actives
     */
    public List<Beehive> findActiveBeehives() throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ACTIVE_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                beehives.add(mapResultSetToBeehive(rs));
            }
        }
        
        return beehives;
    }
    
    /**
     * Trouver les ruches avec production élevée
     */
    public List<Beehive> findHighProductionBeehives(double threshold) throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_HIGH_PRODUCTION_SQL)) {
            
            stmt.setDouble(1, threshold);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    beehives.add(mapResultSetToBeehive(rs));
                }
            }
        }
        
        return beehives;
    }
    
    /**
     * Trouver les ruches avec production faible
     */
    public List<Beehive> findLowProductionBeehives(double threshold) throws SQLException {
        List<Beehive> beehives = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_LOW_PRODUCTION_SQL)) {
            
            stmt.setDouble(1, threshold);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    beehives.add(mapResultSetToBeehive(rs));
                }
            }
        }
        
        return beehives;
    }
    
    /**
     * Obtenir la quantité actuelle de miel pour une ruche
     */
    public float getBeeHouseHoneyActualQuantity(int beehiveId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(sm.value), 0) as current_honey " +
                    "FROM sensor_measurements sm " +
                    "WHERE sm.beehive_id = ? AND sm.indicator_type = 'WEIGHT'";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, beehiveId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("current_honey");
                }
                return 0.0f;
            }
        }
    }
    
    /**
     * Mapper un ResultSet vers un objet Beehive
     */
    private Beehive mapResultSetToBeehive(ResultSet rs) throws SQLException {
        Beehive beehive = new Beehive();
        beehive.setId(rs.getLong("id"));
        beehive.setName(rs.getString("name"));
        beehive.setDescription(rs.getString("description"));
        beehive.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        beehive.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        beehive.setActive(rs.getBoolean("active"));
        
        // Les relations (site, agent) seront chargées séparément si nécessaire
        // pour éviter les requêtes N+1
        
        return beehive;
    }
}