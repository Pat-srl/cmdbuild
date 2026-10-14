-- Update translation codes
DO $$
BEGIN
UPDATE "_Translation"
SET "Code" = REPLACE(
        "Code",
        'dashboard.ServiceDeskActiveTickets.charts',
        'dashboard.ServiceDeskActiveTickets.components'
             )
WHERE "Code" LIKE '%dashboard.ServiceDeskActiveTickets.charts%';
END $$;