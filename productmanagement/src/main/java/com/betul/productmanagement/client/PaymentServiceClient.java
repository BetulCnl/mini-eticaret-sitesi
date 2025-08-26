package com.betul.productmanagement.client;

import com.betul.productmanagement.dto.PaymentRequestDTO;
import com.betul.productmanagement.dto.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 'name' ve 'url' ile bu client'ın hangi servise istek atacağını belirtiyoruz.
// Gerçek bir ortamda url, Eureka gibi bir service discovery mekanizması tarafından yönetilir.
//service discovery kullanmıyorsak, url'yi manuel olarak yazıyoruz.
// Eğer Eureka kullanıyorsak, 'name' ile servisi bulabiliriz ve url'yi yazmamıza gerek kalmaz.

@FeignClient(name = "payment-service", url = "http://localhost:8081/api/v1")
public interface PaymentServiceClient {

    // Bu metot imzası, payment-service'deki PaymentController'daki metotla eşleşmelidir.
    @PostMapping("/payments")
    PaymentResponseDTO createPayment(@RequestBody PaymentRequestDTO request);
//FEIN CLIENT BURADA ORDERSERVICETEN GELEN METOT ÇAĞRISINI ALIP YENİ BİR HTTP İSTEĞİNE DÖNÜŞTÜRÜRÜR DİĞER MİKROSERVİSE YOLLAR
}
// service discovery load balancing burada geçiyormuş
