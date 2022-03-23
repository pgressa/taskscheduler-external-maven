/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;


import com.example.bo.ScheduledTaskContext;
import com.example.bo.TaskDefinition;
import com.example.bo.TaskExecution;
import io.micronaut.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public class YourExternalClass {

    private final Map<String, ScheduledTaskContext> taskContextMap;

    public YourExternalClass() {
        this.taskContextMap = new HashMap<>();
    }

    public String createTask(TaskDefinition taskDefinition){
        String jobId = UUID.randomUUID().toString();
        ScheduledTaskContext scheduledTaskContext = new ScheduledTaskContext(jobId, taskDefinition);
        taskContextMap.put(jobId, scheduledTaskContext);
        scheduleTask(scheduledTaskContext);
        return scheduledTaskContext.getJobId();
    }

    private void scheduleTask(ScheduledTaskContext scheduledTaskContext) {
        TaskScheduler taskScheduler = MicronautFactory.getTaskScheduler();
        TaskDefinition taskDefinition = scheduledTaskContext.getTaskDefinition();
        ScheduledFuture<?> schedule = taskScheduler.schedule(taskDefinition.getCronExpression(), () -> {
            TaskExecution taskExecution = new TaskExecution(taskDefinition.getJobClass(), LocalDateTime.now());
            scheduledTaskContext.getTaskExecutions().add(taskExecution);
            /**
             * Here comes your code that uses reflection to instantiate the job class and execute the job.
             */
            taskExecution.setExecutionDuration(Duration.between(taskExecution.getExecutionStart(), LocalDateTime.now()));
        });
        scheduledTaskContext.setScheduledFuture(schedule);
    }

}
