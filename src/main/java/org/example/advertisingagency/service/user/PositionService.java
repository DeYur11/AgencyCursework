package org.example.advertisingagency.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.model.Position;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.PositionRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final WorkerRepository workerRepository;

    @Autowired
    public PositionService(PositionRepository positionRepository, WorkerRepository workerRepository) {
        this.positionRepository = positionRepository;
        this.workerRepository = workerRepository;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(Integer id) {
        return positionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Position createPosition(String name) {
        Position position = new Position();
        position.setName(name);
        return positionRepository.save(position);
    }

    @Transactional
    public Position updatePosition(Integer id, String name) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
        if (name != null) position.setName(name);
        return positionRepository.save(position);
    }

    @Transactional
    public boolean deletePosition(Integer id) {
        if (!positionRepository.existsById(id)) return false;
        positionRepository.deleteById(id);
        return true;
    }

    public List<Worker> getWorkersByPosition(Integer positionId) {
        return workerRepository.findAllByPositionId(positionId);
    }
}
