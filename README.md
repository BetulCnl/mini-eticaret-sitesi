# Mini E-Ticaret Sitesi

Spring Boot ile yazılmış, iki servisten oluşan küçük bir e-ticaret uygulaması. Ürün listeleme, sepet, sipariş, sahte ödeme ve iade akışı var. Arayüz Thymeleaf sayfalarıyla servis ediliyor.

Bu, projenin en güncel hali. Önceki aşamalar: [product-management](https://github.com/BetulCnl/product-management) (ilk sürüm) ve [13.08.25windows](https://github.com/BetulCnl/13.08.25windows) (ara sürüm).

## Servisler

| Servis | Port | Görevi |
|---|---|---|
| `productmanagement` | 8082 | kullanıcılar, ürünler, kategoriler, sepet, siparişler, web sayfaları |
| `paymentmanagement` | 8081 | ödeme ve iade simülasyonu |

`productmanagement`, ödeme servisine OpenFeign ile istek atıyor. Ödeme servisi gerçek bir ödeme yapmıyor: ~1,5 saniye bekleyip istekte gelen durumu (başarılı / başarısız) geri döndürüyor. İade sonucu rastgele belirleniyor.

İki servis aynı PostgreSQL veritabanını (`pmdb`) kullanıyor.

## Akış

1. Kayıt ol / giriş yap (JWT)
2. Ürünleri listele, ara, sepete ekle
3. Sepetten sipariş oluştur
4. Ödeme sayfasında siparişi "tamamla" ya da "başarısız" olarak işaretle; istek ödeme servisinden geçer
5. "Siparişlerim" sayfasından siparişleri gör, iptal et (iptal, ödeme servisine iade isteği gönderir)

Sepet istekleri `X-Device-Id` başlığıyla cihaza göre ayrılıyor.

## Gereksinimler

- Java 21
- Docker (PostgreSQL için)

## Çalıştırma

```bash
# 1. veritabanı
docker compose up -d postgres

# 2. ödeme servisi
cd paymentmanagement
./mvnw spring-boot:run

# 3. ürün servisi (başka bir terminalde)
cd productmanagement
./mvnw spring-boot:run
```

Sonra tarayıcıdan `http://localhost:8082/login` adresine git.

Uygulama ilk açılışta örnek kategori ve ürünleri kendisi ekliyor.

### Docker ile

Her iki serviste de bir `Dockerfile` ve `application-docker.properties` var. Docker profilinde veritabanı adresi `postgres:5432` oluyor. `docker-compose.yml` şu an sadece PostgreSQL'i içeriyor; servisleri de compose'a eklemek için önce `./mvnw package` ile jar üretmek gerekiyor, çünkü Dockerfile'lar hazır jar'ı kopyalıyor.

## API

| Yol | Erişim | İçerik |
|---|---|---|
| `POST /api/auth/register`, `/login`, `/logout` | herkes | kayıt ve giriş |
| `GET /api/products`, `/search`, `/{id}` | herkes | ürün listeleme ve arama |
| `POST/DELETE/PATCH /api/products/...` | ADMIN | ürün ekleme, silme, stok güncelleme |
| `GET/POST /api/categories` | listeleme herkes, ekleme ADMIN | kategoriler |
| `/api/cart` | USER, ADMIN | sepet |
| `/api/orders` | USER, ADMIN | sipariş oluşturma, tamamlama, iptal |
| `/api/admin/orders` | ADMIN | tüm siparişler |
| `POST /api/v1/payments`, `/refund` | ödeme servisi | ödeme ve iade |

Kayıt isteğinde `"role": "ADMIN"` gönderilirse kullanıcı admin olarak oluşturuluyor. Bu, geliştirme kolaylığı için açık bırakılmış; gerçek bir ortamda kapatılması gerekir.

## Klasör yapısı

```
productmanagement/src/main/java/com/betul/productmanagement/
  controller/   REST endpoint'leri ve sayfa yönlendirmeleri
  service/      iş mantığı (sepet, sipariş, ödeme kaydı, oturum)
  client/       ödeme servisine giden Feign client
  security/     JWT
  entity/, dto/, Mapper/, repository/, enums/
productmanagement/src/main/resources/templates/   login, products, cart, purchase, myOrders sayfaları
paymentmanagement/                                ödeme servisi
docker-compose.yml                                PostgreSQL
```

## Notlar

- `application.properties` içindeki veritabanı şifresi ve JWT anahtarı geliştirme için konmuş sabit değerler.
- Repoda kalmış `pmdb.mv.db` / `pmdb.trace.db` dosyaları eski H2 sürümünden kalma, artık kullanılmıyor.
