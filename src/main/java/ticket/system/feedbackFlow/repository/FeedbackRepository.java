package ticket.system.feedbackFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ticket.system.feedbackFlow.model.Feedback;
import ticket.system.feedbackFlow.model.User;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f JOIN FETCH f.user WHERE f.user = :user")
    List<Feedback> findByUser(@Param("user") User user);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.user WHERE f.id = :id")
    Optional<Feedback> findByIdWithUser(@Param("id") Long id);
}