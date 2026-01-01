package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities.*;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.CampingTypeRepository;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.OrderRepository;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.TicketTypeRepository;
import org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Page<Order> getAllOrders(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return orderRepository.searchOrders(searchTerm, pageable);
        }
        return orderRepository.findAll(pageable);
    }
}