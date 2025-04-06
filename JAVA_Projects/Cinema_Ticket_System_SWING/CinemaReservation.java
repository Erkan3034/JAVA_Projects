package Cinema_Ticket_System_SWING;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CinemaReservation extends JFrame {
	
    
    // koltukları ve fiyat bilgisini tutacak değişkenler
    private JButton[][] seats = new JButton[5][8]; // 5 satır, 8 sütunluk koltuk düzeni
    private double totalPrice = 0.0; // toplam bilet fiyatı degiskeni
    private JLabel priceLabel; // toplam fiyatı gösteren etiket

    public CinemaReservation() {
        // Frame ayarları
        setTitle("Sinema Bilet Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        
        
        
        getContentPane().setBackground(new Color(240, 240, 240)); // arkaplan rengi 

        
        // Başlık
        JLabel title = new JLabel("SİNEMA BİLET SİSTEMİ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH); // Başlığı üst kısma ekle
        

        // koltuk panelini oluşturma ve ekleme
        JPanel seatPanel = createSeatPanel();
        add(seatPanel, BorderLayout.CENTER);
        
        // fiyat etiketini oluşturma  ve ekleme
        priceLabel = new JLabel("Toplam: 0.0 TL", SwingConstants.CENTER);
        
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        add(priceLabel, BorderLayout.SOUTH); // fiyat alt kısımda
        

        setVisible(true);
        
    }

    // koltuk panelini oluşturan  metot
    private JPanel createSeatPanel() {
    	
        JPanel panel = new JPanel(new GridLayout(5, 8, 5, 5)); // 5*8 ızgara
        panel.setBackground(new Color(240, 240, 240));
        
        for (int row = 0; row < 5; row++) // 5 satır
        {  
            for (int col = 0; col < 8; col++)  //8 sütun
            { 
                // Her koltuk için buton oluşturuyruz
                JButton seat = new JButton((row+1) + "-" + (col+1)); 
                seat.setFont(new Font("Arial", Font.BOLD, 10));
                
                
                // Koltuk tipini ve fiyatını belirliyoruz - ilk 2 satır Vip
                if (row < 2) {
                    seat.setBackground(Color.BLUE);
                    seat.setForeground(Color.WHITE);
                    seat.putClientProperty("price", 100.0); // koltuk butonuna "price" adında bir özel özellik ekle ve değerini 100.0 TL yapar

                } else {
                    seat.setBackground(Color.GREEN);
                    seat.putClientProperty("price", 50.0); // Normal fiyat 50 TL
                }
                
                // Koltuk durum bilgilerini sakla
                seat.putClientProperty("sold", false); // key- value 
                seat.putClientProperty("row", row);
                seat.putClientProperty("col", col);
                
                // Tıklama olayını ekle
                seat.addActionListener(new SeatClickListener());
                panel.add(seat);
                seats[row][col] = seat; // Butonu diziye kaydet
            }
        }
        return panel;
    }
    
    // Koltuk tıklama olaylarını yöneten sınıf
    
    private class SeatClickListener implements ActionListener {
        @Override
        
        public void actionPerformed(ActionEvent e) {
        	
            JButton clickedSeat = (JButton) e.getSource();
            
            boolean isSold = (boolean) clickedSeat.getClientProperty("sold");
            double price = (double) clickedSeat.getClientProperty("price");
            int row = (int) clickedSeat.getClientProperty("row");
            int col = (int) clickedSeat.getClientProperty("col");
            String seatNumber = (row+1) + "-" + (col+1);

            
            if (!isSold) {
                // Koltuk satın alma işlemi
                int choice = JOptionPane.showConfirmDialog(
                	CinemaReservation.this, 
                    seatNumber + " numaralı koltuk (" + price + " TL) satın alınsın mı?",
                    "Onay",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    clickedSeat.setBackground(Color.RED);
                    clickedSeat.setText("DOLU");
                    clickedSeat.putClientProperty("sold", true);
                    totalPrice += price;
                    updatePrice();
                }
            } else {
                // koltuk iptal işlemi
                int choice = JOptionPane.showConfirmDialog(
                    CinemaReservation.this, 
                    seatNumber + " numaralı koltuğu iptal etmek istiyor musunuz?",
                    "Onay",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    // orjinal rengine döndür / vip mi normal mi kontrol edelim
                    if (row < 2) {
                        clickedSeat.setBackground(Color.BLUE);
                    } else {
                        clickedSeat.setBackground(Color.GREEN);
                    }
                    
                    clickedSeat.setText(seatNumber);
                    clickedSeat.putClientProperty("sold", false);
                    
                    totalPrice -= price;
                    updatePrice();
                }
            }
        }
    }

    // toplam fiyatı güncelleyen yardımcı metot
    private void updatePrice() {
        priceLabel.setText(String.format("Toplam: %.1f TL", totalPrice));
    }

    public static void main(String[] args) {
        // Swing uygulamasını başlat
        SwingUtilities.invokeLater(() -> new CinemaReservation());
        
        
    }
}
