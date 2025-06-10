package com.smartbeehouse.dao;

import com.smartbeehouse.model.Farmer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des fermiers
 */
public class FarmerDAO extends AbstractDAO<Farmer, Long> {
    
    private static final String INSERT_SQL = 
        "INSERT INTO farmers (first_name, last_name, email, phone, address, created_at, updated_at, active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT * FROM farmers WHERE id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT * FROM farmers ORDER BY last_name, first_name";
    
    private static final String UPDATE_SQL = 
        "UPDATE farmers SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
        "address = ?, updated_at = ?, active = ? WHERE id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM farmers WHERE id = ?";
    
    private static final String EXISTS_SQL = 
        "SELECT COUNT(*) FROM farmers WHERE id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM farmers";
    
    private static final String FIND_BY_EMAIL_SQL = 
        "SELECT * FROM farmers WHERE email = ?";
    
    private static final String FIND_ACTIVE_SQL = 
        "SELECT * FROM farmers WHERE active = true ORDER BY last_name, first_name";
    
    @Override
    public Farmer create(Farmer farmer) throws SQLException {
        return executeInTransaction(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, farmer.getFirstName());
                stmt.setString(2, farmer.getLastName());
                stmt.setString(3, farmer.getEmail());
                stmt.setString(4, farmer.getPhone());
                stmt.setString(5, farmer.getAddress());
                stmt.setTimestamp(6, Timestamp.valueOf(farmer.getCreatedAt()));
                stmt.setTimestamp(7, Timestamp.valueOf(farmer.getUpdatedAt()));
                stmt.setBoolean(8, farmer.isActive());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la création du fermier, aucune ligne affectée.");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        farmer.setId(generatedKeys.getLong(1));
                        return farmer;
                    } else {
                        throw new SQLException("Échec de la création du fermier, aucun ID généré.");
                    }
                }
            }
        });
    }
    
    @Override
    public Farmer findById(Long id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFarmer(rs);
                }
                return null;
            }
        }
    }
    
    @Override
    public List<Farmer> findAll() throws SQLException {
        List<Farmer> farmers = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                farmers.add(mapResultSetToFarmer(rs));
            }
        }
        
        return farmers;
    }
    
    @Override
    public Farmer update(Farmer farmer) throws SQLException {
        return executeInTransaction(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, farmer.getFirstName());
                stmt.setString(2, farmer.getLastName());
                stmt.setString(3, farmer.getEmail());
                stmt.setString(4, farmer.getPhone());
                stmt.setString(5, farmer.getAddress());
                stmt.setTimestamp(6, Timestamp.valueOf(farmer.getUpdatedAt()));
                stmt.setBoolean(7, farmer.isActive());
                stmt.setLong(8, farmer.getId());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la mise à jour du fermier, aucune ligne affectée.");
                }
                
                return farmer;
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
     * Trouver un fermier par email
     */
    public Farmer findByEmail(String email) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFarmer(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Trouver tous les fermiers actifs
     */
    public List<Farmer> findActiveFarmers() throws SQLException {
        List<Farmer> farmers = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ACTIVE_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                farmers.add(mapResultSetToFarmer(rs));
            }
        }
        
        return farmers;
    }
    
    /**
     * Mapper un ResultSet vers un objet Farmer
     */
    private Farmer mapResultSetToFarmer(ResultSet rs) throws SQLException {
        Farmer farmer = new Farmer();
        farmer.setId(rs.getLong("id"));
        farmer.setFirstName(rs.getString("first_name"));
        farmer.setLastName(rs.getString("last_name"));
        farmer.setEmail(rs.getString("email"));
        farmer.setPhone(rs.getString("phone"));
        farmer.setAddress(rs.getString("address"));
        farmer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        farmer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        farmer.setActive(rs.getBoolean("active"));
        return farmer;
    }
}