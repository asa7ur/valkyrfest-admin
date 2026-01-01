package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Stage;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.StageService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/festival/stages")
@RequiredArgsConstructor
public class StageAdminController {
    private final StageService stageService;
    private final MessageSource messageSource;

    /**
     * Lists all stages in the database
     */
    @GetMapping
    public String listStages(Model model) {
        model.addAttribute("stages", stageService.getAllStages());
        model.addAttribute("activePage", "stages");
        return "admin/stages/list";
    }

    /**
     * Shows the form to add a new stage
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("stage", new Stage());
        model.addAttribute("activePage", "stages");
        return "admin/stages/form";
    }

    /**
     * Shows the form to edit an existing stage
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        stageService.getStageById(id).ifPresent(stage -> model.addAttribute("stage", stage));
        model.addAttribute("activePage", "stages");
        return "admin/stages/form";
    }

    /**
     * Saves or updates a stage
     */
    @PostMapping("/save")
    public String saveStage(
            @Valid @ModelAttribute("stage") Stage stage,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "stages");
            return "admin/stages/form";
        }

        stageService.saveStage(stage);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.stage.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/stages";
    }

    /**
     * Deletes a stage by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteStage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        stageService.deleteStage(id);

        redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("msg.admin.stage.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/stages";
    }
}
