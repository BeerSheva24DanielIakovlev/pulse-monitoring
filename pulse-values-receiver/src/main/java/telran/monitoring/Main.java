package telran.monitoring;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class Main {
    private static final int PORT = 3500;
    private static final int MAX_SIZE = 1500;
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[MAX_SIZE];
        while (true) {   
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println(new String(packet.getData()));
            socket.send(packet);
        }
    };
}