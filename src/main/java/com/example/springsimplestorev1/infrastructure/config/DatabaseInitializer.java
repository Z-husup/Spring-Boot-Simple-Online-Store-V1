package com.example.springsimplestorev1.infrastructure.config;

import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Value("${security.users.admin.username:admin}")
    private String adminUsername;

    @Value("${security.users.client.username:client}")
    private String clientUsername;

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail(adminUsername)) {
                userRepository.save(new User(adminUsername, "Administrator"));
            }
            if (!userRepository.existsByEmail(clientUsername)) {
                userRepository.save(new User(clientUsername, "Client"));
            }
        };
    }

    @Bean
    public CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (!productRepository.findAll().isEmpty()) {
                return;
            }

            List<Product> products = List.of(
                    new Product("Wireless Mouse", "Ergonomic 2.4GHz mouse", "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=1200", 24.99, 120),
                    new Product("Mechanical Keyboard", "Blue switch TKL keyboard", "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=1200", 79.99, 60),
                    new Product("27-inch Monitor", "IPS 1440p display", "https://images.unsplash.com/photo-1527443195645-1133f7f28990?w=1200", 249.00, 35),
                    new Product("USB-C Hub", "7-in-1 aluminum USB-C hub", "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=1200", 39.50, 85),
                    new Product("Laptop Stand", "Adjustable aluminum stand", "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=1200", 29.90, 70),
                    new Product("Noise-Canceling Headphones", "Over-ear bluetooth headset", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1200", 149.00, 40)
            );

            products.forEach(productRepository::save);
        };
    }
}
