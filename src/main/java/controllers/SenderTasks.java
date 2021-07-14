package controllers;

import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;


@AllArgsConstructor
@Slf4j
public class SenderTasks extends Thread {
    private final BlockingQueue<TaskResponse> responses;

    @Override
    public void run() {
        log.debug("SenderTasks started");
        while (true) {
            try {
                TaskResponse task = responses.take();
                task.sendJson();
            } catch (IOException | InterruptedException | GameException e) {
                log.error("Error senderTask", e);
            }
        }
    }
}
