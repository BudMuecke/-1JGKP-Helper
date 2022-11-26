package de.jgkp.financeBot.db.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long userId;
    private String userName;
    private String lastPayment;
    private Double lastPaymentAmount;
    private Double currentAccount;
    private Double membershipDaysLeft;
    private Boolean hasReservedSlot;
    private Boolean hasRecievedMembershipExpiredReminder;
    private Long leaderReminderMessageId;
}

