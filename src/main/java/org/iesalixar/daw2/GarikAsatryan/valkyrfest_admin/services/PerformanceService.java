package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Performance;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.PerformanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    public Optional<Performance> getPerformanceById(Long id) {
        return performanceRepository.findById(id);
    }

    @Transactional
    public void savePerformance(Performance performance) {
        performanceRepository.save(performance);
    }

    @Transactional
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }

    public Page<Performance> getAllPerformances(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return performanceRepository.searchPerformances(searchTerm, pageable);
        }
        return performanceRepository.findAll(pageable);
    }
}
