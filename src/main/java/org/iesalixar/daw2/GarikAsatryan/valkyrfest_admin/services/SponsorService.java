package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Sponsor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.SponsorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SponsorService {

    private final SponsorRepository sponsorRepository;
    private final FileService fileService;

    private static final String SPONSORS_FOLDER = "sponsors";

    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }

    public Optional<Sponsor> getSponsorById(Long id) {
        return sponsorRepository.findById(id);
    }

    /**
     * Guarda un patrocinador y gestiona su imagen única.
     * Si ya tenía una imagen y se sube una nueva, borra la antigua del disco.
     */
    @Transactional
    public void saveSponsor(Sponsor sponsor, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Si estamos editando y ya tenía una imagen, la borramos del disco
            if (sponsor.getId() != null) {
                Optional<Sponsor> existingSponsor = sponsorRepository.findById(sponsor.getId());
                if (existingSponsor.isPresent() && existingSponsor.get().getImage() != null) {
                    fileService.deleteFile(existingSponsor.get().getImage(), SPONSORS_FOLDER);
                }
            }

            // Guardar el nuevo archivo físico
            String fileName = fileService.saveFile(imageFile, SPONSORS_FOLDER);
            sponsor.setImage(fileName);
        } else if (sponsor.getId() != null) {
            // Si no se sube imagen nueva en una edición, mantenemos la que ya existía en la BD
            sponsorRepository.findById(sponsor.getId()).ifPresent(existing -> {
                if (sponsor.getImage() == null) {
                    sponsor.setImage(existing.getImage());
                }
            });
        }

        sponsorRepository.save(sponsor);
    }

    /**
     * Borra un patrocinador y su imagen física del disco.
     */
    @Transactional
    public void deleteSponsor(Long id) {
        Optional<Sponsor> sponsorOpt = sponsorRepository.findById(id);
        if (sponsorOpt.isPresent()) {
            Sponsor sponsor = sponsorOpt.get();
            if (sponsor.getImage() != null) {
                fileService.deleteFile(sponsor.getImage(), SPONSORS_FOLDER);
            }
            sponsorRepository.delete(sponsor);
        }
    }

    public Page<Sponsor> getAllSponsors(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return sponsorRepository.searchSponsors(searchTerm, pageable);
        }
        return sponsorRepository.findAll(pageable);
    }
}