package ticket.system.feedbackFlow.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ticket.system.feedbackFlow.dto.StatusUpdateRequest;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.exception.BadRequestException;
import ticket.system.feedbackFlow.exception.ResourceNotFoundException;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.AdminResponseRepository;
import ticket.system.feedbackFlow.repository.FeedbackRepository;
import ticket.system.feedbackFlow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceStateMachineTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private AdminResponseRepository adminResponseRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private User admin;
    private Feedback feedback;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setTitle("Test feedback");
        feedback.setStatus(Status.OPEN);
        feedback.setUser(admin);
    }

    // ── Legal transitions ──────────────────────────────────────

    @Test
    void transition_openToInProgress_shouldSucceed() {
        setupMocks(Status.OPEN);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.IN_PROGRESS);

        assertDoesNotThrow(() ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void transition_openToRejected_shouldSucceed() {
        setupMocks(Status.OPEN);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.REJECTED);

        assertDoesNotThrow(() ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void transition_inProgressToResolved_shouldSucceed() {
        setupMocks(Status.IN_PROGRESS);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.RESOLVED);

        assertDoesNotThrow(() ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void transition_inProgressToRejected_shouldSucceed() {
        setupMocks(Status.IN_PROGRESS);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.REJECTED);

        assertDoesNotThrow(() ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
        verify(feedbackRepository).save(any(Feedback.class));
    }

    // ── Illegal transitions ────────────────────────────────────

    @Test
    void transition_resolvedToAnything_shouldThrow() {
        setupMocks(Status.RESOLVED);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.OPEN);

        assertThrows(BadRequestException.class, () ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
    }

    @Test
    void transition_rejectedToAnything_shouldThrow() {
        setupMocks(Status.REJECTED);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.IN_PROGRESS);

        assertThrows(BadRequestException.class, () ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
    }

    @Test
    void transition_openToResolved_shouldThrow() {
        setupMocks(Status.OPEN);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.RESOLVED);

        assertThrows(BadRequestException.class, () ->
                adminService.updateStatus(1L, "admin@gmail.com", request));
    }

    // ── resolvedAt auto-set ────────────────────────────────────

    @Test
    void transition_toResolved_shouldSetResolvedAt() {
        setupMocks(Status.IN_PROGRESS);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.RESOLVED);

        adminService.updateStatus(1L, "admin@gmail.com", request);

        assertNotNull(feedback.getResolvedAt());
    }

    @Test
    void transition_toRejected_shouldSetResolvedAt() {
        setupMocks(Status.IN_PROGRESS);
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.REJECTED);

        adminService.updateStatus(1L, "admin@gmail.com", request);

        assertNotNull(feedback.getResolvedAt());
    }

    @Test
    void updateStatus_feedbackNotFound_shouldThrow() {
        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));
        when(feedbackRepository.findByIdWithUser(999L))
                .thenReturn(Optional.empty());

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.IN_PROGRESS);

        assertThrows(ResourceNotFoundException.class, () ->
                adminService.updateStatus(999L, "admin@gmail.com", request));
    }

    @Test
    void updateStatus_adminNotFound_shouldThrow() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(Status.IN_PROGRESS);

        assertThrows(ResourceNotFoundException.class, () ->
                adminService.updateStatus(1L, "unknown@gmail.com", request));
    }

    // ── Helper ─────────────────────────────────────────────────

    private void setupMocks(Status currentStatus) {
        feedback.setStatus(currentStatus);
        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));
        lenient().when(feedbackRepository.save(any()))
                .thenReturn(feedback);
        lenient().doNothing().when(auditLogService)
                .log(any(), any(), any(), any());
        lenient().doNothing().when(emailService)
                .sendStatusUpdateEmail(any(), any(), any());
    }
}