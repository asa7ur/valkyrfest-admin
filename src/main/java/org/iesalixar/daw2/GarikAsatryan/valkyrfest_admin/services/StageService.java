package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Stage;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.StageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;

    public List<Stage> getAllStages() {
        return stageRepository.findAll();
    }

    public Optional<Stage> getStageById(Long id) {
        return stageRepository.findById(id);
    }

    @Transactional
    public void saveStage(Stage stage) {
        stageRepository.save(stage);
    }

    @Transactional
    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }
}
