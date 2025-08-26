package com.betul.paymentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentManagementApplication.class, args);
	}

}

//FEİGN CLIENTI KULLABİLMEK İÇİN SPRİNG CLOUD EKLEMEYE ÇALIŞTIM. SPRİNG BOOT VERSİYONUMLA UYUMSUZLAR AMA EKLEYECEĞİM
// UYUMLULUK KONTROLÜNÜ DEVRE DIŞI BIRAKIP ->  spring.cloud.compatibility-verifier.enabled=false
// bu spring cloudu illa ki dahil ettil çünkü  Spring Cloud'u projemize dahil ettik çünkü
// istediğiniz mikroservis iletişimini en kolay ve en standart şekilde yapmamızı sağlayan Feign Client bu kütüphanenin parçasıdır.
