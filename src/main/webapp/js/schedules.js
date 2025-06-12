const ScheduleManager = {
    schedules: [],
    calendar: null,

    // Initialiser le calendrier
    initCalendar: function() {
        const calendarEl = document.getElementById('calendar');
        if (!calendarEl) return;

        this.calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            events: this.schedules,
            eventClick: this.handleEventClick.bind(this),
            dateClick: this.handleDateClick.bind(this)
        });

        this.calendar.render();
    },

    // Charger les visites programmées
    loadSchedules: async function() {
        try {
            const response = await fetch('/smart-bee-house/api/schedules');
            this.schedules = await response.json();
            this.updateCalendar();
        } catch (error) {
            console.error('Erreur chargement visites:', error);
        }
    },

    // Mettre à jour le calendrier
    updateCalendar: function() {
        if (!this.calendar) return;
        
        this.calendar.removeAllEvents();
        this.calendar.addEventSource(this.schedules);
    },

    // Ajouter une nouvelle visite
    addSchedule: async function(schedule) {
        try {
            const response = await fetch('/smart-bee-house/api/schedules', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(schedule)
            });
            
            if (response.ok) {
                await this.loadSchedules();
            }
        } catch (error) {
            console.error('Erreur ajout visite:', error);
        }
    }
};