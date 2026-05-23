package ticket.system.feedbackFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.system.feedbackFlow.model.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {
}