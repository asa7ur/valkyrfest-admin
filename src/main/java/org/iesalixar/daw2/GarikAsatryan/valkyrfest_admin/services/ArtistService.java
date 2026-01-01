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
     * Guarda un artista y procesa múltiples imágenes opcionales.
     */
    @Transactional
    public void saveArtist(Artist artist, MultipartFile[] imageFiles) throws IOException {
        // Primero guardamos el artista para asegurar que tiene un ID (si es nuevo)
        Artist savedArtist = artistRepository.save(artist);

        // Si se han subido archivos, los procesamos
        if (imageFiles != null && imageFiles.length > 0) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    // 1. Guardar el archivo físico
                    String fileName = fileService.saveFile(file, ARTISTS_FOLDER);

                    // 2. Crear el objeto de la entidad imagen
                    ArtistImage artistImage = new ArtistImage();
                    artistImage.setImageUrl(fileName);
                    artistImage.setArtist(savedArtist);

                    // 3. Persistir la relación
                    artistImageRepository.save(artistImage);
                }
            }
        }
    }

    /**
     * Borra un artista y todas sus imágenes físicas del disco.
     */
    @Transactional
    public void deleteArtist(Long id) {
        Optional<Artist> artistOpt = artistRepository.findById(id);
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();

            // Borrar los archivos físicos antes de borrar de la BD
            for (ArtistImage img : artist.getImages()) {
                fileService.deleteFile(img.getImageUrl(), ARTISTS_FOLDER);
            }

            // Borrar el artista (el cascade borrará las ArtistImage en la BD)
            artistRepository.delete(artist);
        }
    }

    /**
     * Borra una imagen específica de un artista.
     */
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