package com.smartbeehouse.webservice;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.soap.SOAPBinding;
import com.smartbeehouse.service.BeehiveService;
import java.sql.SQLException;

/**
 * Service Web XML pour récupérer la quantité de miel d'une ruche
 */
@WebService(name = "HoneyQuantityService", 
           targetNamespace = "http://webservice.smartbeehouse.com/",
           serviceName = "HoneyQuantityService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)

public class HoneyQuantityWebService {
    
    private final BeehiveService beehiveService;
    
    public HoneyQuantityWebService() {
        this.beehiveService = new BeehiveService();
    }
    
    /**
     * Récupérer la quantité actuelle de miel pour une ruche donnée
     * 
     * @param beeHouseID ID de la ruche
     * @return Quantité de miel en kilogrammes
     */
    @WebMethod(operationName = "getBeeHouseHoneyActualQuantity")
    public float getBeeHouseHoneyActualQuantity(
            @WebParam(name = "beeHouseID") int beeHouseID) {
        
        try {
            if (beeHouseID <= 0) {
                throw new IllegalArgumentException("L'ID de la ruche doit être positif");
            }
            
            // Vérifier si la ruche existe
            if (!beehiveService.beehiveExists((long) beeHouseID)) {
                throw new IllegalArgumentException("Ruche non trouvée avec l'ID: " + beeHouseID);
            }
            
            return beehiveService.getBeeHouseHoneyActualQuantity(beeHouseID);
            
        } catch (SQLException e) {
            // Log l'erreur et retourner une valeur par défaut
            System.err.println("Erreur SQL lors de la récupération de la quantité de miel: " + e.getMessage());
            return -1.0f; // Valeur d'erreur
        } catch (IllegalArgumentException e) {
            // Log l'erreur et retourner une valeur par défaut
            System.err.println("Argument invalide: " + e.getMessage());
            return -1.0f; // Valeur d'erreur
        } catch (Exception e) {
            // Log l'erreur et retourner une valeur par défaut
            System.err.println("Erreur inattendue: " + e.getMessage());
            return -1.0f; // Valeur d'erreur
        }
    }
    
    /**
     * Vérifier si une ruche existe
     * 
     * @param beeHouseID ID de la ruche
     * @return true si la ruche existe, false sinon
     */
    @WebMethod(operationName = "beeHouseExists")
    public boolean beeHouseExists(@WebParam(name = "beeHouseID") int beeHouseID) {
        try {
            if (beeHouseID <= 0) {
                return false;
            }
            
            return beehiveService.beehiveExists((long) beeHouseID);
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la vérification de l'existence de la ruche: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupérer le nombre total de ruches
     * 
     * @return Nombre total de ruches
     */
    @WebMethod(operationName = "getTotalBeehivesCount")
    public long getTotalBeehivesCount() {
        try {
            return beehiveService.countBeehives();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du comptage des ruches: " + e.getMessage());
            return -1L; // Valeur d'erreur
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return -1L; // Valeur d'erreur
        }
    }
    
    /**
     * Récupérer les informations de base d'une ruche
     * 
     * @param beeHouseID ID de la ruche
     * @return Informations de base de la ruche au format XML
     */
    @WebMethod(operationName = "getBeeHouseBasicInfo")
    public String getBeeHouseBasicInfo(@WebParam(name = "beeHouseID") int beeHouseID) {
        try {
            if (beeHouseID <= 0) {
                return "<error>ID de ruche invalide</error>";
            }
            
            var beehive = beehiveService.getBeehiveById((long) beeHouseID);
            if (beehive == null) {
                return "<error>Ruche non trouvée</error>";
            }
            
            StringBuilder xml = new StringBuilder();
            xml.append("<beehive>");
            xml.append("<id>").append(beehive.getId()).append("</id>");
            xml.append("<name>").append(escapeXml(beehive.getName())).append("</name>");
            xml.append("<description>").append(escapeXml(beehive.getDescription())).append("</description>");
            xml.append("<active>").append(beehive.isActive()).append("</active>");
            xml.append("<createdAt>").append(beehive.getCreatedAt()).append("</createdAt>");
            xml.append("<honeyQuantity>").append(getBeeHouseHoneyActualQuantity(beeHouseID)).append("</honeyQuantity>");
            xml.append("</beehive>");
            
            return xml.toString();
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            return "<error>Erreur de base de données</error>";
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return "<error>Erreur interne</error>";
        }
    }
    
    /**
     * Échapper les caractères XML spéciaux
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
}