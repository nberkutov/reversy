package org.example;

import org.example.models.GameProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {
    @Bean("handlers")
    public TaskExecutor threadPoolTaskHandlers() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(GameProperties.HANDLER_THREADS + GameProperties.GAME_SEARCH_THREADS);
        executor.setMaxPoolSize(GameProperties.HANDLER_THREADS + GameProperties.GAME_SEARCH_THREADS);
        executor.setThreadNamePrefix("task_executor_thread_");
        executor.initialize();
        return executor;
    }

    @Bean("clientControllers")
    public TaskExecutor threadPoolClients() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(GameProperties.CLIENT_THREADS);
        executor.setThreadNamePrefix("client_");
        executor.initialize();
        return executor;
    }
}
