package taskaya.backend.entity;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.work.Contract;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract; // Associated Contract

    @Column(name = "date", nullable = false)
    @Builder.Default
    private Date date = new Date();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 65)
    private Type type; // Payment type


    @Column(name = "amount", nullable = false)
    private Double amount; // Payment amount


    public enum Type {
        TRANSACTION,
        WITHDRAWL,
        DEPOSIT
    }
}