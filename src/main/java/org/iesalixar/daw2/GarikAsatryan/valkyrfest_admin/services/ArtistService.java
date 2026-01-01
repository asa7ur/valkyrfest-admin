package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Artist;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    @Transactional
    public void saveArtist(Artist artist) {
        artistRepository.save(artist);
    }

    @Transactional
    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }

    public Page<Artist> getAllArtists(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return artistRepository.searchArtists(searchTerm, pageable);
        }
        return artistRepository.findAll(pageable);
    }
}
