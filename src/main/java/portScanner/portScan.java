package portScanner;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// a CLI program that will accept two command line arguments(host and port)

public class portScan {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java portScanner.portScan <host> [<port>]");
            return;
        }

        String host = args[0];
        int maxThreads = 100; // Set maximum threads
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        if (args.length == 2) {
            // Specific port scan
            int port = Integer.parseInt(args[1]);
            scanPort(host, port);
        } else {
            // Full port range scan
            for (int port = 1; port <= 65535; port++) {
                int finalPort = port;
                executor.execute(() -> scanPort(host, finalPort));
            }
        }

        executor.shutdown();
    }

    private static void scanPort(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Port " + port + " is open.");
        } catch (IOException e) {
            // Port is closed or unreachable
        }
    }
}
