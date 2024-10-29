package com.rgt.order.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.rgt.order.model.Order;

@Repository
public class OrderRepository {

	// 저장 공간
    private List<Order> orders = new ArrayList<>();

    // 주문 처리
    public void makeOrder(Order order) {
        orders.add(order);
        System.out.println(orders.get(orders.size() - 1).getFood());
    }

    // 모든 주문 출력
    public List<Order> allOrders() {
        return orders;
    }

    // 주문번호로 주문 찾기
    public Order getOrderById(int id) {
        return orders.stream().filter(order -> order.getOrderNum() == id).findFirst().orElse(null);
    }

    // 주문 상태 업데이트
    public void updateOrder(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNum() == updatedOrder.getOrderNum()) {
                orders.set(i, updatedOrder);
                break;
            }
        }
    }
}
