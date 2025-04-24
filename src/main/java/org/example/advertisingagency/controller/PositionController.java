package org.example.advertisingagency.controller;

import org.example.advertisingagency.model.Position;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @QueryMapping
    public List<Position> positions() {
        return positionService.getAllPositions();
    }

    @QueryMapping
    public Position position(@Argument Integer id) {
        return positionService.getPositionById(id);
    }

    @MutationMapping
    public Position createPosition(@Argument String name) {
        return positionService.createPosition(name);
    }

    @MutationMapping
    public Position updatePosition(@Argument Integer id, @Argument String name) {
        return positionService.updatePosition(id, name);
    }

    @MutationMapping
    public boolean deletePosition(@Argument Integer id) {
        return positionService.deletePosition(id);
    }

    @SchemaMapping(typeName = "Position", field = "workers")
    public List<Worker> getWorkers(Position position) {
        return positionService.getWorkersByPosition(position.getId());
    }
}
