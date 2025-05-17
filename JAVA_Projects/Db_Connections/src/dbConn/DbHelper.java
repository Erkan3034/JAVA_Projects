package dbConn;

import java.sql.Connection; // Veritabanı bağlantısını temsil eder.
import java.sql.DriverManager; // JDBC sürücüsünü kullanarak bağlantı kurar.
import java.sql.SQLException; // Veritabanı işlemlerinde oluşabilecek hataları yönetmek için kullanılır.

import javax.swing.JOptionPane;

public class DbHelper {

    // Veritabanı kullanıcı adı.
    private String userName = "root";

    // Veritabanı kullanıcı şifresi.
    private String password = "password";

    // Veritabanı bağlantı adresi. "world" adlı veritabanına bağlanmak için kullanılıyor.
    private String dbUrl = "jdbc:mysql://localhost:3306/deneme"; // db adresi.
    
    // Veritabanı bağlantısı oluşturmak için kullanılan metod.
    public Connection getConnection() throws SQLException {
        // DriverManager sınıfı, JDBC sürücüsünü kullanarak belirtilen URL, kullanıcı adı ve şifre ile bağlantı kurar.
    	 //JOptionPane.showMessageDialog(null, "Veritabanına Başarıyla Bağandı");
        return DriverManager.getConnection(dbUrl, userName, password);

    }
    
    // SQL hatalarını göstermek için kullanılan metod.
    public void showError(SQLException exception) {
        // Hata mesajını konsola yazdırır.
        System.out.println("Error: " + exception.getMessage());
        // Hatanın kodunu konsola yazdırır. Bu kod, hatanın türüne göre değişir.
        System.out.println("Error code: " + exception.getErrorCode());
    }
}
