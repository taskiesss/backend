package taskaya.backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "skill_name", nullable = false, unique = true)
    private String skillName;
}