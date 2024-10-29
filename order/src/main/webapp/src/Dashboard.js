import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function Dashboard() {
    const [orders, setOrders] = useState([]);

    const statusOptions = ['주문접수', '조리중', '조리완료']; // 가능 상태 목록

    // 주문 목록을 가져오는 함수
    const fetchOrders = async () => {
        try {
            const response = await fetch('http://localhost:8080/allOrders'); // 서버에서 모든 주문을 가져옴
            if (response.ok) {
                const data = await response.json(); // JSON 형태로 변환
                setOrders(data); // 가져온 주문 목록으로 상태 업데이트
            } else {
                console.error('주문을 가져오는 데 실패했습니다.');
            }
        } catch (error) {
            console.error('오류 발생:', error);
        }
    };

    useEffect(() => {
        // 컴포넌트가 마운트될 때 주문 목록 가져오기
        fetchOrders();

        // 웹소켓 연결 설정
		const socket = new SockJS('http://localhost:8080/ws/dashboard'); // 변경된 URL로 수정
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
				client.subscribe('/topic/dashboard', (message) => {
				    const newOrder = JSON.parse(message.body);
				    setOrders((prevOrders) => [...prevOrders, newOrder]);
				});
            },
            onWebSocketError: (event) => {
                console.error('WebSocket 오류:', event);
            },
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

	const updateOrderStatus = async (orderNum, newStatus) => {
	    console.log(`주문 ${orderNum}의 상태를 ${newStatus}로 변경합니다.`);
	    try {
	        const response = await fetch(`http://localhost:8080/order/${orderNum}/status`, {
	            method: 'PUT',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify({ status: newStatus }),
	        });
	        if (response.ok) {
	            setOrders((prevOrders) =>
	                prevOrders.map((order) =>
	                    order.orderNum === orderNum ? { ...order, status: newStatus } : order
	                )
	            );
	        } else {
	            console.error('주문 상태 업데이트 실패:', response.statusText);
	        }
	    } catch (error) {
	        console.error('오류 발생:', error);
	    }
	};


    return (
        <div>
            <h1>실시간 주문 현황</h1>
            <table id="dashTbl">
                <thead>
                    <tr>
                        <th>주문번호</th>
                        <th>메뉴명</th>
                        <th>수량</th>
                        <th>좌석번호</th>
                        <th>주문일시</th>
                        <th>상태</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map((order) => (
                        <tr key={order.orderNum}>
                            <td>{order.orderNum}</td>
                            <td>{order.food}</td>
                            <td>{order.quantity}개</td>
                            <td>{order.seatNum}</td>
                            <td>{order.orderTime}</td>
                            <td>
                                <select 
                                    value={order.status} 
                                    onChange={(e) => updateOrderStatus(order.orderNum, e.target.value)}
                                >
                                    {statusOptions.map((status) => (
                                        <option key={status} value={status}>
                                            {status}
                                        </option>
                                    ))}
                                </select>
                            </td> {/* 주문 상태 표시 및 수정 */}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default Dashboard;
