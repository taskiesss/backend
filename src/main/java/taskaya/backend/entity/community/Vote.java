package taskaya.backend.entity.community;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Proposal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Integer for optimized performance
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "community_member_id", nullable = false)
    private CommunityMember communityMember;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false)
    private Boolean agreed;
}