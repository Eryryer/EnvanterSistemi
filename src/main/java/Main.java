import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;
import net.datafaker.Faker; // Eklediğimiz kütüphane

public class Main {

    public static void main(String[] args) {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=EnvanterDB;encrypt=true;trustServerCertificate=true;";
        String user = "sa";
        String pass = "12345";

        Connection conn = null;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Veritabanına bağlanılıyor...");
            conn = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Bağlantı BAŞARILI! \n");

            // --- YENİ: Veri Doldurma Menüsü ---
            System.out.print("Veritabanına rastgele test verisi (1000 adet) eklensin mi? (E/H): ");
            String cevap = scanner.next();
            if (cevap.equalsIgnoreCase("E")) {
                veriUretVeEkle(conn, 1000);
            }

            listele(conn);

            System.out.println("\n--------------------------------");
            System.out.print("Sipariş vermek istediğiniz Ürün ID'sini girin: ");
            int urunId = scanner.nextInt();

            System.out.print("Kaç adet almak istersiniz?: ");
            int adet = scanner.nextInt();

            String updateSQL = "UPDATE URUNLER SET StokAdedi = StokAdedi - ? WHERE UrunID = ?";

            PreparedStatement pstmt = conn.prepareStatement(updateSQL);
            pstmt.setInt(1, adet);
            pstmt.setInt(2, urunId);

            int etkilenenSatir = pstmt.executeUpdate();

            if (etkilenenSatir > 0) {
                System.out.println("\n SİPARİŞ BAŞARILI! Stok güncellendi.");
                System.out.println("--- Güncel Stok Durumu ---");
                listele(conn); // Güncel halini tekrar listele
            } else {
                System.out.println("\n HATA: Böyle bir Ürün ID bulunamadı!");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("CK_Stok_Pozitif")) {
                System.out.println("\n HATA: Stok yetersiz! Veritabanı işlemi reddetti.");
            } else {
                System.out.println("HATA OLUŞTU! ");
                e.printStackTrace();
            }
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // ---  Fake Veri Üretici Metot ---
    public static void veriUretVeEkle(Connection conn, int eklenecekAdet) {
        Faker faker = new Faker();
        String insertSQL = "INSERT INTO URUNLER (Ad, Fiyat, StokAdedi) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            System.out.println(eklenecekAdet + " adet rastgele veri üretiliyor, lütfen bekleyin...");

            // Performans için AutoCommiti kapattım
            conn.setAutoCommit(false);

            for (int i = 1; i <= eklenecekAdet; i++) {
                // Ürünleri rastgele isimli olusturunca hata verdi boyle cozdum son kullanma tarıhi
                String urunAdi = faker.commerce().productName() + " - SKU" + i + faker.number().digits(4);
                double fiyat = faker.number().randomDouble(2, 10, 5000);
                int stok = faker.number().numberBetween(1, 500);

                pstmt.setString(1, urunAdi);
                pstmt.setBigDecimal(2, BigDecimal.valueOf(fiyat));
                pstmt.setInt(3, stok);

                // Sorguyu batch  kuyruğuna ekle
                pstmt.addBatch();

                // Bellek  dolmasın diye her 100 kayıtta bir kuyruktakileri veritabanına gönder
                if (i % 100 == 0 || i == eklenecekAdet) {
                    pstmt.executeBatch();
                }
            }
            conn.commit();
            System.out.println("BAŞARILI: " + eklenecekAdet + " adet veri veritabanına eklendi!\n");

        } catch (SQLException e) {
            System.out.println("Veri eklenirken hata oluştu!");
            e.printStackTrace();
            try {
                conn.rollback(); // Hata olursa işlemleri geri al
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true); // Ayarı eski haline döndür
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listele(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        // 1000 verinin hepsini konsola basmak programı kitler bu yüzden sadece son 20'yi çektim
        ResultSet rs = stmt.executeQuery("SELECT TOP 20 * FROM URUNLER ORDER BY UrunID DESC");

        System.out.println("--- GÜNCEL ÜRÜN LİSTESİ (Son 20 Eklenen) ---");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("UrunID") +
                    " | Ürün: " + rs.getString("Ad") +
                    " | Fiyat: " + rs.getBigDecimal("Fiyat") + " TL" +
                    " | Stok: " + rs.getInt("StokAdedi"));
        }
    }
}