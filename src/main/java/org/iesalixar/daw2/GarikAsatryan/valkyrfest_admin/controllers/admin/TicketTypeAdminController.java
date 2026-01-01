package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.TicketType;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.TicketTypeService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/festival/ticket-types")
@RequiredArgsConstructor
public class TicketTypeAdminController {

    private final TicketTypeService ticketTypeService;
    private final MessageSource messageSource;

    /**
     * Lista todos los tipos de entradas disponibles.
     */
    @GetMapping
    public String listTicketTypes(Model model) {
        model.addAttribute("ticketTypes", ticketTypeService.getAllTicketTypes());
        model.addAttribute("activePage", "ticketTypes");
        return "admin/ticket_types/list";
    }

    /**
     * Muestra el formulario para crear un nuevo tipo de entrada.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ticketType", new TicketType());
        model.addAttribute("activePage", "ticketTypes");
        return "admin/ticket_types/form";
    }

    /**
     * Muestra el formulario para editar un tipo de entrada existente.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ticketTypeService.getTicketTypeById(id).ifPresent(type -> model.addAttribute("ticketType", type));
        model.addAttribute("activePage", "ticketTypes");
        return "admin/ticket_types/form";
    }

    /**
     * Guarda o actualiza un tipo de entrada.
     */
    @PostMapping("/save")
    public String saveTicketType(
            @Valid @ModelAttribute("ticketType") TicketType ticketType,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "ticketTypes");
            return "admin/ticket_types/form";
        }

        ticketTypeService.saveTicketType(ticketType);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.ticket_type.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/ticket-types";
    }

    /**
     * Elimina un tipo de entrada por su ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteTicketType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ticketTypeService.deleteTicketType(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.ticket_type.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/ticket-types";
    }
}