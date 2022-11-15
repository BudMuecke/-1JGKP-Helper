package de.jgkp.financeBot.db.repositories;

import de.jgkp.financeBot.db.entities.Accounts;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    boolean existsByUserId(Long userId);
    Accounts findAccountsByUserId(Long userID);
    Accounts findAccountsByLeaderReminderMessageId(Long messageId);
    List<Accounts> findAllByMembershipDaysLeftGreaterThanEqual(double minimumDaysLeft);
    List<Accounts> findAllByMembershipDaysLeftLessThanEqual(double maximumDaysLeft);
    List<Accounts> findAllByMembershipDaysLeftGreaterThanEqualAndMembershipDaysLeftLessThanEqual(double minimumDaysLeft, double maximumDaysLeft);
    @NotNull
    List<Accounts> findAll();
}
