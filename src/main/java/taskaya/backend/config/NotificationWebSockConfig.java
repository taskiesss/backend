package taskaya.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class NotificationWebSockConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry config) {
        config.enableSimpleBroker("/notifications"); //prefix for broadcasting
        config.setApplicationDestinationPrefixes("/app"); //prefix for client-to-server messages
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications").setAllowedOrigins("http://localhost:3000").withSockJS();
    }
}
