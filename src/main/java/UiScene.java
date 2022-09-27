import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HexFormat;

public class UiScene extends JDialog {

    private JPanel contentPane;
    private JButton buttonDoor0;
    private JButton buttonDoor1;
    private JTextArea jTestArea;
    private JLabel jlabel1;
    private static final Logger LOGGER = LogManager.getLogger(UiScene.class);
    private static final int SERVICE_PORT = 8192;
    private static final byte[] MESSAGE = {0x02, 0x1F, 0x00, 0x03, 0x61, 0x38};

    ConnectionDatabase connectionDatabase = new ConnectionDatabase();
    Statement statement;

    {
        try {
            LOGGER.info("Отправка запроса.");
            statement = connectionDatabase.getConnection().createStatement();
            LOGGER.info("Запрос успешно отправлен.");
        } catch (SQLException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public UiScene() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDoor0);

        buttonDoor0.addActionListener(e -> onDoor0());
        buttonDoor1.addActionListener(e -> onDoor1());
    }

    private void onDoor0() {

        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress ipAddress = InetAddress.getByName(jTestArea.getText());

            LOGGER.info("Чтение ip: " + ipAddress + ":" + SERVICE_PORT);
            InetSocketAddress mainAddress = new InetSocketAddress(ipAddress, SERVICE_PORT);
            socket.setSoTimeout(1000);

            //Запоминаем сообщение
            DatagramPacket messagePacket = new DatagramPacket(MESSAGE, MESSAGE.length, mainAddress);

            // Отправляем сообщение
            LOGGER.info("Отправка данных: " + Arrays.toString(MESSAGE) + "." + " От: " + InetAddress.getLocalHost());
            socket.send(messagePacket);

            byte[] receiveBuffer = new byte[1024];

            // Пакет для получения сообщения
            DatagramPacket outputPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(outputPacket);
            int packetLength = outputPacket.getLength();

            String hex = HexFormat.of().formatHex(receiveBuffer, 0, packetLength);

            LOGGER.info("Проверка на объем пришедшего пакета. Получено: " +
                    packetLength +
                    " byte. Ответ: " +
                    hex +
                    "." +
                    " От " +
                    outputPacket.getAddress() +
                    ":" +
                    outputPacket.getPort());

            if (packetLength > 256)
                throw new RuntimeException("Полученные данные превышают размер буфера. Размер " + packetLength + " byte.");

            //Получаем сообщение
            jlabel1.setText("Message: " + hex);
            LOGGER.info("Закрытие сокета.");
        } catch (Exception e) {
            LOGGER.error("Ошибка: " + e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onDoor1() {

    }

    public static void main(String[] args) {

        LOGGER.info("Запуск программы.");
        UiScene dialog = new UiScene();
        dialog.pack();
        dialog.setVisible(true);
        LOGGER.info("Закрытие программы.");
        System.exit(0);

    }


}
