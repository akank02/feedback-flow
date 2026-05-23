package ticket.system.feedbackFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.system.feedbackFlow.enums.Status;
import ticket.system.feedbackFlow.model.AuditLog;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.AuditLogRepository;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Feedback feedback, User changedBy,
                    Status oldStatus, Status newStatus) {

        AuditLog auditLog = new AuditLog();
        auditLog.setFeedback(feedback);
        auditLog.setChangedBy(changedBy);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(newStatus);

        auditLogRepository.save(auditLog);
    }
}