package com.betul.productmanagement.service;

import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

//UserCurrentService: Oturum açmış kullanıcının bilgilerini alır.
//Bu servis, güvenlik bağlamından (SecurityContext) oturum açmış kullanıcının kimliğini alır ve bu kimliğe göre kullanıcı veritabanından kullanıcı bilgilerini
//bulur. Eğer kullanıcı bulunamazsa veya oturum açılmamışsa, bir hata fırlatır.
//Bu servis, genellikle kullanıcı bilgilerine erişim gerektiren işle

//yani zaten JWT ile kimlik doğrulaması yapılmış bir kullanıcı için kullanılır.



@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CurrentUserService.class);

    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            logger.warn("Authentication object is null, cannot find current user.");
            throw new RuntimeException("Not authenticated");
        }
        String userEmail = auth.getName();
        logger.info("Attempting to find current user by email from security context: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User with email {} not found in the database.", userEmail);
                    return new RuntimeException("User not found");
                });

        logger.info("Current user found: ID={}, Email={}", user.getId(), user.getEmail());
        return user;
    }
}