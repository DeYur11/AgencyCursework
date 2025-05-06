package org.example.advertisingagency.util.state_machine.config;

import org.example.advertisingagency.enums.ServiceEvent;
import org.example.advertisingagency.enums.ServiceStatusType;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.example.advertisingagency.enums.ServiceEvent.EMPTY;
import static org.example.advertisingagency.enums.ServiceEvent.START;
import static org.example.advertisingagency.enums.ServiceStatusType.*;

@Configuration
@EnableStateMachine(name = "serviceInProgressStateMachine")
public class ServiceInProgressStateMachineConfig
        extends StateMachineConfigurerAdapter<ServiceStatusType, ServiceEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<ServiceStatusType, ServiceEvent> states) throws Exception {
        states
                .withStates()
                .initial(ServiceStatusType.NOT_STARTED)
                .states(EnumSet.allOf(ServiceStatusType.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ServiceStatusType, ServiceEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ServiceStatusType.NOT_STARTED).target(IN_PROGRESS).event(START)
                .and()
                .withExternal()
                .source(IN_PROGRESS).target(COMPLETED).event(ServiceEvent.COMPLETE)
                .and()
                .withExternal()
                .source(ServiceStatusType.NOT_STARTED).target(ServiceStatusType.CANCELLED).event(ServiceEvent.CANCEL)
                .and()
                .withExternal()
                .source(IN_PROGRESS).target(ServiceStatusType.CANCELLED).event(ServiceEvent.CANCEL)
                .and()
                .withExternal().source(COMPLETED).target(IN_PROGRESS).event(ServiceEvent.REOPEN)
                .and()
                .withExternal().source(COMPLETED).target(IN_PROGRESS).event(START)
                .and()
                .withExternal().source(IN_PROGRESS).target(NOT_STARTED).event(EMPTY)
                .and()
                .withExternal().source(COMPLETED).target(NOT_STARTED).event(EMPTY);
    }
}
