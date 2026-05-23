package ticket.system.feedbackFlow.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalFeedback;
    private Map<String, Long> countByStatus;
    private Map<String, Long> countByCategory;
    private Double avgResolutionTimeHours;
}