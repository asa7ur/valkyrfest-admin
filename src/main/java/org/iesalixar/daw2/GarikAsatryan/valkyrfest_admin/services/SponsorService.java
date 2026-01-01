package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Sponsor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.SponsorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SponsorService {
    private final SponsorRepository sponsorRepository;

    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }

    public Optional<Sponsor> getSponsorById(Long id) {
        return sponsorRepository.findById(id);
    }

    @Transactional
    public void saveSponsor(Sponsor sponsor) {
        sponsorRepository.save(sponsor);
    }

    @Transactional
    public void deleteSponsor(Long id) {
        sponsorRepository.deleteById(id);
    }

    public Page<Sponsor> getAllSponsors(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return sponsorRepository.searchSponsors(searchTerm, pageable);
        }
        return sponsorRepository.findAll(pageable);
    }
}
