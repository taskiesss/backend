package taskaya.backend.entity.community.posts;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Integer for optimized performance
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Freelancer owner;

    @Column(nullable = false, length = 1000)
    private String description;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReply> replies = new ArrayList<>();
}