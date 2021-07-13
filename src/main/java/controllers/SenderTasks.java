package controllers;

import dto.request.player.TaskRequest;
import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@Slf4j
public class SenderTasks extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;


    @Override
    public void run() {
        log.debug("SenderTasks started");
        while (true) {
            try {
                TaskResponse task = responses.take();
//                log.debug("Server send response {}", task.getResponse());
                task.sendJson();
            } catch (IOException | InterruptedException | GameException e) {
                log.error("Error senderTask", e);
            }
        }
    }
}
