package douglasferdos.notification;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notify")
public class NotificationController {

	private NotificationService notification;
	
	public NotificationController(NotificationService notification) {
		this.notification = notification;
	}
	
	@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
	private Mono<Void> notification (@RequestBody String message) {
		
		return notification.notify(message)
				.doOnError( e -> {
					System.out.println("Notification error: " + e.getMessage());
				})
				.then();
	}
	
}
