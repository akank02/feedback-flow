package ticket.system.feedbackFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ticket.system.feedbackFlow.model.AdminResponse;
import ticket.system.feedbackFlow.model.Feedback;

public interface AdminResponseRepository
        extends JpaRepository<AdminResponse, Long> {

    List<AdminResponse> findByFeedback(Feedback feedback);
}