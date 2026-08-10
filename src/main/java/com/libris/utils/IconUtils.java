package com.libris.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class IconUtils {

    // 1. TỰ ĐỘNG SCALE: Điều chỉnh icon khớp theo kích thước font chữ
    public static ImageIcon loadIconForComponent(String path, JComponent component) {
        int size = 20;
        if (component != null && component.getFont() != null) {
            FontMetrics fm = component.getFontMetrics(component.getFont());
            size = fm.getHeight(); 
        }
        return loadIcon(path, size, size);
    }

    // 2. Load và render SVG (hoặc PNG/JPG) từ URL Web hoặc File Local
    public static ImageIcon loadIcon(String path, int width, int height) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println("Đường dẫn icon bị null hoặc rỗng!");
            return createFallbackIcon(width, height);
        }
        try {
            // XỬ LÝ LINK INTERNET (Giả lập trình duyệt để chống block)
            if (path.startsWith("http://") || path.startsWith("https://")) {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Giả lập trình duyệt Chrome để unpkg.com không chặn request
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                conn.setConnectTimeout(5000); // Timeout 5s
                
                try (InputStream in = conn.getInputStream()) {
                    if (path.toLowerCase().contains(".svg")) {
                        // Lưu SVG vào file tạm (vì FlatSVGIcon đọc file local tốt hơn stream mạng)
                        File tempFile = File.createTempFile("icon_", ".svg");
                        tempFile.deleteOnExit(); // Tự xóa khi tắt app
                        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        FlatSVGIcon svgIcon = new FlatSVGIcon(tempFile);
                        return svgIcon.derive(width, height);
                    } else {
                        // Đọc PNG/JPG từ web
                        Image img = ImageIO.read(in);
                        if (img != null) {
                            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                        }
                    }
                }
            } 
            // XỬ LÝ FILE LOCAL TRONG RESOURCES
            else {
                URL localUrl = IconUtils.class.getClassLoader().getResource(path);
                if (localUrl != null) {
                    if (path.toLowerCase().contains(".svg")) {
                        FlatSVGIcon svgIcon = new FlatSVGIcon(localUrl);
                        return svgIcon.derive(width, height);
                    } else {
                        Image img = ImageIO.read(localUrl);
                        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể load icon: " + path + " - Lỗi: " + e.getMessage());
        }
        
        // Trả về icon mặc định nếu lỗi
        return createFallbackIcon(width, height);
    }

    // 3. Load ảnh giữ nguyên kích thước gốc (Local)
    public static ImageIcon loadIcon(String path) {
        try {
            URL url = IconUtils.class.getClassLoader().getResource(path);
            if (url != null) {
                if (path.toLowerCase().contains(".svg")) {
                    return new FlatSVGIcon(url);
                }
                Image img = ImageIO.read(url);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Không thể load icon: " + path);
        }
        return null;
    }

    // 4. Scale icon từ một ImageIcon có sẵn
    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // 5. Fallback Icon khi lỗi tải ảnh 
    private static ImageIcon createFallbackIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(37, 99, 235));
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(image);
    }

    // 6. Icon chữ mặc định 
    public static ImageIcon getDefaultIcon(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(37, 99, 235));
        g2d.fillRoundRect(0, 0, width, height, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, width / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height + fm.getAscent()) / 2 - 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return new ImageIcon(image);
    }
}