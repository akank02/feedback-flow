package ticket.system.feedbackFlow.dto;

import ticket.system.feedbackFlow.model.Feedback;

public class FeedbackMapper {

    public static FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getTitle(),
                feedback.getDescription(),
                feedback.getCategory(),
                feedback.getPriority(),
                feedback.getStatus(),
                feedback.getUser().getEmail(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt(),
                feedback.getResolvedAt()
        );
    }
}