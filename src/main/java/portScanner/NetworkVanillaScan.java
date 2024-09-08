package portScanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkVanillaScan {

    private static final int TIMEOUT = 1000; // Timeout in milliseconds

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java HostPortScanner <networkPrefix> <startIP> <endIP> [<ports>]");
            System.out.println("Example (specific ports): java HostPortScanner 192.168.1. 1 254 21,22,80,443");
            System.out.println("Example (all ports): java HostPortScanner 192.168.1. 1 254 all");
            return;
        }

        String networkPrefix = args[0];
        int startIP = Integer.parseInt(args[1]);
        int endIP = Integer.parseInt(args[2]);

        int[] ports;


        if (args.length == 4 && args[3].equalsIgnoreCase("all"))
        {
//   A vanilla scan is the most basic scan offered by a port scanner
//   it will attempt to open a full TCP connection to each of the 65,535 ports on a server.
            ports = new int[65536];
            for (int i = 0; i < 65536; i++) {
                ports[i] = i;
            }
        } else if (args.length == 4) {
            ports = Arrays.stream(args[3].split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } else {
            ports = new int[]{21, 22, 80, 443}; // Default common ports
        }

        if (startIP < 0 || endIP > 255 || startIP > endIP) {
            System.out.println("Invalid IP range. Ensure 0 <= startIP <= endIP <= 255.");
            return;
        }

        // Thread pool to handle multiple scanning tasks concurrently
        ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust pool size as needed

        for (int i = startIP; i <= endIP; i++) {
            String ip = networkPrefix + i;
            if (isHostReachable(ip)) {
                System.out.println("Host " + ip + " is reachable.");
                // Submit the port scanning task to the executor
                executor.submit(() -> scanPorts(ip, ports));
            }
        }

        executor.shutdown(); // Shut down the executor once all tasks are submitted
    }

    // Method to check if the host is reachable
    private static boolean isHostReachable(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(TIMEOUT);
        } catch (IOException e) {
            System.err.println("Error reaching host: " + ipAddress);
        }
        return false;
    }

    // Method to scan ports on a reachable host using multiple threads
    private static void scanPorts(String host, int[] ports) {
        ExecutorService portExecutor = Executors.newFixedThreadPool(50); // Adjust size based on performance needs
        for (int port : ports) {
            portExecutor.submit(() -> {
                try (Socket socket = new Socket(host, port)) {
                    System.out.println("Port " + port + " is open on " + host);
                } catch (IOException e) {
                    // Port is closed or cannot connect
                }
            });
        }
        portExecutor.shutdown(); // Shut down the port executor once all port scan tasks are submitted
    }
}

