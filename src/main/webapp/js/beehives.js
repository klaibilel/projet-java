// Gestion des ruches et leurs comportements
const BeehiveManager = {
    beehives: [],
    
    // Charger les ruches depuis l'API
    loadBeehives: async function() {
        try {
            const response = await fetch('/smart-bee-house/api/beehives');
            this.beehives = await response.json();
            this.displayBeehives();
        } catch (error) {
            console.error('Erreur chargement ruches:', error);
        }
    },

    // Afficher les ruches dans le tableau
    displayBeehives: function() {
        const container = document.getElementById('beehives-list');
        if (!container) return;

        container.innerHTML = this.beehives.map(beehive => `
            <div class="beehive-card">
                <h3>Ruche #${beehive.id}</h3>
                <p>Population: ${beehive.population}</p>
                <p>Température: ${beehive.temperature}°C</p>
                <p>Humidité: ${beehive.humidity}%</p>
                <button onclick="BeehiveManager.showDetails(${beehive.id})">
                    Détails
                </button>
            </div>
        `).join('');
    },

    // Afficher les détails d'une ruche
    showDetails: function(id) {
        const beehive = this.beehives.find(b => b.id === id);
        if (!beehive) return;
        
        // Afficher modal avec détails
        // ... code pour modal
    }
};