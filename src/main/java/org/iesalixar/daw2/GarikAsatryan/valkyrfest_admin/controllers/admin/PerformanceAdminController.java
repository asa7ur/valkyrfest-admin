package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Performance;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.ArtistService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.PerformanceService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.StageService;
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

import java.util.List;

@Controller
@RequestMapping("/admin/festival/performances")
@RequiredArgsConstructor
public class PerformanceAdminController {
    private final PerformanceService performanceService;
    private final ArtistService artistService;
    private final StageService stageService;
    private final MessageSource messageSource;

    /**
     * Lists all performances in the database
     */
    @GetMapping
    public String listPerformances(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {

        Page<Performance> performancePage = performanceService.getAllPerformances(searchTerm, pageable);

        model.addAttribute("performances", performancePage.getContent());
        PaginationUtils.setupPaginationModel(model, performancePage, pageable, searchTerm, "performances");
        return "admin/performances/list";
    }

    /**
     * Shows the form to create a new performance
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("performance", new Performance());
        model.addAttribute("artists", artistService.getAllArtists());
        model.addAttribute("stages", stageService.getAllStages());
        model.addAttribute("activePage", "performances");
        return "admin/performances/form";
    }

    /**
     * Shows the form to edit an existing performance
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        performanceService.getPerformanceById(id).ifPresent(p -> model.addAttribute("performance", p));
        model.addAttribute("artists", artistService.getAllArtists());
        model.addAttribute("stages", stageService.getAllStages());
        model.addAttribute("activePage", "performances");
        return "admin/performances/form";
    }

    /**
     * Saves or updates a performance
     */
    @PostMapping("/save")
    public String savePerformance(
            @Valid @ModelAttribute("performance") Performance performance,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("artists", artistService.getAllArtists());
            model.addAttribute("stages", stageService.getAllStages());
            model.addAttribute("activePage", "performances");
            return "admin/performances/form";
        }

        performanceService.savePerformance(performance);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.performance.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/performances";
    }

    /**
     * Deletes a performance by its ID
     */
    @GetMapping("/delete/{id}")
    public String deletePerformance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        performanceService.deletePerformance(id);

        redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("msg.admin.performance.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/performances";
    }
}
