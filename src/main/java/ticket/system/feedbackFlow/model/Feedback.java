package ticket.system.feedbackFlow.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;
import ticket.system.feedbackFlow.enums.Status;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = true)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    
    @Column(name= "title", nullable=false)
    private String title;

    @Column(name="description", nullable=false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="category")
    private FeedbackCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name="priority")
    private FeedbackPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable=false )
    private LocalDateTime updatedAt;

    @Column(name="resolved_at", nullable=true )
    private LocalDateTime  resolvedAt;
}
