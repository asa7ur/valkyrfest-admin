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

    public Page<Artist> getAllArtists(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return artistRepository.searchArtists(searchTerm, pageable);
        }
        return artistRepository.findAll(pageable);
    }

    /**
     * Guarda un artista gestionando su logo (único) y sus imágenes (múltiples y aditivas).
     */
    @Transactional
    public void saveArtist(Artist artist, MultipartFile logoFile, MultipartFile[] imageFiles) throws IOException {
        Artist artistToSave;

        if (artist.getId() != null) {
            // 1. Recuperamos la versión actual de la base de datos
            artistToSave = artistRepository.findById(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist not found"));

            // 2. Actualizamos solo los campos de texto
            artistToSave.setName(artist.getName());
            artistToSave.setPhone(artist.getPhone());
            artistToSave.setEmail(artist.getEmail());
            artistToSave.setGenre(artist.getGenre());
            artistToSave.setCountry(artist.getCountry());

            // Al usar el objeto recuperado 'artistToSave', mantenemos sus listas
            // de 'images' y 'performances' originales, evitando que Hibernate las borre.
        } else {
            // Es un artista nuevo
            artistToSave = artist;
        }

        // 3. Gestión del LOGO
        if (logoFile != null && !logoFile.isEmpty()) {
            // Si ya tenía un logo, lo borramos del disco
            if (artistToSave.getLogo() != null) {
                fileService.deleteFile(artistToSave.getLogo(), ARTISTS_FOLDER);
            }
            String logoName = fileService.saveFile(logoFile, ARTISTS_FOLDER);
            artistToSave.setLogo(logoName);
        }
        // Si no se sube logo nuevo, 'artistToSave' ya mantiene el que tenía de la DB

        // 4. Guardamos el artista (esto preserva las relaciones existentes)
        Artist savedArtist = artistRepository.save(artistToSave);

        // 5. Gestión de nuevas IMÁGENES (Añadir sin borrar las anteriores)
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
    public void deleteLogo(Long id) {
        artistRepository.findById(id).ifPresent(artist -> {
            if (artist.getLogo() != null) {
                fileService.deleteFile(artist.getLogo(), ARTISTS_FOLDER);
                artist.setLogo(null);
                artistRepository.save(artist);
            }
        });
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
}