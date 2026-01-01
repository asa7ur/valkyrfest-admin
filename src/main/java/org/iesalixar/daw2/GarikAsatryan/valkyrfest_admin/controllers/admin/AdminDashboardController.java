package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ArtistService artistService;
    private final StageService stageService;
    private final PerformanceService performanceService;
    private final UserService userService;
    private final OrderService orderService;
    private final TicketService ticketService;
    private final CampingService campingService;
    private final SponsorService sponsorService;

    /**
     * Shows the main Dashboard view with statistical counters.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Obtains the total counts for the dashboard “cards.”
        model.addAttribute("artistCount", artistService.getAllArtists().size());
        model.addAttribute("stageCount", stageService.getAllStages().size());
        model.addAttribute("performanceCount", performanceService.getAllPerformances().size());
        model.addAttribute("userCount", userService.getAllUsers().size());
        model.addAttribute("orderCount", orderService.getAllOrders().size());
        model.addAttribute("ticketCount", ticketService.getAllTickets().size());
        model.addAttribute("campingCount", campingService.getAllCampings().size());
        model.addAttribute("sponsorCount", sponsorService.getAllSponsors().size());

        // Attribute to mark the active link in the side menu.
        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }
}
