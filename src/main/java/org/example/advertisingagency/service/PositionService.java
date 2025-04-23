package org.example.advertisingagency.service;

import org.example.advertisingagency.model.Position;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.PositionRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {
    private final PositionRepository positionRepository;
    private final WorkerRepository workerRepository;

    public PositionService(PositionRepository positionRepository,
                           WorkerRepository workerRepository) {
        this.positionRepository = positionRepository;
        this.workerRepository = workerRepository;
    }

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public Optional<Position> findOne(Integer id) {
        return positionRepository.findById(id);
    }

    public Position create(Position position) {
        return positionRepository.save(position);
    }

    public Optional<Position> update(Integer id, Position updated) {
        return positionRepository.findById(id).map(pos -> {
            pos.setName(updated.getName());
            pos.setUpdateDatetime(updated.getUpdateDatetime());
            return positionRepository.save(pos);
        });
    }

    public boolean delete(Integer id) {
        return positionRepository.findById(id).map(pos -> {
            positionRepository.delete(pos);
            return true;
        }).orElse(false);
    }

    public List<Worker> getWorkers(Integer positionId) {
        return workerRepository.findAllByPositionId(positionId);
    }
}
