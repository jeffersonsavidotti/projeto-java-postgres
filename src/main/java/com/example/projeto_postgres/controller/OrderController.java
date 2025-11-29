package com.example.projeto_postgres.controller;

import com.example.projeto_postgres.model.*;
import com.example.projeto_postgres.repository.CustomerRepository;
import com.example.projeto_postgres.repository.OrderRepository;
import com.example.projeto_postgres.repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações CRUD de Pedidos
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * DTO para criar um pedido com itens
     */
    public static class CreateOrderDTO {
        @NotNull(message = "O ID do cliente é obrigatório")
        private Long customerId;
        
        private List<OrderItemDTO> items = new ArrayList<>();

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public List<OrderItemDTO> getItems() {
            return items;
        }

        public void setItems(List<OrderItemDTO> items) {
            this.items = items;
        }
    }

    /**
     * DTO para item do pedido
     */
    public static class OrderItemDTO {
        @NotNull(message = "O ID do produto é obrigatório")
        private Long productId;
        
        @Positive(message = "A quantidade deve ser maior que zero")
        @NotNull(message = "A quantidade é obrigatória")
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * CREATE - Criar um novo pedido
     * POST /orders
     * 
     * Body exemplo:
     * {
     *   "customerId": 1,
     *   "items": [
     *     {"productId": 1, "quantity": 2},
     *     {"productId": 2, "quantity": 1}
     *   ]
     * }
     */
    @Operation(summary = "Criar um novo pedido", description = "Cria um novo pedido com itens para um cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderDTO orderDTO) {
        // Busca o cliente
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Cria o pedido
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Order.OrderStatus.PENDING);

        // Cria os itens do pedido
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            items.add(item);
        }

        order.setItems(items);
        Order savedOrder = orderRepository.save(order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    /**
     * READ - Listar todos os pedidos
     * GET /orders
     */
    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista com todos os pedidos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        // Força o carregamento dos relacionamentos lazy
        orders.forEach(order -> {
            order.getItems().size(); // Carrega os itens
            order.getCustomer().getName(); // Carrega o cliente
        });
        return ResponseEntity.ok(orders);
    }

    /**
     * READ - Buscar um pedido por ID
     * GET /orders/{id}
     */
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        // Força o carregamento dos relacionamentos lazy
        order.getItems().size(); // Carrega os itens
        order.getCustomer().getName(); // Carrega o cliente
        return ResponseEntity.ok(order);
    }

    /**
     * READ - Buscar pedidos de um cliente
     * GET /orders/customer/{customerId}
     */
    @Operation(summary = "Buscar pedidos de um cliente", description = "Retorna todos os pedidos de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/customer/{customerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        // Força o carregamento dos relacionamentos lazy
        orders.forEach(order -> {
            order.getItems().size(); // Carrega os itens
            order.getCustomer().getName(); // Carrega o cliente
        });
        return ResponseEntity.ok(orders);
    }

    /**
     * UPDATE - Atualizar status do pedido
     * PUT /orders/{id}/status
     * 
     * Body exemplo:
     * {
     *   "status": "CONFIRMED"
     * }
     */
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id, 
            @RequestBody OrderStatusDTO statusDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusDTO.getStatus().toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido. Valores válidos: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED");
        }

        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * DTO para atualizar status
     */
    public static class OrderStatusDTO {
        @NotNull(message = "O status é obrigatório")
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * DELETE - Deletar um pedido
     * DELETE /orders/{id}
     */
    @Operation(summary = "Deletar pedido", description = "Remove um pedido do sistema pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID do pedido", required = true) @PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado");
        }
        
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

