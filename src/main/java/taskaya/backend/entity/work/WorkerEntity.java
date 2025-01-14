package taskaya.backend.entity.work;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "worker_entities")
public class WorkerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Unique ID for WorkerEntity, separate from Freelancer ID

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WorkerType type; // Enum to define the type of worker (COMMUNITY, FREELANCER)

    @JsonIgnore
    @OneToMany(mappedBy = "workerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposal> proposals; // List of proposals associated with the worker

    @JsonIgnore
    @OneToMany(mappedBy = "workerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contract> contracts;

    public enum WorkerType {
        COMMUNITY,
        FREELANCER
    }
}