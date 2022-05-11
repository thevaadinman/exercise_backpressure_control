package foo.v5archstudygroup.exercises.backpressure.client;

import foo.v5archstudygroup.exercises.backpressure.messages.Messages;
import foo.v5archstudygroup.exercises.backpressure.messages.converter.ProcessingRequestMessageConverter;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * This class is responsible for interacting with the REST endpoint on the server. You are allowed to change this class
 * in any way you like.
 */
public class RestClient {

    private final RestTemplate restTemplate;
    private final URI serverUri;

    public RestClient(URI serverUri) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        // Always remember to set timeouts!
        requestFactory.setConnectTimeout(100);
        requestFactory.setReadTimeout(1000);
        restTemplate = new RestTemplate(List.of(new ProcessingRequestMessageConverter()));
        restTemplate.setRequestFactory(requestFactory);
        this.serverUri = serverUri;
    }

    public void sendToServer(Messages.ProcessingRequest processingRequest) throws Exception {
        var uri = UriComponentsBuilder.fromUri(serverUri).path("/process").build().toUri();
        long wait = 100; // msec
        boolean retry = true;

        while (retry) {
            try {              
                if (restTemplate.postForEntity(uri, processingRequest, Void.class).getStatusCode().compareTo(HttpStatus.OK) != 0) {
                    retry = false;
                    wait = 100;
                    return;
                }
            } catch (Exception ex) {
                System.out.println("lol? " + ex.getMessage());
            }

            wait = (long)(wait * 1.5);

            if (wait < 10000) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                retry = false; // fuck this
                throw new Exception("Failed to send to server");
            }
        }
    }
}
