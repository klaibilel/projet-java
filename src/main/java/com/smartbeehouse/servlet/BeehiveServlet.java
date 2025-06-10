package com.smartbeehouse.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbeehouse.model.Beehive;
import com.smartbeehouse.service.BeehiveService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet pour la gestion des ruches
 */
@WebServlet(name = "BeehiveServlet", urlPatterns = {"/beehives/*"})
public class BeehiveServlet extends HttpServlet {
    
    private BeehiveService beehiveService;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.beehiveService = new BeehiveService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Récupérer toutes les ruches
                handleGetAllBeehives(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                // Récupérer une ruche par ID
                Long id = Long.parseLong(pathInfo.substring(1));
                handleGetBeehiveById(id, response);
            } else if (pathInfo.equals("/site")) {
                // Récupérer les ruches par site
                String siteIdParam = request.getParameter("siteId");
                if (siteIdParam != null) {
                    Long siteId = Long.parseLong(siteIdParam);
                    handleGetBeehivesBySite(siteId, response);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                                    "Paramètre siteId requis");
                }
            } else if (pathInfo.equals("/agent")) {
                // Récupérer les ruches par agent
                String agentIdParam = request.getParameter("agentId");
                if (agentIdParam != null) {
                    Long agentId = Long.parseLong(agentIdParam);
                    handleGetBeehivesByAgent(agentId, response);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                                    "Paramètre agentId requis");
                }
            } else if (pathInfo.equals("/high-production")) {
                // Récupérer les ruches à haute production
                String thresholdParam = request.getParameter("threshold");
                double threshold = thresholdParam != null ? Double.parseDouble(thresholdParam) : 5000.0;
                handleGetHighProductionBeehives(threshold, response);
            } else if (pathInfo.equals("/low-production")) {
                // Récupérer les ruches à faible production
                String thresholdParam = request.getParameter("threshold");
                double threshold = thresholdParam != null ? Double.parseDouble(thresholdParam) : 1000.0;
                handleGetLowProductionBeehives(threshold, response);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint non trouvé");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "Format de nombre invalide: " + e.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur de base de données: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur interne: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            Beehive beehive = objectMapper.readValue(request.getReader(), Beehive.class);
            Beehive createdBeehive = beehiveService.createBeehive(beehive);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            sendJsonResponse(response, createdBeehive);
            
        } catch (SQLException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur lors de la création: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur interne: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "ID de ruche requis dans l'URL");
            return;
        }
        
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            Beehive beehive = objectMapper.readValue(request.getReader(), Beehive.class);
            beehive.setId(id);
            
            Beehive updatedBeehive = beehiveService.updateBeehive(beehive);
            sendJsonResponse(response, updatedBeehive);
            
        } catch (SQLException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur lors de la mise à jour: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur interne: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "ID de ruche requis dans l'URL");
            return;
        }
        
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = beehiveService.deleteBeehive(id);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                                "Ruche non trouvée");
            }
            
        } catch (SQLException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur lors de la suppression: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur interne: " + e.getMessage());
        }
    }
    
    private void handleGetAllBeehives(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String activeParam = request.getParameter("active");
        List<Beehive> beehives;
        
        if ("true".equals(activeParam)) {
            beehives = beehiveService.getActiveBeehives();
        } else {
            beehives = beehiveService.getAllBeehives();
        }
        
        sendJsonResponse(response, beehives);
    }
    
    private void handleGetBeehiveById(Long id, HttpServletResponse response) 
            throws SQLException, IOException {
        
        Beehive beehive = beehiveService.getBeehiveById(id);
        
        if (beehive != null) {
            sendJsonResponse(response, beehive);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                            "Ruche non trouvée avec l'ID: " + id);
        }
    }
    
    private void handleGetBeehivesBySite(Long siteId, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<Beehive> beehives = beehiveService.getBeehivesBySite(siteId);
        sendJsonResponse(response, beehives);
    }
    
    private void handleGetBeehivesByAgent(Long agentId, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<Beehive> beehives = beehiveService.getBeehivesByAgent(agentId);
        sendJsonResponse(response, beehives);
    }
    
    private void handleGetHighProductionBeehives(double threshold, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<Beehive> beehives = beehiveService.getHighProductionBeehives(threshold);
        sendJsonResponse(response, beehives);
    }
    
    private void handleGetLowProductionBeehives(double threshold, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<Beehive> beehives = beehiveService.getLowProductionBeehives(threshold);
        sendJsonResponse(response, beehives);
    }
    
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }
    
    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        ErrorResponse errorResponse = new ErrorResponse(status, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    /**
     * Classe pour les réponses d'erreur
     */
    private static class ErrorResponse {
        private int status;
        private String message;
        private long timestamp;
        
        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters pour Jackson
        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}