package ticket.system.feedbackFlow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.system.feedbackFlow.dto.FeedbackRequest;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.service.FeedbackService;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @Valid @RequestBody FeedbackRequest request) {

                String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(feedbackService.submitFeedback(email, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback() {

        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        return ResponseEntity.ok(feedbackService.getMyFeedback(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(
            @PathVariable Long id) {

                String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

        return ResponseEntity.ok(
                feedbackService.getFeedbackByIdForUser(id, email));
    }

    @GetMapping("/test")
public String test(Authentication authentication) {
    return authentication.getName();
}

    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request) {

                String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

        return ResponseEntity.ok(
                feedbackService.updateFeedback(id, email, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long id) {

                String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

        feedbackService.deleteFeedback(id, email);
        return ResponseEntity.noContent().build();
    }
}