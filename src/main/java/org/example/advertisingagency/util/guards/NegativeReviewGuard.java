package org.example.advertisingagency.util.guards;

import org.example.advertisingagency.enums.MaterialStatus;
import org.example.advertisingagency.enums.MaterialStatusEvent;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class NegativeReviewGuard implements Guard<MaterialStatus, MaterialStatusEvent> {

    @Override
    public boolean evaluate(StateContext<MaterialStatus, MaterialStatusEvent> context) {
        Integer negativeCount = (Integer) context.getExtendedState().getVariables().getOrDefault("negativeCount", 0);
        return negativeCount >= 5;
    }
}
