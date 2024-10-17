package douglasferdos.notification;

import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import reactor.core.publisher.Mono;

@Service
public class NotificationService {

	private JmsTemplate jmsTemplate;
	
	public NotificationService (JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Mono<Void> notify (String message) {
		
		WebClient webClient = WebClient.create("https://util.devi.tools/api/v1");

		UriSpec<RequestBodySpec> uriSpec = webClient.post();
		
		// Define the ´endpoint´ that will be called as ´/notify´
		RequestBodySpec bodySpec = uriSpec.uri("/notify");
		
		// Define the Header Body, the type will be a plain text with the passed message
		RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(message).header("Content-Type", "text/plain");

		return headersSpec.exchangeToMono(response -> {
			
			if (response.statusCode().equals(HttpStatus.NO_CONTENT)) {

				System.out.println("Notification Message sent successfully");
				return response.bodyToMono(Void.class);
				
			} else if (response.statusCode().is5xxServerError()) {
				
				// convert the message String into a JMS message and send to the retryQueue
				jmsTemplate.convertAndSend("retryQueue", message);
				
				System.out.println("Server error, adding to retryQueue");
				return response.bodyToMono(Void.class);
				
			} else {
				
				System.out.println("error");
				return response.createException().flatMap(Mono::error);
			}
		});
		
	}
	
}
