package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.DocumentType;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Ticket;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.TicketStatus;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.OrderService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.TicketService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.TicketTypeService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/festival/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class TicketAdminController {
    private static final Logger logger = LoggerFactory.getLogger(TicketAdminController.class);

    private final TicketService ticketService;
    private final TicketTypeService ticketTypeService;
    private final OrderService orderService;
    private final MessageSource messageSource;

    /**
     * Lists all tickets in the database
     */
    @GetMapping
    public String listTickets(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {
        logger.info("Listing tickets...");

        Page<Ticket> ticketPage = ticketService.getAllTickets(searchTerm, pageable);

        logger.info("Found {} tickets.", ticketPage.getTotalElements());
        model.addAttribute("tickets", ticketPage.getContent());
        PaginationUtils.setupPaginationModel(model, ticketPage, pageable, searchTerm, "tickets");

        return "admin/tickets/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        populateFormModels(model);
        return "admin/tickets/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ticketService.getTicketById(id).ifPresent(t -> model.addAttribute("ticket", t));
        populateFormModels(model);
        return "admin/tickets/form";
    }

    @PostMapping("/save")
    public String saveTicket(
            @Valid @ModelAttribute("ticket") Ticket ticket,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            populateFormModels(model);
            return "admin/tickets/form";
        }

        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.ticket.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/tickets";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ticketService.deleteTicket(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.ticket.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/tickets";
    }

    private void populateFormModels(Model model) {
        model.addAttribute("ticketTypes", ticketTypeService.getAllTicketTypes());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("docTypes", DocumentType.values());
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("activePage", "tickets");
    }
}