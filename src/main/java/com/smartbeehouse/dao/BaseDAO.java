package com.smartbeehouse.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface de base pour tous les DAO
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Créer une nouvelle entité
     */
    T create(T entity) throws SQLException;
    
    /**
     * Récupérer une entité par son ID
     */
    T findById(ID id) throws SQLException;
    
    /**
     * Récupérer toutes les entités
     */
    List<T> findAll() throws SQLException;
    
    /**
     * Mettre à jour une entité
     */
    T update(T entity) throws SQLException;
    
    /**
     * Supprimer une entité
     */
    boolean delete(ID id) throws SQLException;
    
    /**
     * Vérifier si une entité existe
     */
    boolean exists(ID id) throws SQLException;
    
    /**
     * Compter le nombre d'entités
     */
    long count() throws SQLException;
}

/**
 * Classe abstraite de base pour l'implémentation des DAO
 */
abstract class AbstractDAO<T, ID> implements BaseDAO<T, ID> {
    
    protected static final String DB_URL = "jdbc:mysql://localhost:3306/smart_bee_house";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = "password";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }
    
    /**
     * Obtenir une connexion à la base de données
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Fermer une connexion de manière sécurisée
     */
    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log l'erreur mais ne pas la propager
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    /**
     * Exécuter une transaction
     */
    protected <R> R executeInTransaction(TransactionCallback<R> callback) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            
            R result = callback.execute(connection);
            
            connection.commit();
            return result;
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    // Log l'erreur
                    System.err.println("Erreur lors de la restauration de l'auto-commit: " + e.getMessage());
                }
                closeConnection(connection);
            }
        }
    }
    
    /**
     * Interface fonctionnelle pour les callbacks de transaction
     */
    @FunctionalInterface
    protected interface TransactionCallback<R> {
        R execute(Connection connection) throws SQLException;
    }
}