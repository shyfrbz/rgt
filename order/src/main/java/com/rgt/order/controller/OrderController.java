package com.rgt.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rgt.order.model.Order;
import com.rgt.order.service.OrderService;

@RestController
public class OrderController {
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate; // 웹소켓 메시지 전송 템플릿

	// 주문 처리
	@PostMapping("/order")
    public ResponseEntity<String> makeOrder(@RequestBody Order order) {
        try {
            orderService.makeOrder(order);
            
            // 클라이언트로 새 주문 정보 전송
            // dashboard는 WebSocketConfig에 설정한 구독 경로
            messagingTemplate.convertAndSend("/topic/dashboard", order); 
            
            return ResponseEntity.status(HttpStatus.CREATED).body("주문이 접수되었습니다.");
       
        } catch (IllegalArgumentException e) {
        	// 유효성 검사 실패 시
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	// 모든 주문 출력
	@GetMapping("/allOrders")
	public List<Order> allOrders() {
	    return orderService.allOrders();
	}
	
	// 주문 상태 변경
	@PutMapping("/order/{id}/status")
	public ResponseEntity<String> updateOrderStatus(@PathVariable("id") int id, @RequestBody Map<String, String> request) {
	    String newStatus = request.get("status");
	    try {
	        orderService.updateOrderStatus(id, newStatus);
	        Order updatedOrder = orderService.getOrderById(id);
	        if (updatedOrder != null) {
	            messagingTemplate.convertAndSend("/dashboard", updatedOrder); 
	            return ResponseEntity.ok("주문 상태가 업데이트되었습니다.");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주문을 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}


	

}
