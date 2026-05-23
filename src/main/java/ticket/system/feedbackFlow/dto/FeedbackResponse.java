package ticket.system.feedbackFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;
import ticket.system.feedbackFlow.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackResponse {

    private Long id;
    private String title;
    private String description;
    private FeedbackCategory category;
    private FeedbackPriority priority;
    private Status status;
    private String submittedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}