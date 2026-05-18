package ticket.system.feedbackFlow.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;
import ticket.system.feedbackFlow.enums.SystemPlan;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;
    
    @Column(name= "slug",nullable=false, unique=true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name= "plan")
    private SystemPlan plan;
    
    @Column(name="owner_id")
    private Long ownerid;

    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

}
