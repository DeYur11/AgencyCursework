package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.position.CreatePositionInput;
import org.example.advertisingagency.dto.position.UpdatePositionInput;
import org.example.advertisingagency.model.Position;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.service.user.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    // ====== QUERY ======

    @QueryMapping
    public List<Position> positions() {
        return positionService.getAllPositions();
    }

    @QueryMapping
    public Position position(@Argument Integer id) {
        return positionService.getPositionById(id);
    }

    // ====== MUTATION ======

    @MutationMapping
    @Transactional
    public Position createPosition(@Argument CreatePositionInput input) {
        return positionService.createPosition(input);
    }

    @MutationMapping
    @Transactional
    public Position updatePosition(@Argument Integer id, @Argument UpdatePositionInput input) {
        return positionService.updatePosition(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deletePosition(@Argument Integer id) {
        return positionService.deletePosition(id);
    }

    // ====== SCHEMA MAPPING ======

    @SchemaMapping(typeName = "Position", field = "workers")
    public List<Worker> getWorkers(Position position) {
        return positionService.getWorkersByPosition(position.getId());
    }
}
