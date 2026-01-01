package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Camping;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.DocumentType;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.TicketStatus;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.CampingService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.CampingTypeService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.OrderService;
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
@RequestMapping("/admin/festival/campings")
@RequiredArgsConstructor
public class CampingAdminController {

    private final CampingService campingService;
    private final CampingTypeService campingTypeService;
    private final OrderService orderService;
    private final MessageSource messageSource;

    /**
     * Lists all camping tickets in the database
     */
    @GetMapping
    public String listCampings(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {
        Page<Camping> campingPage = campingService.getAllCampings(searchTerm, pageable);

        model.addAttribute("campings", campingPage.getContent());
        PaginationUtils.setupPaginationModel(model, campingPage, pageable, searchTerm, "campings");
        return "admin/campings/list";
    }

    /**
     * Shows the form to create a new camping ticket
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("camping", new Camping());
        populateFormModels(model);
        return "admin/campings/form";
    }

    /**
     * Shows the form to edit an existing camping ticket
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        campingService.getCampingById(id).ifPresent(c -> model.addAttribute("camping", c));
        populateFormModels(model);
        return "admin/campings/form";
    }

    /**
     * Saves or updates a camping ticket
     */
    @PostMapping("/save")
    public String saveCamping(
            @Valid @ModelAttribute("camping") Camping camping,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            populateFormModels(model);
            return "admin/campings/form";
        }

        campingService.saveCamping(camping);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.camping.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/campings";
    }

    /**
     * Deletes a camping ticket by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteCamping(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        campingService.deleteCamping(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.camping.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/campings";
    }

    private void populateFormModels(Model model) {
        model.addAttribute("campingTypes", campingTypeService.getAllCampingTypes());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("docTypes", DocumentType.values());
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("activePage", "campings");
    }
}