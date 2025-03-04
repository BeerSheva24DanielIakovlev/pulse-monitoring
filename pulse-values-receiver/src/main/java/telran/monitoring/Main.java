package telran.monitoring;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import telran.monitoring.api.SensorData;
import telran.monitoring.logging.Logger;
import telran.monitoring.logging.LoggerStandard;

public class Main {

    private static final int PORT = 5000;
    private static final int MAX_SIZE = 1500;
    private static final int WARNING_LOG_VALUE = 220;
    private static final int ERROR_LOG_VALUE = 230;
    static Logger logger = new LoggerStandard("receiver");

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);

        byte[] buffer = new byte[MAX_SIZE];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String jsonStr = new String(packet.getData());
            logger.log("finest", jsonStr);
            logPulseValue(jsonStr);

            socket.send(packet);
        }
    }

    private static void logPulseValue(String jsonStr) {
        SensorData sensorData = SensorData.of(jsonStr);
        int value = sensorData.value();
        if (value >= WARNING_LOG_VALUE && value <= ERROR_LOG_VALUE) {
            logValue("warning", sensorData);
        } else if(value > ERROR_LOG_VALUE) {
            logValue("error", sensorData);
        }
    }

    private static void logValue(String level, SensorData sensorData) {
        logger.log(level, String.format("patient %d has pulse value greater than %d", sensorData.patientId(),
                level.equals("warning") ? WARNING_LOG_VALUE : ERROR_LOG_VALUE));
    }

}