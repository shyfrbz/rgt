package com.rgt.order.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rgt.order.model.Order;
import com.rgt.order.repository.OrderRepository;

import jakarta.annotation.PostConstruct;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 주문번호 관리용 AtomicInteger
    private AtomicInteger orderCounter = new AtomicInteger(0); 

    // 주문번호 중 가장 높은 번호 찾아서
    // 새로운 주문번호를 설정하기 위한 메소드
    @PostConstruct
    public void orderCnt() {
        int maxOrderNum = orderRepository.allOrders()
                                         .stream()
                                         .mapToInt(Order::getOrderNum)
                                         .max()
                                         .orElse(0);
        orderCounter.set(maxOrderNum);
    }

    // 주문 처리 (유효성 검사)
    public void makeOrder(Order order) {
        if (order.getFood() == null || order.getFood().isEmpty()) {
            throw new IllegalArgumentException("주문하실 메뉴를 입력해주세요.");
        } else if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        } else if (order.getSeatNum() <= 0) {
            throw new IllegalArgumentException("올바른 좌석번호를 입력해주세요");
        }

        // 주문 번호 설정
        int num = orderCounter.incrementAndGet();
        order.setOrderNum(num);

        // 주문일시 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        order.setOrderTime(formattedDateTime);

        // 주문 저장
        orderRepository.makeOrder(order);
        
        // 주문 생성 후 WebSocket을 통해 새 주문 전송
        messagingTemplate.convertAndSend("/dashboard", order);
    }

    // 모든 주문 출력
    public List<Order> allOrders() {
        return orderRepository.allOrders();
    }

    // 주문 상태 업데이트
    public void updateOrderStatus(int id, String newStatus) {
        Order order = orderRepository.getOrderById(id); // 주문 조회
        if (order != null) {
            order.setStatus(newStatus); // 상태 업데이트
            orderRepository.updateOrder(order); // 변경사항 저장
        } else {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }
    }

    // 주문번호로 주문정보 가져옴
    public Order getOrderById(int id) {
        return orderRepository.getOrderById(id);
    }
}
