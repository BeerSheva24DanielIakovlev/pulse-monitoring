package telran.monitoring;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import telran.monitoring.api.SensorData;
import telran.monitoring.logging.Logger;
import telran.monitoring.logging.LoggerStandard;

public class Main {
    private static final int PORT = 3500;
    private static final int MAX_SIZE = 1500;
    private static final String PULSE_LOG_FILE = "pulse_log.txt";
    private static final String ERROR_LOG_FILE = "error_log.txt";
    private static final int LOGGED_PATIENT_ID = 3;
    private static final int HIGHT_BAD_PULSE = 120;
    private static final int LOW_BAD_PULSE = 60;
    private static final int HIGHT_CRIT = 160;
    private static final int LOW_CRIT = 50;
    static Logger logger = new LoggerStandard("receiver");

    public static void main(String[] args) throws Exception {
        @SuppressWarnings("resource")
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[MAX_SIZE];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            
            String receivedData = new String(packet.getData(), 0, packet.getLength());
            SensorData sensorData = SensorData.of(receivedData);
            int pulseValue = sensorData.value();
            
            if (sensorData.patientId() == LOGGED_PATIENT_ID) {
                logger.log("finest", "Received data: " + receivedData);
                writeToFile(PULSE_LOG_FILE, receivedData);
                
                if (pulseValue > HIGHT_CRIT || pulseValue < LOW_CRIT) {
                    String errorMsg = "Critical pulse: " + pulseValue;
                    logger.log("severe", errorMsg);
                    writeToFile(ERROR_LOG_FILE, errorMsg);
                } else if (pulseValue > HIGHT_BAD_PULSE || pulseValue < LOW_BAD_PULSE) {
                    String warningMsg = "Bad pulse: " + pulseValue;
                    logger.log("warning", warningMsg);
                    writeToFile(ERROR_LOG_FILE, warningMsg);
                }
            }
            
            socket.send(packet);
        }
    }

    private static void writeToFile(String fileName, String data) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(data + "\n");
        } catch (IOException e) {
            logger.log("severe", "Error writing to file: " + fileName);
        }
    }
}
