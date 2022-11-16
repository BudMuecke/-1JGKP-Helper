package de.jgkp.financeBot.db.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Component
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Double dailyMembershipFee;
    private Integer membershipExpiresInXDaysReminderDaysAmount;
    private String leaderChannelId;
    private String helperSpamChannelId;
    private String recruitmentChannelId;
}
