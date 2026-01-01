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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/festival/artists")
@RequiredArgsConstructor
public class ArtistAdminController {
    private final ArtistService artistService;
    private final MessageSource messageSource;

    /**
     * Lists all artists in the database
     */
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

    /**
     * Shows the form to create a new artist
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        model.addAttribute("activePage", "artists");
        return "admin/artists/form";
    }

    /**
     * Shows the form to edit an existing artist
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        artistService.getArtistById(id).ifPresent(artist -> model.addAttribute("artist", artist));
        model.addAttribute("activePage", "artists");
        return "admin/artists/form";
    }

    /**
     * Saves or updates an artist
     */
    @PostMapping("/save")
    public String saveArtist(
            @Valid @ModelAttribute("artist") Artist artist,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "artists");
            return "admin/artists/form";
        }

        artistService.saveArtist(artist);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.artist.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/artists";
    }

    /**
     * Deletes an artist by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        artistService.deleteArtist(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.artist.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/artists";
    }
}































