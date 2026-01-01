package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.components;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.PropertyImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("imgHelper") // El nombre "imgHelper" es como lo llamaremos en el HTML
public class ImageHelper {

    public String getImagesAsString(List<ArtistImage> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        // Convierte la lista de objetos Image a un String separado por comas
        return images.stream()
                .map(ArtistImage::getFileName)
                .collect(Collectors.joining(","));
    }
}