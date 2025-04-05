package Cinema_Ticket_System_SWING;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CinemaReservation extends JFrame {
    private JButton[][] seats = new JButton[5][8]; // 5 satır, 8 sütun koltuk düzeni
    private double totalPrice = 0.0; // Toplam fiyat
    private JLabel priceLabel; // Toplam fiyatı gösteren etiket

    public CinemaReservation() {
        setTitle("Sinema Salonu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Arka planı krem rengi yapar
        getContentPane().setBackground(new Color(245, 245, 220));

        // Üstte "BİLET SİSTEMİ" başlığı
        JLabel titleLabel = new JLabel("BİLET SİSTEMİ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Koltuk paneli (5x8 grid)
        JPanel seatPanel = new JPanel(new GridLayout(5, 8, 5, 5));
        seatPanel.setBackground(new Color(245, 245, 220));
        initializeSeats(seatPanel);

        // Toplam fiyat etiketi
        priceLabel = new JLabel("Toplam Fiyat: 0.0 TL", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(titleLabel, BorderLayout.NORTH);
        add(seatPanel, BorderLayout.CENTER);
        add(priceLabel, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeSeats(JPanel seatPanel) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                String seatNumber = (row + 1) + "-" + (col + 1);
                seats[row][col] = new JButton(seatNumber);
                seats[row][col].setFont(new Font("Arial", Font.BOLD, 12));

                // İlk 2 satır VIP, diğerleri normal koltuk
                if (row < 2) {
                    seats[row][col].setBackground(Color.CYAN);
                    seats[row][col].putClientProperty("price", 100.0);
                    seats[row][col].putClientProperty("type", "VIP");
                } else {
                    seats[row][col].setBackground(Color.GREEN);
                    seats[row][col].putClientProperty("price", 50.0);
                    seats[row][col].putClientProperty("type", "Normal");
                }
                seats[row][col].putClientProperty("sold", false);
                seats[row][col].putClientProperty("seatNumber", seatNumber);

                seats[row][col].addActionListener(new SeatActionListener(row, col));
                seatPanel.add(seats[row][col]);
            }
        }
    }

    private class SeatActionListener implements ActionListener {
        private int row, col;

        public SeatActionListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton seat = seats[row][col];
            boolean isSold = (boolean) seat.getClientProperty("sold");
            double price = (double) seat.getClientProperty("price");
            String type = (String) seat.getClientProperty("type");
            String seatNumber = (String) seat.getClientProperty("seatNumber");

            if (!isSold) { // Satın alma işlemi
                int confirm = JOptionPane.showConfirmDialog(
                        CinemaReservation.this,
                        seatNumber + " numaralı koltuğu " + price + " TL'ye satın almak istiyor musunuz?",
                        "Satın Alma Onayı",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    seat.setBackground(Color.ORANGE); // Koltuğu turuncuya boyar (satıldı)
                    seat.setText("Satıldı"); // Koltukta "Satıldı" yazısını gösterir
                    seat.putClientProperty("sold", true); // Koltuğu satılmış olarak işaretler
                    totalPrice += price; // Toplam fiyata koltuk fiyatını ekler
                    updatePriceLabel(); // Fiyat etiketini günceller
                }
            } else { // İptal işlemi
                int confirm = JOptionPane.showConfirmDialog(
                        CinemaReservation.this,
                        seatNumber + " numaralı koltuğu iptal etmek istiyor musunuz?",
                        "İptal Onayı",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    seat.setBackground(type.equals("VIP") ? Color.CYAN : Color.GREEN); // Koltuğu eski rengine döndürür (VIP ise cyan, normal ise yeşil)
                    seat.setText(seatNumber); // Koltuk numarasını geri yükler
                    seat.putClientProperty("sold", false); // Koltuğu satılmamış olarak işaretler
                    totalPrice -= price; // Toplam fiyattan koltuk fiyatını çıkarır
                    updatePriceLabel(); // Fiyat etiketini günceller
                }
            }
        }
    }

    private void updatePriceLabel() {
        // Toplam fiyatı formatlı bir şekilde etikete yazar
        priceLabel.setText(String.format("Toplam Fiyat: %.1f TL", totalPrice));
    }

    public static void main(String[] args) {
    	//Arayüzü baslatır
        SwingUtilities.invokeLater(CinemaReservation::new);
    }
}