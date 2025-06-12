/**
 * Smart Bee House Dashboard JavaScript
 * Gestion des interactions et des données du tableau de bord
 */

// Configuration globale
const CONFIG = {
    API_BASE_URL: '/smart-bee-house/api',
    REFRESH_INTERVAL: 30000, // 30 secondes
    CHART_COLORS: {
        primary: '#f39c12',
        secondary: '#e67e22',
        success: '#27ae60',
        warning: '#f1c40f',
        danger: '#e74c3c',
        info: '#3498db'
    }
};

// Variables globales
let productionChart = null;
let refreshInterval = null;

// Initialisation au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    setupEventListeners();
    startAutoRefresh();
});

/**
 * Initialisation du tableau de bord
 */
function initializeDashboard() {
    // Initialisation des données
    loadStatistics();
    loadProductionChart();
    loadRecentVisits();
    loadHighProductionBeehives();
    loadLowProductionBeehives();


}

/**
 * Configuration des écouteurs d'événements
 */
function setupEventListeners() {
    // Gestion du changement de langue
    document.querySelectorAll('[href*="?lang="]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const lang = this.href.split('lang=')[1];
            changeLanguage(lang);
        });
    });
    
    // Gestion du rafraîchissement manuel
    const refreshBtn = document.getElementById('refreshBtn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', function() {
            refreshDashboard();
        });
    }
    
    // Gestion des filtres
    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            applyFilters();
        });
    }
}

/**
 * Démarrage du rafraîchissement automatique
 */
function startAutoRefresh() {
    refreshInterval = setInterval(() => {
        refreshDashboard();
    }, CONFIG.REFRESH_INTERVAL);
}

/**
 * Arrêt du rafraîchissement automatique
 */
function stopAutoRefresh() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }
}

/**
 * Rafraîchissement complet du tableau de bord
 */
function refreshDashboard() {
    showLoadingIndicators();
    loadStatistics();
    loadRecentVisits();
    loadHighProductionBeehives();
    loadLowProductionBeehives();
    updateProductionChart();
}

/**
 * Affichage des indicateurs de chargement
 */
function showLoadingIndicators() {
    const loadingHTML = '<i class="fas fa-spinner fa-spin"></i>';
    
    document.getElementById('totalBeehives').innerHTML = loadingHTML;
    document.getElementById('highProductionBeehives').innerHTML = loadingHTML;
    document.getElementById('scheduledVisits').innerHTML = loadingHTML;
    document.getElementById('alerts').innerHTML = loadingHTML;
}

/**
 * Chargement des statistiques principales
 */
async function loadStatistics() {
    try {
        const [beehivesResponse, visitsResponse] = await Promise.all([
            fetch(`${CONFIG.API_BASE_URL}/beehives`),
            fetch(`${CONFIG.API_BASE_URL}/visits`)
        ]);
        
        const beehives = await beehivesResponse.json();
        const visits = await visitsResponse.json();
        
        // Mise à jour des compteurs
        document.getElementById('totalBeehives').textContent = beehives.length;
        
        const highProduction = beehives.filter(b => b.honeyQuantity > 5000).length;
        document.getElementById('highProductionBeehives').textContent = highProduction;
        
        const scheduledVisits = visits.filter(v => v.status === 'PLANNED').length;
        document.getElementById('scheduledVisits').textContent = scheduledVisits;
        
        const overdueVisits = visits.filter(v => 
            v.status === 'PLANNED' && new Date(v.scheduledDate) < new Date()
        ).length;
        document.getElementById('alerts').textContent = overdueVisits;
        
    } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
        showError('Erreur lors du chargement des statistiques');
    }
}

/**
 * Chargement du graphique de production
 */
async function loadProductionChart() {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/statistics/production`);
        const data = await response.json();
        
        const ctx = document.getElementById('productionChart').getContext('2d');
        
        if (productionChart) {
            productionChart.destroy();
        }
        
        productionChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.labels,
                datasets: [{
                    label: 'Production de Miel (kg)',
                    data: data.values,
                    borderColor: CONFIG.CHART_COLORS.primary,
                    backgroundColor: CONFIG.CHART_COLORS.primary + '20',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: '#fff',
                        bodyColor: '#fff',
                        borderColor: CONFIG.CHART_COLORS.primary,
                        borderWidth: 1
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: 'Période'
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: 'Production (kg)'
                        },
                        beginAtZero: true
                    }
                },
                interaction: {
                    mode: 'nearest',
                    axis: 'x',
                    intersect: false
                }
            }
        });
        
    } catch (error) {
        console.error('Erreur lors du chargement du graphique:', error);
        document.getElementById('productionChart').innerHTML = 
            '<p class="text-center text-muted">Erreur lors du chargement du graphique</p>';
    }
}

/**
 * Mise à jour du graphique de production
 */
async function updateProductionChart() {
    if (productionChart) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/statistics/production`);
            const data = await response.json();
            
            productionChart.data.labels = data.labels;
            productionChart.data.datasets[0].data = data.values;
            productionChart.update('none');
            
        } catch (error) {
            console.error('Erreur lors de la mise à jour du graphique:', error);
        }
    }
}

/**
 * Chargement des visites récentes
 */
async function loadRecentVisits() {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/visits?limit=5&status=PLANNED`);
        const visits = await response.json();
        
        const container = document.getElementById('recentVisits');
        
        if (visits.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted">
                    <i class="fas fa-calendar-times fa-2x mb-2"></i>
                    <p>Aucune visite planifiée</p>
                </div>
            `;
            return;
        }
        
        const visitsHTML = visits.map(visit => `
            <div class="d-flex align-items-center mb-3 p-2 border rounded hover-shadow">
                <div class="me-3">
                    <i class="fas fa-calendar-check fa-2x text-primary"></i>
                </div>
                <div class="flex-grow-1">
                    <h6 class="mb-1">${visit.beehive.name}</h6>
                    <p class="mb-1 text-muted small">${visit.reason}</p>
                    <small class="text-muted">
                        <i class="fas fa-clock me-1"></i>
                        ${formatDate(visit.scheduledDate)}
                    </small>
                </div>
                <div>
                    <span class="badge bg-${getStatusColor(visit.status)}">${visit.status}</span>
                </div>
            </div>
        `).join('');
        
        container.innerHTML = visitsHTML;
        
    } catch (error) {
        console.error('Erreur lors du chargement des visites:', error);
        document.getElementById('recentVisits').innerHTML = 
            '<p class="text-center text-danger">Erreur lors du chargement</p>';
    }
}

/**
 * Chargement des ruches à haute production
 */
async function loadHighProductionBeehives() {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/beehives/high-production?threshold=5000`);
        const beehives = await response.json();
        
        const tbody = document.querySelector('#highProductionTable tbody');
        
        if (beehives.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center text-muted">
                        Aucune ruche à haute production
                    </td>
                </tr>
            `;
            return;
        }
        
        const rowsHTML = beehives.slice(0, 5).map(beehive => `
            <tr class="hover-row" onclick="viewBeehive(${beehive.id})">
                <td>
                    <strong>${beehive.name}</strong>
                    <br>
                    <small class="text-muted">#${beehive.id}</small>
                </td>
                <td>${beehive.site ? beehive.site.name : 'N/A'}</td>
                <td>
                    <span class="badge bg-success">
                        ${beehive.honeyQuantity} kg
                    </span>
                </td>
            </tr>
        `).join('');
        
        tbody.innerHTML = rowsHTML;
        
    } catch (error) {
        console.error('Erreur lors du chargement des ruches haute production:', error);
        document.querySelector('#highProductionTable tbody').innerHTML = 
            '<tr><td colspan="3" class="text-center text-danger">Erreur lors du chargement</td></tr>';
    }
}

/**
 * Chargement des ruches à faible production
 */
async function loadLowProductionBeehives() {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/beehives/low-production?threshold=1000`);
        const beehives = await response.json();
        
        const tbody = document.querySelector('#lowProductionTable tbody');
        
        if (beehives.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center text-muted">
                        Aucune ruche à faible production
                    </td>
                </tr>
            `;
            return;
        }
        
        const rowsHTML = beehives.slice(0, 5).map(beehive => `
            <tr class="hover-row" onclick="viewBeehive(${beehive.id})">
                <td>
                    <strong>${beehive.name}</strong>
                    <br>
                    <small class="text-muted">#${beehive.id}</small>
                </td>
                <td>${beehive.site ? beehive.site.name : 'N/A'}</td>
                <td>
                    <span class="badge bg-warning">
                        ${beehive.honeyQuantity} kg
                    </span>
                </td>
            </tr>
        `).join('');
        
        tbody.innerHTML = rowsHTML;
        
    } catch (error) {
        console.error('Erreur lors du chargement des ruches faible production:', error);
        document.querySelector('#lowProductionTable tbody').innerHTML = 
            '<tr><td colspan="3" class="text-center text-danger">Erreur lors du chargement</td></tr>';
    }
}

/**
 * Changement de langue
 */
function changeLanguage(lang) {
    const url = new URL(window.location);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

/**
 * Application des filtres
 */
function applyFilters() {
    const formData = new FormData(document.getElementById('filterForm'));
    const filters = Object.fromEntries(formData);
    
    // Recharger les données avec les filtres
    loadStatistics(filters);
    loadRecentVisits(filters);
    loadHighProductionBeehives(filters);
    loadLowProductionBeehives(filters);
}

/**
 * Affichage d'une ruche
 */
function viewBeehive(beehiveId) {
    window.location.href = `beehives.jsp?id=${beehiveId}`;
}

/**
 * Formatage des dates
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = date - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) {
        return "Aujourd'hui";
    } else if (diffDays === 1) {
        return "Demain";
    } else if (diffDays === -1) {
        return "Hier";
    } else if (diffDays > 0) {
        return `Dans ${diffDays} jour${diffDays > 1 ? 's' : ''}`;
    } else {
        return `Il y a ${Math.abs(diffDays)} jour${Math.abs(diffDays) > 1 ? 's' : ''}`;
    }
}

/**
 * Obtention de la couleur selon le statut
 */
function getStatusColor(status) {
    const colors = {
        'PLANNED': 'primary',
        'APPROVED': 'info',
        'IN_PROGRESS': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger',
        'POSTPONED': 'secondary'
    };
    return colors[status] || 'secondary';
}

/**
 * Affichage des erreurs
 */
function showError(message) {
    const alertHTML = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    const container = document.querySelector('.container-fluid');
    container.insertAdjacentHTML('afterbegin', alertHTML);
    
    // Auto-suppression après 5 secondes
    setTimeout(() => {
        const alert = container.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

/**
 * Affichage des messages de succès
 */
function showSuccess(message) {
    const alertHTML = `
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    const container = document.querySelector('.container-fluid');
    container.insertAdjacentHTML('afterbegin', alertHTML);
    
    // Auto-suppression après 3 secondes
    setTimeout(() => {
        const alert = container.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 3000);
}

/**
 * Gestion des erreurs réseau
 */
window.addEventListener('online', function() {
    showSuccess('Connexion rétablie');
    refreshDashboard();
});

window.addEventListener('offline', function() {
    showError('Connexion perdue. Certaines fonctionnalités peuvent être indisponibles.');
    stopAutoRefresh();
});

/**
 * Nettoyage avant fermeture de la page
 */
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
    if (productionChart) {
        productionChart.destroy();
    }
});

/**
 * Gestion du redimensionnement de la fenêtre
 */
window.addEventListener('resize', function() {
    if (productionChart) {
        productionChart.resize();
    }
});

/**
 * Export des données
 */
function exportData(type) {
    const url = `${CONFIG.API_BASE_URL}/export/${type}`;
    window.open(url, '_blank');
}

/**
 * Impression du tableau de bord
 */
function printDashboard() {
    window.print();
}

// Ajout de styles CSS dynamiques pour les interactions
const style = document.createElement('style');
style.textContent = `
    .hover-row {
        cursor: pointer;
        transition: all 0.3s ease;
    }
    
    .hover-row:hover {
        background-color: rgba(243, 156, 18, 0.1);
        transform: translateX(5px);
    }
    
    .hover-shadow:hover {
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        transform: translateY(-2px);
    }
`;
document.head.appendChild(style);