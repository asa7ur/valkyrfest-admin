package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Artist;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.ArtistService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.utils.PaginationUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/festival/artists")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ArtistAdminController {
    private final ArtistService artistService;
    private final MessageSource messageSource;

    @GetMapping
    public String listArtists(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {
        Page<Artist> artistPage = artistService.getAllArtists(searchTerm, pageable);
        model.addAttribute("artists", artistPage.getContent());
        PaginationUtils.setupPaginationModel(model, artistPage, pageable, searchTerm, "artists");
        return "admin/artists/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        model.addAttribute("activePage", "artists");
        return "admin/artists/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        artistService.getArtistById(id).ifPresent(artist -> model.addAttribute("artist", artist));
        model.addAttribute("activePage", "artists");
        return "admin/artists/form";
    }

    @PostMapping("/save")
    public String saveArtist(
            @Valid @ModelAttribute("artist") Artist artist,
            BindingResult result,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "artists");
            return "admin/artists/form";
        }

        try {
            artistService.saveArtist(artist, logoFile, imageFiles);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.admin.artist.save.success", null, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.admin.error.file_save", null, LocaleContextHolder.getLocale()));
            model.addAttribute("activePage", "artists");
            return "admin/artists/form";
        }

        return "redirect:/admin/festival/artists";
    }
    
    @GetMapping("/delete-logo/{id}")
    public String deleteLogo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        artistService.deleteLogo(id);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.artist.logo.delete.success", null, LocaleContextHolder.getLocale()));
        return "redirect:/admin/festival/artists/edit/" + id;
    }


    @GetMapping("/delete-image/{artistId}/{imageId}")
    public String deleteImage(@PathVariable Long artistId, @PathVariable Long imageId, RedirectAttributes redirectAttributes) {
        artistService.deleteArtistImage(imageId);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.artist.image.delete.success", null, LocaleContextHolder.getLocale()));
        return "redirect:/admin/festival/artists/edit/" + artistId;
    }

    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        artistService.deleteArtist(id);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.artist.delete.success", null, LocaleContextHolder.getLocale()));
        return "redirect:/admin/festival/artists";
    }
}