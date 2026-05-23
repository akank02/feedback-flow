package ticket.system.feedbackFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;

@Getter
@Setter
public class FeedbackRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private FeedbackCategory category;

    @NotNull(message = "Priority is required")
    private FeedbackPriority priority;
}