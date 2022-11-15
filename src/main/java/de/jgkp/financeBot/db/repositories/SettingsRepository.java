package de.jgkp.financeBot.db.repositories;

import de.jgkp.financeBot.db.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Settings findSettingsById(Long id);
}
