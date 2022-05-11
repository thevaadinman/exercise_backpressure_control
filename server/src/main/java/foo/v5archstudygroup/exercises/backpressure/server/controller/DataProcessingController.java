package foo.v5archstudygroup.exercises.backpressure.server.controller;

import foo.v5archstudygroup.exercises.backpressure.messages.Messages;
import foo.v5archstudygroup.exercises.backpressure.server.DataProcessor;

import java.util.concurrent.RejectedExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * This is the REST controller that receives requests from the client and forwards them to the {@link DataProcessor}.
 * Feel free to change this in any way you like.
 */
@Controller
public class DataProcessingController {

    private final DataProcessor dataProcessor;

    public DataProcessingController(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @PostMapping("/process")
    public ResponseEntity<Void> process(@RequestBody Messages.ProcessingRequest request) {
        try {
            dataProcessor.enqueue(request);
        } catch (RejectedExecutionException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(null);
    }
}
