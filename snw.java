import java.io.*;
import java.net.*;

public class StopAndWaitProtocolDemo {
    private static final int PORT = 10000;

    public static void main(String[] args) {
        // Simulated sender and receiver
        Thread senderThread = new Thread(new Sender());
        Thread receiverThread = new Thread(new Receiver());

        senderThread.start();
        receiverThread.start();
    }

    static class Sender implements Runnable {
        @Override
        public void run() {
            try {
                DatagramSocket senderSocket = new DatagramSocket();
                InetAddress receiverAddress = InetAddress.getLocalHost();

                for (int i = 1; i <= 5; i++) {
                    String message = "Frame " + i;
                    byte[] sendData = message.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, PORT);
                    senderSocket.send(sendPacket);

                    System.out.println("Sender: Sent " + message);

                    // Simulate waiting for ACK
                    Thread.sleep(2000);

                    // Simulate receiving ACK (assuming no errors)
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    senderSocket.receive(receivePacket);
                    String ackMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Sender: Received ACK for " + ackMessage);
                }

                senderSocket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                DatagramSocket receiverSocket = new DatagramSocket(PORT);

                for (int i = 1; i <= 5; i++) {
                    // Simulate receiving a frame
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiverSocket.receive(receivePacket);
                    String frameMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Receiver: Received " + frameMessage);

                    // Simulate processing the frame and sending an ACK
                    Thread.sleep(1000);
                    String ackMessage = "ACK for Frame " + i;
                    byte[] ackData = ackMessage.getBytes();
                    DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, receivePacket.getAddress(), receivePacket.getPort());
                    receiverSocket.send(ackPacket);

                    System.out.println("Receiver: Sent ACK for " + frameMessage);
                }

                receiverSocket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
