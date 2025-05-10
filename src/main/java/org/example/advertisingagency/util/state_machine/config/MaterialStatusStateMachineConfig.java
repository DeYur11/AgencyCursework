package org.example.advertisingagency.util.state_machine.config;


import org.example.advertisingagency.enums.MaterialStatus;
import org.example.advertisingagency.enums.MaterialStatusEvent;
import org.example.advertisingagency.util.guards.NegativeReviewGuard;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine(name = "materialStatusStateMachine")
public class MaterialStatusStateMachineConfig
        extends StateMachineConfigurerAdapter<MaterialStatus, MaterialStatusEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<MaterialStatus, MaterialStatusEvent> states) throws Exception {
        states
                .withStates()
                .initial(MaterialStatus.DRAFT)
                .states(EnumSet.allOf(MaterialStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<MaterialStatus, MaterialStatusEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(MaterialStatus.DRAFT)
                .target(MaterialStatus.PENDING_REVIEW)
                .event(MaterialStatusEvent.SUBMIT_FOR_REVIEW)
                .and()
                .withExternal()
                .source(MaterialStatus.PENDING_REVIEW)
                .target(MaterialStatus.ACCEPTED)
                .event(MaterialStatusEvent.ADD_POSITIVE_REVIEW)
                .guard(context -> {
                    Integer positiveCount = (Integer) context.getExtendedState().getVariables().getOrDefault("positiveCount", 0);
                    Integer negativeCount = (Integer) context.getExtendedState().getVariables().getOrDefault("negativeCount", 0);
                    return positiveCount >= 3 && negativeCount < 5;
                })
                .and()
                .withExternal()
                .source(MaterialStatus.PENDING_REVIEW)
                .target(MaterialStatus.REJECTED)
                .event(MaterialStatusEvent.ADD_NEGATIVE_REVIEW)
                .guard(new NegativeReviewGuard())
                .and()
                .withExternal()
                .source(MaterialStatus.PENDING_REVIEW)
                .target(MaterialStatus.DRAFT)
                .event(MaterialStatusEvent.ADD_NEGATIVE_REVIEW)
                .guard(context -> {
                    Integer negativeCount = (Integer) context.getExtendedState().getVariables().getOrDefault("negativeCount", 0);
                    return negativeCount < 5;
                });
    }
}

