package de.jgkp.financeBot.db.repositories;

import de.jgkp.financeBot.db.entities.Accountupdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountupdatesRepository extends JpaRepository<Accountupdates, Long> {
    Accountupdates findAccountupdatesById(Long id);
}
