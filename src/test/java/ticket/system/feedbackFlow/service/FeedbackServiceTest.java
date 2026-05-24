package ticket.system.feedbackFlow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.system.feedbackFlow.dto.FeedbackRequest;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.exception.BadRequestException;
import ticket.system.feedbackFlow.exception.ResourceNotFoundException;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.FeedbackRepository;
import ticket.system.feedbackFlow.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private User user;
    private Feedback feedback;
    private FeedbackRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setTitle("Test title");
        feedback.setDescription("Test description");
        feedback.setCategory(FeedbackCategory.BUG);
        feedback.setPriority(FeedbackPriority.HIGH);
        feedback.setStatus(Status.OPEN);
        feedback.setUser(user);

        request = new FeedbackRequest();
        request.setTitle("Test title");
        request.setDescription("Test description");
        request.setCategory(FeedbackCategory.BUG);
        request.setPriority(FeedbackPriority.HIGH);
    }

    @Test
    void submitFeedback_shouldSaveAndReturnResponse() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.save(any(Feedback.class)))
                .thenReturn(feedback);

        FeedbackResponse response = feedbackService
                .submitFeedback("user@gmail.com", request);

        assertNotNull(response);
        assertEquals("Test title", response.getTitle());
        assertEquals(Status.OPEN, response.getStatus());
        assertEquals("user@gmail.com", response.getSubmittedBy());
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_userNotFound_shouldThrow() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.submitFeedback(
                        "unknown@gmail.com", request));
    }

    @Test
    void getMyFeedback_shouldReturnListForUser() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByUser(user))
                .thenReturn(List.of(feedback));

        List<FeedbackResponse> result = feedbackService
                .getMyFeedback("user@gmail.com");

        assertEquals(1, result.size());
        assertEquals("Test title", result.get(0).getTitle());
    }

    @Test
    void getMyFeedback_shouldReturnEmptyListWhenNoFeedback() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByUser(user))
                .thenReturn(List.of());

        List<FeedbackResponse> result = feedbackService
                .getMyFeedback("user@gmail.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void getFeedbackByIdForUser_shouldReturnFeedback() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));

        FeedbackResponse response = feedbackService
                .getFeedbackByIdForUser(1L, "user@gmail.com");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getFeedbackByIdForUser_notFound_shouldThrow() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.getFeedbackByIdForUser(
                        999L, "user@gmail.com"));
    }

    @Test
    void getFeedbackByIdForUser_wrongOwner_shouldThrow() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@gmail.com");

        when(userRepository.findByEmail("other@gmail.com"))
                .thenReturn(Optional.of(otherUser));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));

        assertThrows(Exception.class, () ->
                feedbackService.getFeedbackByIdForUser(
                        1L, "other@gmail.com"));
    }

    @Test
    void updateFeedback_shouldUpdateAndReturn() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class)))
                .thenReturn(feedback);

        request.setTitle("Updated title");
        FeedbackResponse response = feedbackService
                .updateFeedback(1L, "user@gmail.com", request);

        assertNotNull(response);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void updateFeedback_notOpen_shouldThrow() {
        feedback.setStatus(Status.IN_PROGRESS);

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));

        assertThrows(BadRequestException.class, () ->
                feedbackService.updateFeedback(
                        1L, "user@gmail.com", request));
    }

    @Test
    void deleteFeedback_shouldDelete() {
        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));

        assertDoesNotThrow(() ->
                feedbackService.deleteFeedback(1L, "user@gmail.com"));
        verify(feedbackRepository).delete(feedback);
    }

    @Test
    void deleteFeedback_notOpen_shouldThrow() {
        feedback.setStatus(Status.RESOLVED);

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));
        when(feedbackRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(feedback));

        assertThrows(BadRequestException.class, () ->
                feedbackService.deleteFeedback(1L, "user@gmail.com"));
    }
}