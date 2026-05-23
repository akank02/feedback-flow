package ticket.system.feedbackFlow.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.dto.AdminResponseRequest;
import ticket.system.feedbackFlow.dto.AdminResponseResponse;
import ticket.system.feedbackFlow.dto.AdminStatsResponse;
import ticket.system.feedbackFlow.dto.FeedbackMapper;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.dto.StatusUpdateRequest;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.exception.ResourceNotFoundException;
import ticket.system.feedbackFlow.exception.BadRequestException;
import ticket.system.feedbackFlow.model.AdminResponse;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.AdminResponseRepository;
import ticket.system.feedbackFlow.repository.FeedbackRepository;
import ticket.system.feedbackFlow.repository.FeedbackSpecification;
import ticket.system.feedbackFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final FeedbackRepository feedbackRepository;
    private final AdminResponseRepository adminResponseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    private User getAdminByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Feedback getFeedbackById(Long id) {
        return feedbackRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
    }

    private void validateTransition(Status current, Status next) {
        boolean valid = switch (current) {
            case OPEN -> next == Status.IN_PROGRESS || next == Status.REJECTED;
            case IN_PROGRESS -> next == Status.RESOLVED || next == Status.REJECTED;
            case RESOLVED, REJECTED -> false;
        };
        if (!valid) {
            throw new BadRequestException(
                "Invalid transition: " + current + " → " + next);
        }
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getAllFeedback(
            Status status,
            FeedbackCategory category,
            FeedbackPriority priority,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        return feedbackRepository
                .findAll(FeedbackSpecification
                        .withFilters(status, category, priority, from, to),
                        pageable)
                .map(FeedbackMapper::toFeedbackResponse);
    }

    @Transactional(noRollbackFor=BadRequestException.class)
    public FeedbackResponse updateStatus(
            Long id, String adminEmail, StatusUpdateRequest request) {

        User admin = getAdminByEmail(adminEmail);
        Feedback feedback = getFeedbackById(id);

        Status oldStatus = feedback.getStatus();
        Status newStatus = request.getStatus();

        validateTransition(oldStatus, newStatus);

        feedback.setStatus(newStatus);

        if (newStatus == Status.RESOLVED || newStatus == Status.REJECTED) {
            feedback.setResolvedAt(LocalDateTime.now());
        }

        feedbackRepository.save(feedback);

        auditLogService.log(feedback, admin, oldStatus, newStatus);

        // after feedbackRepository.save(feedback) and auditLogService.log(...)
        emailService.sendStatusUpdateEmail(
            feedback.getUser().getEmail(),
            feedback.getTitle(),
            newStatus
        );

        return FeedbackMapper.toFeedbackResponse(feedback);
    }

    @Transactional
    public AdminResponseResponse postResponse(
            Long feedbackId, String adminEmail,
            AdminResponseRequest request) {

        User admin = getAdminByEmail(adminEmail);
        Feedback feedback = getFeedbackById(feedbackId);

        AdminResponse response = new AdminResponse();
        response.setFeedback(feedback);
        response.setAdmin(admin);
        response.setMessage(request.getMessage());

        AdminResponse saved = adminResponseRepository.save(response);

        emailService.sendAdminResponseEmail(
            feedback.getUser().getEmail(),
            feedback.getTitle(),
            request.getMessage()
        );

        return new AdminResponseResponse(
                saved.getId(),
                feedback.getId(),
                admin.getEmail(),
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {

        List<Feedback> all = feedbackRepository.findAll();

        long total = all.size();

        Map<String, Long> byStatus = Arrays.stream(Status.values())
        .collect(Collectors.toMap(
                s -> s.name(),
                s -> all.stream()
                        .filter(f -> f.getStatus() == s)
                        .count()
        ));

Map<String, Long> byCategory = Arrays.stream(FeedbackCategory.values())
        .collect(Collectors.toMap(
                c -> c.name(),
                c -> all.stream()
                        .filter(f -> f.getCategory() == c)
                        .count()
        ));

        double avgResolution = all.stream()
                .filter(f -> f.getResolvedAt() != null)
                .mapToLong(f -> Duration.between(
                        f.getCreatedAt(), f.getResolvedAt()).toHours())
                .average()
                .orElse(0.0);

        return new AdminStatsResponse(total, byStatus,
                byCategory, avgResolution);
    }
}