package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${upload.directory:uploads}")
    private String uploadDirectory;

    /**
     * Guarda un archivo en el sistema de archivos.
     *
     * @param file     El archivo recibido desde el formulario.
     * @param subFolder Subcarpeta dentro de uploads (ej: "artists" o "sponsors").
     * @return El nombre del archivo guardado.
     * @throws IOException Si ocurre un error al guardar.
     */
    public String saveFile(MultipartFile file, String subFolder) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Crear la ruta del directorio (uploads/artists, etc)
        Path uploadPath = Paths.get(uploadDirectory, subFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar un nombre único para evitar duplicados
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Guardar el archivo físically
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }

    /**
     * Elimina un archivo físicamente del disco.
     *
     * @param fileName Nombre del archivo.
     * @param subFolder Subcarpeta donde está guardado.
     */
    public void deleteFile(String fileName, String subFolder) {
        if (fileName == null || fileName.isEmpty()) return;

        try {
            Path filePath = Paths.get(uploadDirectory, subFolder).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Loguear error pero no detener la ejecución
            System.err.println("Error eliminando archivo: " + fileName);
        }
    }
}