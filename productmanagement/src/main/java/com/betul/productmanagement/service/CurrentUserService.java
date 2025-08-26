package com.betul.productmanagement.service;

import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}