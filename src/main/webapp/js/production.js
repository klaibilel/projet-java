const ProductionTracker = {
    data: [],
    chart: null,

    // Initialiser le graphique de production
    initChart: function() {
        const ctx = document.getElementById('production-chart');
        if (!ctx) return;

        this.chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Production de miel (kg)',
                    data: [],
                    borderColor: '#ffd700',
                    tension: 0.1
                }]
            }
        });
    },

    // Charger les données de production
    loadProductionData: async function() {
        try {
            const response = await fetch('/smart-bee-house/api/production');
            this.data = await response.json();
            this.updateChart();
        } catch (error) {
            console.error('Erreur chargement production:', error);
        }
    },

    // Mettre à jour le graphique
    updateChart: function() {
        if (!this.chart) return;
        
        this.chart.data.labels = this.data.map(d => d.date);
        this.chart.data.datasets[0].data = this.data.map(d => d.quantity);
        this.chart.update();
    }
};