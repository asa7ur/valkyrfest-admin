package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.CampingType;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.CampingTypeService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/festival/camping-types")
@RequiredArgsConstructor
public class CampingTypeAdminController {

    private final CampingTypeService campingTypeService;
    private final MessageSource messageSource;

    /**
     * Lista todas las opciones de camping disponibles.
     */
    @GetMapping
    public String listCampingTypes(Model model) {
        model.addAttribute("campingTypes", campingTypeService.getAllCampingTypes());
        model.addAttribute("activePage", "campingTypes");
        return "admin/camping_types/list";
    }

    /**
     * Muestra el formulario para crear una nueva modalidad de camping.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("campingType", new CampingType());
        model.addAttribute("activePage", "campingTypes");
        return "admin/camping_types/form";
    }

    /**
     * Muestra el formulario para editar una modalidad existente.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        campingTypeService.getCampingTypeById(id).ifPresent(type -> model.addAttribute("campingType", type));
        model.addAttribute("activePage", "campingTypes");
        return "admin/camping_types/form";
    }

    /**
     * Guarda o actualiza la modalidad de camping.
     */
    @PostMapping("/save")
    public String saveCampingType(
            @Valid @ModelAttribute("campingType") CampingType campingType,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // La validación incluye la restricción de stockAvailable <= stockTotal
        if (result.hasErrors()) {
            model.addAttribute("activePage", "campingTypes");
            return "admin/camping_types/form";
        }

        campingTypeService.saveCampingType(campingType);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.camping_type.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/camping-types";
    }

    /**
     * Elimina una modalidad de camping.
     */
    @GetMapping("/delete/{id}")
    public String deleteCampingType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        campingTypeService.deleteCampingType(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.camping_type.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/camping-types";
    }
}