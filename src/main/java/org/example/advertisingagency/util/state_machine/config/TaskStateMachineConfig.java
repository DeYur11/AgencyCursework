package org.example.advertisingagency.util.state_machine.config;

import org.example.advertisingagency.enums.TaskEvent;
import org.example.advertisingagency.enums.TaskStatusType;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine(name = "taskStateMachine")
public class TaskStateMachineConfig extends StateMachineConfigurerAdapter<TaskStatusType, TaskEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<TaskStatusType, TaskEvent> states) throws Exception {
        states.withStates()
                .initial(TaskStatusType.NOT_STARTED)
                .states(EnumSet.allOf(TaskStatusType.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TaskStatusType, TaskEvent> transitions) throws Exception {
        transitions
                .withExternal().source(TaskStatusType.NOT_STARTED).target(TaskStatusType.IN_PROGRESS).event(TaskEvent.START)
                .and()
                .withExternal().source(TaskStatusType.IN_PROGRESS).target(TaskStatusType.ON_HOLD).event(TaskEvent.HOLD)
                .and()
                .withExternal().source(TaskStatusType.ON_HOLD).target(TaskStatusType.IN_PROGRESS).event(TaskEvent.RESUME)
                .and()
                .withExternal().source(TaskStatusType.IN_PROGRESS).target(TaskStatusType.COMPLETED).event(TaskEvent.COMPLETE)
                .and()
                .withExternal().source(TaskStatusType.COMPLETED).target(TaskStatusType.IN_PROGRESS).event(TaskEvent.RESUME);
    }
}
