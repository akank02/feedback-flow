package ticket.system.feedbackFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FeedbackFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackFlowApplication.class, args);
	}

}
