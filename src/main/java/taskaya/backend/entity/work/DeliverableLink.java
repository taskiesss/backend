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
@Table(name = "deliverable_links")
public class DeliverableLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName; // Name of the file

    @Column(name = "link_url", nullable = false, length = 500)
    private String linkUrl; // URL of the deliverable link

}