# 📦 Envanter ve Sipariş Takip Sistemi (Inventory & Order Management)

Bu proje, bir e-ticaret sitesinin veya fiziksel bir mağazanın stoklarını, ürün fiyatlandırmalarını ve müşteri siparişlerini yönetebilmesi için geliştirilmis, iliskisel veritabanı  tabanlı bir otomasyon sistemidir. 

Sistem, **MICROSOFT SQL Server** üzerinde çalışan sağlam bir veritabanı mimarisi ile **Java (JDBC)** kullanılarak geliştirilmiş etkileşimli bir backend uygulamasından oluşmaktadır.

## 🚀 Öne Çıkan Teknik Özellikler

* **Veri Bütünlüğü (Data Integrity):** Veritabanı seviyesinde uygulanan `CHECK` kısıtlamaları sayesinde, stok miktarının sıfırın altına düşmesi veya geçersiz işlemler yapılması engellenmiştir. Java uygulaması bu kısıtlamaları Exception Handling ile yakalayarak kullanıcıya anlık geri bildirim verir.
* **Otomatik Denetim (Audit Logging):** Ürün fiyatlarında veya stoklarında bir değişiklik olduğunda, SQL Server üzerinde yazılan `AFTER UPDATE` Trigger'ı devreye girer ve güncelleme tarihini otomatik olarak sisteme kaydeder.
* **Performans Optimizasyonu (Batch Processing):** Sistemin ölçeklenebilirliğini test etmek amacıyla **Datafaker** kütüphanesi entegre edilmiştir. 1.000'den fazla sentetik ürün verisi, ağ trafiğini yormamak adına JDBC Batch Processing kullanılarak paketler halinde veritabanına aktarılmıştır.
* **Normalizasyon:** Veri tekrarını önlemek için Siparişler ve Sipariş Detayları 1'e N (One-to-Many) ilişkisi kurularak ayrı tablolara bölünmüştür.

## 🛠️ Kullanılan Teknolojiler

* **Backend:** Java (JDK 8+), JDBC
* **Veritabanı:** Microsoft SQL Server, T-SQL (Triggers, Constraints, Keys)
* **Bağımlılık Yönetimi:** Maven
* **Test & Mock Data:** Datafaker (`net.datafaker`)

## 🗄️ Veritabanı Mimarisi

Sistem 3 temel tablodan oluşmaktadır:

1. **URUNLER:** Mağazadaki ürünlerin ID, Ad, Stok, Fiyat ve Audit (Oluşturulma/Güncellenme Tarihi) bilgilerini tutar. Benzersiz ürün adları (`UNIQUE`) ve negatif stoğu engelleyen (`CHECK`) kısıtlamalar içerir.
2. **SIPARISLER:** Müşteri siparişlerinin üst bilgilerini (Tarih, Toplam Tutar, Durum) barındırır.
3. **SIPARISDETAY:** Hangi siparişte hangi üründen kaç adet alındığı bilgisini Foreign Key (Yabancı Anahtar) ilişkileriyle bağlayarak tutar.

## 💻 Kurulum ve Kullanım

1. Repoyu bilgisayarınıza klonlayın:
   ```bash
   git clone [https://github.com/Eryryer/EnvanterSistemi.git](https://github.com/Eryryer/EnvanterSistemi.git)
