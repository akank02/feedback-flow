package ticket.system.feedbackFlow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResponseRequest {

    @NotBlank(message = "Message is required")
    private String message;
}