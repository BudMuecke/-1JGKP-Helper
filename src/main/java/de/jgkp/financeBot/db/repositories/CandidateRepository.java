package de.jgkp.financeBot.db.repositories;

import de.jgkp.financeBot.db.entities.Candidate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate,Long> {
    Candidate findCandidateByUserId(Long id);
    @NotNull
    List<Candidate> findAll();
    List<Candidate> findAllByStatusContaining(String status);
}
