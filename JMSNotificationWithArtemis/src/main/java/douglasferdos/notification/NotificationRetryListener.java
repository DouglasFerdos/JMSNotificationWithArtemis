package douglasferdos.notification;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationRetryListener {

	private NotificationService notificationService;
	
	public NotificationRetryListener (NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@JmsListener(destination = "retryQueue")
	public void onRetryNotification (String message) {
		// retry to send the message
		notificationService.notify(message).subscribe(null, error -> {
			System.out.println("subscribe error");
		});
		
	}
}
