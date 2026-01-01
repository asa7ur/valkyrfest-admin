package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Artist;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.ArtistImage;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.ArtistImageRepository;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistImageRepository artistImageRepository;
    private final FileService fileService;

    private static final String ARTISTS_FOLDER = "artists";

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    /**
     * Guarda un artista gestionando su logo (único) y sus imágenes (múltiples y aditivas).
     */
    @Transactional
    public void saveArtist(Artist artist, MultipartFile logoFile, MultipartFile[] imageFiles) throws IOException {
        // 1. Gestión del LOGO (Funciona como el del Sponsor: uno solo que se reemplaza)
        if (logoFile != null && !logoFile.isEmpty()) {
            if (artist.getId() != null) {
                artistRepository.findById(artist.getId()).ifPresent(existing -> {
                    if (existing.getLogo() != null) {
                        fileService.deleteFile(existing.getLogo(), ARTISTS_FOLDER);
                    }
                });
            }
            String logoName = fileService.saveFile(logoFile, ARTISTS_FOLDER);
            artist.setLogo(logoName);
        } else if (artist.getId() != null) {
            artistRepository.findById(artist.getId()).ifPresent(existing -> {
                if (artist.getLogo() == null) {
                    artist.setLogo(existing.getLogo());
                }
            });
        }

        Artist savedArtist = artistRepository.save(artist);

        if (imageFiles != null && imageFiles.length > 0) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String fileName = fileService.saveFile(file, ARTISTS_FOLDER);
                    ArtistImage artistImage = new ArtistImage();
                    artistImage.setImageUrl(fileName);
                    artistImage.setArtist(savedArtist);
                    artistImageRepository.save(artistImage);
                }
            }
        }
    }

    @Transactional
    public void deleteArtist(Long id) {
        Optional<Artist> artistOpt = artistRepository.findById(id);
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            if (artist.getLogo() != null) {
                fileService.deleteFile(artist.getLogo(), ARTISTS_FOLDER);
            }
            for (ArtistImage img : artist.getImages()) {
                fileService.deleteFile(img.getImageUrl(), ARTISTS_FOLDER);
            }
            artistRepository.delete(artist);
        }
    }

    @Transactional
    public void deleteArtistImage(Long imageId) {
        Optional<ArtistImage> imgOpt = artistImageRepository.findById(imageId);
        if (imgOpt.isPresent()) {
            ArtistImage img = imgOpt.get();
            fileService.deleteFile(img.getImageUrl(), ARTISTS_FOLDER);
            artistImageRepository.delete(img);
        }
    }

    public Page<Artist> getAllArtists(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return artistRepository.searchArtists(searchTerm, pageable);
        }
        return artistRepository.findAll(pageable);
    }
}