package ticket.system.feedbackFlow.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminResponseResponse {

    private Long id;
    private Long feedbackId;
    private String adminEmail;
    private String message;
    private LocalDateTime createdAt;
}