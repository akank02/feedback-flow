package ticket.system.feedbackFlow.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.dto.AdminResponseRequest;
import ticket.system.feedbackFlow.dto.AdminResponseResponse;
import ticket.system.feedbackFlow.dto.AdminStatsResponse;
import ticket.system.feedbackFlow.dto.FeedbackResponse;
import ticket.system.feedbackFlow.dto.StatusUpdateRequest;
import ticket.system.feedbackFlow.enums.FeedbackCategory;
import ticket.system.feedbackFlow.enums.FeedbackPriority;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    private String getEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    @GetMapping("/feedback")
    public ResponseEntity<Page<FeedbackResponse>> getAllFeedback(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) FeedbackCategory category,
            @RequestParam(required = false) FeedbackPriority priority,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        return ResponseEntity.ok(adminService.getAllFeedback(
                status, category, priority, from, to, pageable));
    }

    @PatchMapping("/feedback/{id}/status")
    public ResponseEntity<FeedbackResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(
                adminService.updateStatus(id, getEmail(), request));
    }

    @PostMapping("/feedback/{id}/respond")
    public ResponseEntity<AdminResponseResponse> postResponse(
            @PathVariable Long id,
            @Valid @RequestBody AdminResponseRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postResponse(id, getEmail(), request));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}