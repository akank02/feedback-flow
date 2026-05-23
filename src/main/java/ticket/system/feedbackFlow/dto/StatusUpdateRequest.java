package ticket.system.feedbackFlow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ticket.system.feedbackFlow.enums.Status;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;
}