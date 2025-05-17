package dbConn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import dbConn.DbHelper;

public class PersonnelManagementApp extends JFrame {
    private JTextField isimField, soyadField, yasField, dogumTarihiField;
    private JButton addButton, addToBatchButton, deleteButton;
    private JTable personnelTable;
    private DefaultTableModel tableModel;
    private ArrayList<String[]> batchRecords = new ArrayList<>();
    private DbHelper dbHelper = new DbHelper();

    public PersonnelManagementApp() {
        // Arayüzü oluştur
        setTitle("Personnel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(600, 400);

        // Üst panel-> Giriş alanları ve butonlar
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.add(new JLabel("İsim:"));
        isimField = new JTextField();
        inputPanel.add(isimField);
        inputPanel.add(new JLabel("Soyad:"));
        soyadField = new JTextField();
        inputPanel.add(soyadField);
        inputPanel.add(new JLabel("Yaş:"));
        yasField = new JTextField();
        inputPanel.add(yasField);
        inputPanel.add(new JLabel("Doğum Tarihi (YYYY-MM-DD):"));
        dogumTarihiField = new JTextField();
        inputPanel.add(dogumTarihiField);
        addButton = new JButton("Personel Ekle");
        inputPanel.add(addButton);
        deleteButton = new JButton("Seçili Personeli Sil");
        inputPanel.add(deleteButton);
        addToBatchButton = new JButton("Batch'e Ekle");
        inputPanel.add(addToBatchButton);

        // Tablo modeli ve JTable
        tableModel = new DefaultTableModel(new Object[]{"id", "İsim", "Soyad", "Yaş", "Doğum Tarihi"}, 0);
        personnelTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(personnelTable);

        // Panel ekleme
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Verileri yükle
        loadPersonnel();

        // Olay dinleyicileri
        addButton.addActionListener(e -> addPersonnel());
        deleteButton.addActionListener(e -> deleteSelectedPersonnel());
        addToBatchButton.addActionListener(e -> addToBatchList());

        // Pencere kapanırken batch işlemini gerçekleştir
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executeBatch();
                dispose(); 
            }
        });
    }

    private void loadPersonnel() {
        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM personeller")) {
            tableModel.setRowCount(0); // Tabloyu temizle
            while (rs.next()) {
                int id = rs.getInt("id");
                String isim = rs.getString("isim");
                String soyad = rs.getString("soyad");
                int yas = rs.getInt("yas");
                String dogumTarihi = rs.getString("dogumTarihi");
                tableModel.addRow(new Object[]{id, isim, soyad, yas, dogumTarihi});
            }
        } catch (SQLException e) {
            dbHelper.showError(e);
        }
    }

    private void addPersonnel() {
        String isim = isimField.getText().trim();
        String soyad = soyadField.getText().trim();
        String yasText = yasField.getText().trim();
        String dogumTarihi = dogumTarihiField.getText().trim();

        if (!isim.isEmpty() && !soyad.isEmpty() && !yasText.isEmpty() && !dogumTarihi.isEmpty()) {
            try {
                int yas = Integer.parseInt(yasText);
                String sql = "INSERT INTO personeller (isim, soyad, yas, dogumTarihi) VALUES (?, ?, ?, ?)";
                try (Connection conn = dbHelper.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, isim);
                    pstmt.setString(2, soyad);
                    pstmt.setInt(3, yas);
                    pstmt.setString(4, dogumTarihi);
                    pstmt.executeUpdate();
                    loadPersonnel(); // Tabloyu güncelle
                    isimField.setText("");
                    soyadField.setText("");
                    yasField.setText("");
                    dogumTarihiField.setText("");
                    JOptionPane.showMessageDialog(this, "Personel Başarıyla Eklendi!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Yaş bir sayı olmalı!");
            } catch (SQLException e) {
                dbHelper.showError(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
        }
    }

    private void addToBatchList() {
        String isim = isimField.getText().trim();
        String soyad = soyadField.getText().trim();
        String yasText = yasField.getText().trim();
        String dogumTarihi = dogumTarihiField.getText().trim();

        if (!isim.isEmpty() && !soyad.isEmpty() && !yasText.isEmpty() && !dogumTarihi.isEmpty()) {
            try {
                Integer.parseInt(yasText); // Yaşın geçerli bir sayı olduğunu kontrol et
                batchRecords.add(new String[]{isim, soyad, yasText, dogumTarihi});
                isimField.setText("");
                soyadField.setText("");
                yasField.setText("");
                dogumTarihiField.setText("");
                JOptionPane.showMessageDialog(this, "Personel Batch Listesine Eklendi!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Yaş bir sayı olmalı!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
        }
    }

    private void deleteSelectedPersonnel() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String sql = "DELETE FROM personeller WHERE id = ?";
            try (Connection conn = dbHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadPersonnel(); // Tabloyu güncelle
                JOptionPane.showMessageDialog(this, "Personel Başaıyla Silindi!");
            } catch (SQLException e) {
                dbHelper.showError(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin!");
        }
    }

    private void executeBatch() {
        if (!batchRecords.isEmpty()) {
            String sql = "INSERT INTO personeller (isim, soyad, yas, dogumTarihi) VALUES (?, ?, ?, ?)";
            try (Connection conn = dbHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                conn.setAutoCommit(false);
                for (String[] record : batchRecords) {
                    pstmt.setString(1, record[0]);
                    pstmt.setString(2, record[1]);
                    pstmt.setInt(3, Integer.parseInt(record[2]));
                    pstmt.setString(4, record[3]);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
                batchRecords.clear(); // Batch listesini temizle
                loadPersonnel(); // Tabloyu güncelle
                JOptionPane.showMessageDialog(this, "Batch islemi basariyla Tamamlandı!");
            } catch (SQLException e) {
                dbHelper.showError(e);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Yaş alanlarında geçersiz veri!!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PersonnelManagementApp app = new PersonnelManagementApp();
            app.setVisible(true);
        });
    }
}