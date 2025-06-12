const AlertSystem = {
    alerts: [],

    // Initialiser les websockets pour les alertes en temps réel
    initWebSocket: function() {
        const ws = new WebSocket('ws://localhost:8080/smart-bee-house/ws/alerts');
        
        ws.onmessage = (event) => {
            const alert = JSON.parse(event.data);
            this.handleNewAlert(alert);
        };
    },

    // Gérer une nouvelle alerte
    handleNewAlert: function(alert) {
        this.alerts.unshift(alert);
        this.displayAlerts();
        this.showNotification(alert);
    },

    // Afficher les alertes dans l'interface
    displayAlerts: function() {
        const container = document.getElementById('alerts-container');
        if (!container) return;

        container.innerHTML = this.alerts.map(alert => `
            <div class="alert alert-${alert.severity}">
                <h4>${alert.title}</h4>
                <p>${alert.message}</p>
                <small>${new Date(alert.timestamp).toLocaleString()}</small>
            </div>
        `).join('');
    },

    // Afficher une notification système
    showNotification: function(alert) {
        if (!("Notification" in window)) return;

        Notification.requestPermission().then(permission => {
            if (permission === "granted") {
                new Notification("Smart Bee House Alert", {
                    body: alert.message
                });
            }
        });
    }
};