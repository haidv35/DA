package com.haidv.lab.ecommerce.service.Impl;

import com.haidv.lab.ecommerce.domain.Order;
import com.haidv.lab.ecommerce.domain.OrderItem;
import com.haidv.lab.ecommerce.domain.Perfume;
import com.haidv.lab.ecommerce.repository.OrderItemRepository;
import com.haidv.lab.ecommerce.repository.OrderRepository;
import com.haidv.lab.ecommerce.repository.PerfumeRepository;
import com.haidv.lab.ecommerce.service.OrderService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PerfumeRepository perfumeRepository;
    private final MailSender mailSender;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Order>> getAllOrdersByQuery() {
        return dataFetchingEnvironment -> orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByEmailQuery() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email").toString();
            return orderRepository.findOrderByEmail(email);
        };
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByQuery() {
        return dataFetchingEnvironment -> {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return orderRepository.findOrderByEmail(email);
        };
    }

    @Override
    public DataFetcher<Order> getOrderByQuery() {
        return dataFetchingEnvironment -> {
            Long orderId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return orderRepository.findById(orderId).get();
        };
    }

    @Override
    public List<Order> findOrderByEmail(String email) {
        return orderRepository.findOrderByEmail(email);
    }

    @Override
    @Transactional
    public Order postOrder(Order validOrder, Map<Long, Long> perfumesId) {
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : perfumesId.entrySet()) {
            Perfume perfume = perfumeRepository.findById(entry.getKey()).get();
            OrderItem orderItem = new OrderItem();
            orderItem.setPerfume(perfume);
            orderItem.setAmount((perfume.getPrice() * entry.getValue()));
            orderItem.setQuantity(entry.getValue());
            orderItemList.add(orderItem);
            orderItemRepository.save(orderItem);
        }

        order.getOrderItems().addAll(orderItemList);
        order.setTotalPrice(validOrder.getTotalPrice());
        order.setFirstName(validOrder.getFirstName());
        order.setLastName(validOrder.getLastName());
        order.setCity(validOrder.getCity());
        order.setAddress(validOrder.getAddress());
        order.setPostIndex(validOrder.getPostIndex());
        order.setEmail(validOrder.getEmail());
        order.setPhoneNumber(validOrder.getPhoneNumber());
        orderRepository.save(order);

        StringBuilder perfumes = new StringBuilder();
        order.getOrderItems().forEach((orderItem) ->
        {
            perfumes.append(orderItem.getPerfume().getPerfumer());
            perfumes.append(" ");
            perfumes.append(orderItem.getPerfume().getPerfumeTitle());
            perfumes.append(" — $");
            perfumes.append(orderItem.getPerfume().getPrice());
            perfumes.append(".00 (quantity: ");
            perfumes.append(orderItem.getQuantity());
            perfumes.append(")");
            perfumes.append("\n");
        });

        String subject = "Order #" + order.getId();
        String message = "Hello " + order.getFirstName() + "!\n" +
                "Thank you for your order in Perfume online store.\n" +
                "Your order number is " + order.getId() + "\n" +
                "Date: " + order.getDate() + "\n" +
                "Name: " + order.getFirstName() + " " + order.getLastName() + "\n" +
                "Address: " + order.getCity() + ", " + order.getAddress() + "\n" +
                "Post index: " + order.getPostIndex() + "\n" +
                "Phone: " + order.getPhoneNumber() + "\n\n" +
                "Perfumes: " + "\n" + perfumes + "\n" +
                "Total price: $" + order.getTotalPrice();
        mailSender.send(order.getEmail(), subject, message);
        return order;
    }

    @Override
    @Transactional
    public List<Order> deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.getOrderItems().forEach(orderItem -> orderItemRepository.deleteById(orderItem.getId()));
        orderRepository.delete(order);
        return orderRepository.findAllByOrderByIdAsc();
    }
}
