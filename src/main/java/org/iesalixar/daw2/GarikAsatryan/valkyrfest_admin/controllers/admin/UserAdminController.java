package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.User;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.RoleService;
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
@RequestMapping("/admin/festival/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final MessageSource messageSource;

    /**
     * Lists all users in the database
     */
    @GetMapping
    public String listUsers(
            String searchTerm,
            @PageableDefault Pageable pageable,
            Model model) {

        Page<User> userPage = userService.getAllUsers(searchTerm, pageable);

        model.addAttribute("users", userPage.getContent());
        PaginationUtils.setupPaginationModel(model, userPage, pageable, searchTerm, "users");
        return "admin/users/list";
    }

    /**
     * Shows the form to create a new user
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("activePage", "users");
        return "admin/users/form";
    }

    /**
     * Shows the form to edit an existing user
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        userService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("activePage", "users");
        return "admin/users/form";
    }

    /**
     * Saves or updates a user
     */
    @PostMapping("/save")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "users");
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/users/form";
        }

        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.user.save.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/users";
    }

    /**
     * Deletes a user by its ID
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.admin.user.delete.success", null, LocaleContextHolder.getLocale()));

        return "redirect:/admin/festival/users";
    }
}
