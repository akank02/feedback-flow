package ticket.system.feedbackFlow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.dto.FeedbackRequest;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "User — Feedback",
     description = "Submit and manage your own feedback. Requires JWT.")
@SecurityRequirement(name = "Bearer Authentication")
public class FeedbackController {

    private final FeedbackService feedbackService;

    private String getEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    @Operation(
        summary = "Submit feedback",
        description = "Submit new feedback. Status defaults to OPEN. " +
                      "userId is extracted from JWT — never from request body."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Feedback submitted"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
    })
    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(feedbackService.submitFeedback(getEmail(), request));
    }

    @Operation(
        summary = "Get my feedback",
        description = "Returns all feedback submitted by the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List returned"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
    })
    @GetMapping("/my")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback() {
        return ResponseEntity.ok(feedbackService.getMyFeedback(getEmail()));
    }

    @Operation(
        summary = "Get single feedback",
        description = "Returns a single feedback by ID. Only the owner can view it."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback found"),
        @ApiResponse(responseCode = "404", description = "Feedback not found"),
        @ApiResponse(responseCode = "403", description = "Not your feedback")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                feedbackService.getFeedbackByIdForUser(id, getEmail()));
    }

    @Operation(
        summary = "Edit feedback",
        description = "Edit your feedback. Only allowed when status is OPEN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Feedback is not OPEN"),
        @ApiResponse(responseCode = "403", description = "Not your feedback"),
        @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(
                feedbackService.updateFeedback(id, getEmail(), request));
    }

    @Operation(
        summary = "Delete feedback",
        description = "Delete your feedback. Only allowed when status is OPEN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Feedback is not OPEN"),
        @ApiResponse(responseCode = "403", description = "Not your feedback"),
        @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id, getEmail());
        return ResponseEntity.noContent().build();
    }
}