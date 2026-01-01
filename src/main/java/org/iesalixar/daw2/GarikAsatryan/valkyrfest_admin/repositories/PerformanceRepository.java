package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories;

import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("SELECT p FROM Performance p WHERE " +
            "LOWER(p.artist.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.stage.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Performance> searchPerformances(@Param("searchTerm") String searchTerm, Pageable pageable);
}
