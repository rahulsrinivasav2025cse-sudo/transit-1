package com.example.transit.service;

import com.example.transit.dto.PurchaseOrderRequest;
import com.example.transit.entity.Contact;
import com.example.transit.entity.Product;
import com.example.transit.entity.PurchaseOrder;
import com.example.transit.entity.enums.OrderStatus;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ContactService contactService;
    private final ProductService productService;

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request) {
        Contact vendor = contactService.getContactById(request.getVendorId());
        Product product = productService.getProductById(request.getProductId());

        BigDecimal unitPrice = request.getUnitPrice() != null ? request.getUnitPrice() : product.getUnitPrice();
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .vendor(vendor)
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalAmount(total)
                .status(OrderStatus.CONFIRMED)
                .orderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now())
                .build();

        return purchaseOrderRepository.save(po);
    }

    public List<PurchaseOrder> getAll() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + id));
    }
}
