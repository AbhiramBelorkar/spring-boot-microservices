package com.microservices.order.service;

import com.microservices.order.config.WebClientConfig;
import com.microservices.order.dto.InventoryReponse;
import com.microservices.order.dto.OrderLineItemsDto;
import com.microservices.order.dto.OrderRequest;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderLineItems;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCode = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkeuCode)
                .toList();

//        Call inventory service and place order if product is in stock
        InventoryReponse[] inventoryReponsesArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCode).build())
                .retrieve()
                .bodyToMono(InventoryReponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryReponsesArray)
                .allMatch(InventoryReponse::isInStock);

        if(allProductsInStock){
            orderRepository.save(order);
        } else{
            throw new IllegalArgumentException("Product is not in stock, Please try again later.");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkeuCode(orderLineItemsDto.getSkeuCode());
        return orderLineItems;
    }
}
