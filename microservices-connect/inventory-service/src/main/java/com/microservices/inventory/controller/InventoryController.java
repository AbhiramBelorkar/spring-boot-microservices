package com.microservices.inventory.controller;

import com.microservices.inventory.dto.InventoryReponse;
import com.microservices.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /*Path variables will be added, separated by comma - http:localhost:8082/api/inventory/iphone-13,iphone-13-red
        because a single order may contains list of different order, if a order comes with multiple skucode products
        in list we should make use Request parameters
        Request Parameter - http:localhost:8082/api/inventory/sku-code=iphone-13&sku-code=iphone-13-red

        @GetMapping("/{sku-Code}")
        @ResponseStatus(HttpStatus.OK)
        public boolean isInStock(@PathVariable("sku-code") String skuCode){
            return inventoryService.isInStock(skuCode);
        }
    */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryReponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
