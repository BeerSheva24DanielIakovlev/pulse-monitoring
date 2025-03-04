package telran.monitoring;

import java.net.*;
import java.util.Random;

import telran.monitoring.api.SensorData;

public class Main {
    static final int MIN_PULSE_VALUE = 40;
    static final int MAX_PULSE_VALUE = 240;
    static final long TIMEOUT_SEND = 2000;
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = 3500;
    static final int DEFAULT_N_PATIENTS = 10;
    static final double JUMP_PROB = 0.1;
    static final int MIN_JUMP_PERCENT = 10;
    static final int MAX_JUMP_PERCENT = 50;
    static final double JUMP_POSITIVE_PROB = 0.7;
    static final int NORMAL_MIN = 60;
    static final int NORMAL_MAX = 80;

    static DatagramSocket socket;
    static Random random = new Random();
    static int[] lastPulseValues = new int[DEFAULT_N_PATIENTS + 1];

    public static void main(String[] args) throws Exception {
        socket = new DatagramSocket();
        
        for (int i = 1; i <= DEFAULT_N_PATIENTS; i++) {
            lastPulseValues[i] = getRandomNumber(NORMAL_MIN, NORMAL_MAX);
        }
        
        while (true) {
            for (int patientId = 1; patientId <= DEFAULT_N_PATIENTS; patientId++) {
                send(patientId);
            }
            Thread.sleep(TIMEOUT_SEND);
        }
    }

    static void send(int patientId) {
        SensorData sensor = getRealisticSensorData(patientId);
        String jsonStr = sensor.toString();
        System.out.println("Generated: " + jsonStr);
        try {
            udpSend(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void udpSend(String jsonStr) throws Exception {
        byte[] bufferSend = jsonStr.getBytes();
        DatagramPacket packet =
                new DatagramPacket(bufferSend, bufferSend.length,
                        InetAddress.getByName(DEFAULT_HOST), DEFAULT_PORT);
        socket.send(packet);
    }

    private static SensorData getRealisticSensorData(int patientId) {
        int value = applyPulseJump(patientId);
        lastPulseValues[patientId] = value;
        long timestamp = System.currentTimeMillis();
        return new SensorData(patientId, value, timestamp);
    }

    private static int applyPulseJump(int patientId) {
        if (random.nextDouble() < JUMP_PROB) {
            int jumpPercent = getRandomNumber(MIN_JUMP_PERCENT, MAX_JUMP_PERCENT);
            int jumpValue = (lastPulseValues[patientId] * jumpPercent) / 100;
            boolean positiveJump = random.nextDouble() < JUMP_POSITIVE_PROB;
            int newValue = positiveJump ? lastPulseValues[patientId] + jumpValue : lastPulseValues[patientId] - jumpValue;
            return Math.max(MIN_PULSE_VALUE, Math.min(newValue, MAX_PULSE_VALUE));
        }
        return lastPulseValues[patientId];
    }

    private static int getRandomNumber(int minValue, int maxValue) {
        return random.nextInt(minValue, maxValue + 1);
    }
}
