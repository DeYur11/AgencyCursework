package org.example.advertisingagency.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.position.CreatePositionInput;
import org.example.advertisingagency.dto.position.UpdatePositionInput;
import org.example.advertisingagency.model.Position;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.PositionRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final WorkerRepository workerRepository;

    public PositionService(PositionRepository positionRepository, WorkerRepository workerRepository) {
        this.positionRepository = positionRepository;
        this.workerRepository = workerRepository;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
    }

    public Position createPosition(CreatePositionInput input) {
        Position position = new Position();
        position.setName(input.getName());
        return positionRepository.save(position);
    }

    public Position updatePosition(Integer id, UpdatePositionInput input) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));

        if (input.getName() != null) {
            position.setName(input.getName());
        }
        return positionRepository.save(position);
    }

    public boolean deletePosition(Integer id) {
        if (!positionRepository.existsById(id)) {
            return false;
        }
        positionRepository.deleteById(id);
        return true;
    }

    public List<Worker> getWorkersByPosition(Integer positionId) {
        return workerRepository.findAllByPosition_Id(positionId);
    }
}
