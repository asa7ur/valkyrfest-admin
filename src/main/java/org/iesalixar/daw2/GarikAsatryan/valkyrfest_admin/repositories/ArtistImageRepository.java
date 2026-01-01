package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories;

import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.ArtistImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistImageRepository extends JpaRepository<ArtistImage, Long> {

    /**
     * Busca todas las imágenes asociadas a un artista específico por su ID.
     * * @param artistId ID del artista.
     * @return Lista de imágenes del artista.
     */
    List<ArtistImage> findByArtistId(Long artistId);
}