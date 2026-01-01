package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.Order;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.OrderStatus;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.OrderService;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.UserService;
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
@RequestMapping("/admin/festival/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final MessageSource messageSource;

    /**
     * Lists all orders in the database
     */
    @GetMapping
    public String listOrders(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {

        Page<Order> orderPage = orderService.getAllOrders(searchTerm, pageable);

        model.addAttribute("orders", orderPage.getContent());
        PaginationUtils.setupPaginationModel(model, orderPage, pageable, searchTerm, "orders");
        return "admin/orders/list";
    }

    /**
     * Shows the form to create a new order
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("activePage", "orders");
        return "admin/orders/form";
    }

    /**
     * Shows the form to edit an existing order
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        orderService.getOrderById(id).ifPresent(order -> model.addAttribute("order", order));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("activePage", "orders");
        return "admin/orders/form";
    }

    /**
     * Saves or updates an order
     */
    @PostMapping("/save")
    public String saveOrder(
            @Valid @ModelAttribute("order") Order order,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("statuses", OrderStatus.values());
            model.addAttribute("activePage", "orders");
            return "admin/orders/form";
        }

        orderService.saveOrder(order);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.order.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/orders";
    }

    /**
     * Deletes an order by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteOrder(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.order.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/orders";
    }
}