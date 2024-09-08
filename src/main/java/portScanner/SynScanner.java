package portScanner;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.util.MacAddress;

import java.net.Inet4Address;
import java.net.InetAddress;

public class SynScanner {

    // Set your actual network interface name here
    private static final String NETWORK_INTERFACE = "Ethernet"; // Replace with "eth0", "Wi-Fi", etc.

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java SynScanner <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // Get network interface
        PcapNetworkInterface nif = Pcaps.getDevByName(NETWORK_INTERFACE);
        if (nif == null) {
            System.err.println("Network interface not found.");
            return;
        }

        // Open handle
        PcapHandle handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);

        // Build and send SYN packet
        sendSynPacket(handle, host, port);

        // Close handle
        handle.close();
    }

    // Function to send SYN packet
    private static void sendSynPacket(PcapHandle handle, String host, int port) throws Exception {
        InetAddress srcIp = InetAddress.getLocalHost();
        InetAddress dstIp = InetAddress.getByName(host);
        MacAddress srcMac = MacAddress.getByName("0A-00-27-00-00-06"); // Replace with your actual MAC address
        MacAddress dstMac = MacAddress.ETHER_BROADCAST_ADDRESS; // Use specific MAC if known

        // Build TCP packet
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder();
        tcpBuilder.srcPort(TcpPort.getInstance((short) 50000)) // Arbitrary source port
                .dstPort(TcpPort.getInstance((short) port))
                .syn(true)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);

        // Build IP packet
        IpV4Packet.Builder ipBuilder = new IpV4Packet.Builder();
        ipBuilder.srcAddr((Inet4Address) srcIp)
                .dstAddr((Inet4Address) dstIp)
                .protocol(IpNumber.TCP)
                .ttl((byte) 64)
                .payloadBuilder(tcpBuilder);

        // Build Ethernet packet
        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder.srcAddr(srcMac)
                .dstAddr(dstMac)
                .type(EtherType.IPV4)
                .payloadBuilder(ipBuilder)
                .paddingAtBuild(true); // Add padding if needed

        // Send SYN packet
        handle.sendPacket(etherBuilder.build());
        System.out.println("SYN packet sent to " + host + ":" + port);

        // Listen for SYN-ACK response
        listenForSynAck(handle);
    }

    // Function to listen for SYN-ACK responses
    private static void listenForSynAck(PcapHandle handle) {
        try {
            PacketListener listener = packet -> {
                if (packet.contains(TcpPacket.class)) {
                    TcpPacket tcpPacket = packet.get(TcpPacket.class);
                    if (tcpPacket.getHeader().getSyn() && tcpPacket.getHeader().getAck()) {
                        System.out.println("Port is open.");
                    }
                }
            };

            handle.loop(10, listener); // Listen for 10 packets or until SYN-ACK received
        } catch (InterruptedException | PcapNativeException | NotOpenException e) {
            System.err.println("Error receiving SYN-ACK: " + e.getMessage());
        }
    }

}
