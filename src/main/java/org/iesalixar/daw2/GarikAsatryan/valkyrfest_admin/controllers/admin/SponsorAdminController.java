package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Sponsor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.SponsorService;
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
@RequestMapping("/admin/festival/sponsors")
@RequiredArgsConstructor
public class SponsorAdminController {

    private final SponsorService sponsorService;
    private final MessageSource messageSource;

    /**
     * Lists all sponsors in the database
     */
    @GetMapping
    public String listSponsors(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {

        Page<Sponsor> sponsorPage = sponsorService.getAllSponsors(searchTerm, pageable);

        model.addAttribute("sponsors", sponsorPage.getContent());
        PaginationUtils.setupPaginationModel(model, sponsorPage, pageable, searchTerm, "sponsors");
        return "admin/sponsors/list";
    }

    /**
     * Shows the form to create a new sponsor
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("sponsor", new Sponsor());
        model.addAttribute("activePage", "sponsors");
        return "admin/sponsors/form";
    }

    /**
     * Shows the form to edit an existing sponsor
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        sponsorService.getSponsorById(id).ifPresent(s -> model.addAttribute("sponsor", s));
        model.addAttribute("activePage", "sponsors");
        return "admin/sponsors/form";
    }

    /**
     * Saves or updates a sponsor
     */
    @PostMapping("/save")
    public String saveSponsor(
            @Valid @ModelAttribute("sponsor") Sponsor sponsor,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "sponsors");
            return "admin/sponsors/form";
        }

        sponsorService.saveSponsor(sponsor);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.sponsor.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/sponsors";
    }

    /**
     * Deletes a sponsor by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteSponsor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sponsorService.deleteSponsor(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.sponsor.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/sponsors";
    }
}