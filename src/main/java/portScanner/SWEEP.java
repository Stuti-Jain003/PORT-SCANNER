import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
//javac -cp ".;C:\Users\sjstu\.m2\repository\commons-cli\commons-cli\1.4\commons-cli-1.4.jar" SWEEP.java
//java -cp ".;C:\Users\sjstu\.m2\repository\commons-cli\commons-cli\1.4\commons-cli-1.4.jar" SWEEP -host "192.168.1.*" -port 80

public class SWEEP {

    public static void main(String[] args) {
        // Define command-line options
        Options options = new Options();
        options.addOption("host", true, "List of hosts or wildcard IP address");
        options.addOption("port", true, "Port to scan");

        // Create a command-line parser
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            // Parse the command-line arguments
            cmd = parser.parse(options, args);

            if (cmd.hasOption("host") && cmd.hasOption("port")) {
                String hostInput = cmd.getOptionValue("host");
                int port = Integer.parseInt(cmd.getOptionValue("port"));
                List<String> hosts = expandHosts(hostInput);

                for (String host : hosts) {
                    System.out.println("Scanning " + host);
                    scanPort(host, port);
                }
            } else {
                formatter.printHelp("SweepScan", options);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SweepScan", options);
        }
    }

    // Method to expand wildcard IP addresses
    private static List<String> expandHosts(String hostInput) {
        List<String> hosts = new ArrayList<>();

        if (hostInput.contains("*")) {
            String[] parts = hostInput.split("\\.");
            if (parts.length == 4) {
                String base = parts[0] + "." + parts[1] + "." + parts[2] + ".";
                for (int i = 1; i <= 254; i++) {
                    hosts.add(base + i);
                }
            }
        } else if (hostInput.contains("/")) {
            // Handle CIDR notation if needed
        } else {
            hosts.add(hostInput);
        }

        return hosts;
    }

    // Method to scan a specific port on a host
    private static void scanPort(String host, int port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            try (Socket socket = new Socket(address, port)) {
                System.out.println("Port " + port + " is open on " + host);
            } catch (IOException e) {
                System.out.println("Port " + port + " is closed on " + host);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        }
    }
}
