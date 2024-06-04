package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenReceiver {
    public static void main(String[] args) {
        int port = 12345;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept();
             InputStream inputStream = socket.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            JFrame frame = new JFrame("Screen Receiver");
            JLabel label = new JLabel();
            frame.add(label);
            frame.setSize(screenSize);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            while (true) {
                int length = dataInputStream.readInt();
                if (length > 0) {
                    byte[] imageBytes = new byte[length];
                    dataInputStream.readFully(imageBytes);
                    System.out.println("Received image of size: " + length);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(byteArrayInputStream);
                    byteArrayInputStream.close();

                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
                        ImageIcon imageIcon = new ImageIcon(scaledImage);
                        label.setIcon(imageIcon);
                        frame.repaint();
                        System.out.println("Image displayed");
                    } else {
                        System.out.println("Failed to decode image");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
