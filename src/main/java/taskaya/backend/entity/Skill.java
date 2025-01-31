package taskaya.backend.entity;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Skill(String skillName){
        name=skillName;
    }
}