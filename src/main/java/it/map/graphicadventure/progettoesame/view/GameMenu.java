package it.map.graphicadventure.progettoesame.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GameMenu extends JFrame {

    public GameMenu() {
        setTitle("Undertale - Avventura");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Variabile per salvare il font personalizzato
        Font customFont;

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/PressStart2P-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.err.println("Errore: Font non trovato. Uso il font di default.");
            customFont = new Font("Monospaced", Font.BOLD, 24);
        }

        BackgroundPanel mainPanel = new BackgroundPanel("src/main/resources/main_menu_background.png");
        mainPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("UNDERTALE");
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 80f));
        titleLabel.setForeground(Color.WHITE);

        titleLabel.setUI(new ShadowLabelUI());

        StyledButton btnStart = new StyledButton("INIZIA");
        StyledButton btnExit = new StyledButton("ESCI");

        btnStart.setFont(customFont.deriveFont(Font.PLAIN, 32f));
        btnExit.setFont(customFont.deriveFont(Font.PLAIN, 32f));

        btnStart.addActionListener(e -> {
            System.out.println("Avvio del gioco...");
            dispose();
        });

        btnExit.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 80, 0); // Margine grande sotto il titolo
        mainPanel.add(titleLabel, gbc);

        // pulsante Inizia
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0); // Spazio tra i pulsanti
        mainPanel.add(btnStart, gbc);

        // pulsante Esci
        gbc.gridy = 2;
        mainPanel.add(btnExit, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private static class StyledButton extends JButton {
        private final Color normalColor = new Color(0, 0, 0, 150); // Nero semitrasparente
        private final Color hoverColor = new Color(255, 255, 255, 50); // Bianco semitrasparente
        private boolean isHovered = false;

        public StyledButton(String text) {
            super(text);
            setPreferredSize(new Dimension(300, 60));
            setForeground(Color.WHITE); // Testo bianco
            setFocusPainted(false);     // Niente bordino di focus
            setContentAreaFilled(false); // Disabilita il riempimento standard di Swing
            setBorderPainted(false);     // Disabilita il bordo standard
            setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursore a manina

            // Gestione dell'effetto hover (passaggio del mouse)
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHovered) {
                g2d.setColor(hoverColor);
            } else {
                g2d.setColor(normalColor);
            }

            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

            g2d.dispose();

            super.paintComponent(g);
        }
    }

    private static class ShadowLabelUI extends javax.swing.plaf.basic.BasicLabelUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            JLabel label = (JLabel) c;
            String text = label.getText();
            if (text == null || text.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int x = label.getInsets().left;
            int y = label.getHeight() - label.getInsets().bottom - g2d.getFontMetrics().getDescent();

            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.drawString(text, x + 4, y + 4);

            g2d.setColor(label.getForeground());
            g2d.drawString(text, x, y);
        }
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            try {
                backgroundImage = ImageIO.read(new File(fileName));
            } catch (IOException e) {
                System.err.println("Errore sfondo: " + fileName);
                setBackground(Color.DARK_GRAY);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = backgroundImage.getWidth(this);
                int imgHeight = backgroundImage.getHeight(this);

                double ratioW = (double) panelWidth / imgWidth;
                double ratioH = (double) panelHeight / imgHeight;
                double scale = Math.max(ratioW, ratioH);

                int targetWidth = (int) (imgWidth * scale);
                int targetHeight = (int) (imgHeight * scale);
                int x = (panelWidth - targetWidth) / 2;
                int y = (panelHeight - targetHeight) / 2;

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, x, y, targetWidth, targetHeight, this);
            }
        }
    }

    static void main() {
        SwingUtilities.invokeLater(GameMenu::new);
    }
}