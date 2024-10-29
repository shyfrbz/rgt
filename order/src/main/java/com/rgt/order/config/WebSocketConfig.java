package com.rgt.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 "/topic" 경로를 구독 가능
        config.enableSimpleBroker("/topic"); 
        
        // 클라이언트가 서버로 메시지 전송 시 "/app" 경로 사용
        config.setApplicationDestinationPrefixes("/app"); 
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트에서 WebSocket 연결 엔드포인트 설정
        registry.addEndpoint("/ws/dashboard") // 엔드포인트를 '/ws/dashboard'로 변경
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS(); // SockJS 폴백을 사용
    }

}
