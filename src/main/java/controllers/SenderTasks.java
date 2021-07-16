package controllers;

import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;


@AllArgsConstructor
@Slf4j
public class SenderTasks extends Thread {
    private final LinkedBlockingDeque<TaskResponse> responses;

    @Override
    public void run() {
        log.info("SenderTasks started");
        while (true) {
            try {
                TaskResponse task = responses.takeFirst();
                task.sendJson();
                log.debug("TaskResponse send port={} {}", task.getClient().getSocket().getPort(), task.getResponse());
            } catch (IOException | InterruptedException | GameException e) {
                log.error("Error senderTask", e);
            }
        }
    }
}
