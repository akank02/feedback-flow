package ticket.system.feedbackFlow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.dto.FeedbackMapper;
import ticket.system.feedbackFlow.dto.FeedbackRequest;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.exception.ResourceNotFoundException;
import ticket.system.feedbackFlow.exception.BadRequestException;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.FeedbackRepository;
import ticket.system.feedbackFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

   

    private Feedback getFeedbackById(Long id) {
    return feedbackRepository.findByIdWithUser(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
}

    private void validateOwnership(Feedback feedback, User user) {
        if (!feedback.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied — not your feedback");
        }
    }

    private void validateEditable(Feedback feedback) {
        if (feedback.getStatus() != Status.OPEN) {
            throw new BadRequestException(
                "Cannot edit feedback — status is: " + feedback.getStatus());
        }
    }

    @Transactional
    public FeedbackResponse submitFeedback(
            String email, FeedbackRequest request) {

        User user = getUserByEmail(email);

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setTitle(request.getTitle());
        feedback.setDescription(request.getDescription());
        feedback.setCategory(request.getCategory());
        feedback.setPriority(request.getPriority());
        feedback.setStatus(Status.OPEN);

        return FeedbackMapper.toFeedbackResponse(
                feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getMyFeedback(String email) {
        User user = getUserByEmail(email);
        return feedbackRepository.findByUser(user)
                .stream()
                .map(FeedbackMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackByIdForUser(
            Long id, String email) {

        User user = getUserByEmail(email);
        Feedback feedback = getFeedbackById(id);
        validateOwnership(feedback, user);

        return FeedbackMapper.toFeedbackResponse(feedback);
    }


    @Transactional
    public FeedbackResponse updateFeedback(
            Long id, String email, FeedbackRequest request) {

        User user = getUserByEmail(email);
        Feedback feedback = getFeedbackById(id);
        validateOwnership(feedback, user);
        validateEditable(feedback);

        feedback.setTitle(request.getTitle());
        feedback.setDescription(request.getDescription());
        feedback.setCategory(request.getCategory());
        feedback.setPriority(request.getPriority());

        return FeedbackMapper.toFeedbackResponse(
                feedbackRepository.save(feedback));
    }

    @Transactional
    public void deleteFeedback(Long id, String email) {
        User user = getUserByEmail(email);
        Feedback feedback = getFeedbackById(id);
        validateOwnership(feedback, user);
        validateEditable(feedback);

        feedbackRepository.delete(feedback);
    }
}