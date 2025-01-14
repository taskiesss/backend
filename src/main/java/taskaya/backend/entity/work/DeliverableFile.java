package taskaya.backend.entity.work;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deliverable_files")
public class DeliverableFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName; // Name of the file

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath; // Path to the file
}