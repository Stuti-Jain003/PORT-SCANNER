import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SweepScanner {

    public static void main(String[] args) {
        // Define command-line options
        Options options = new Options();
        options.addOption("host", true, "List of hosts or wildcard IP address");

        // Create a command-line parser
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            // Parse the command-line arguments
            cmd = parser.parse(options, args);

            if (cmd.hasOption("host")) {
                String hostInput = cmd.getOptionValue("host");
                List<String> hosts = expandHosts(hostInput);

                for (String host : hosts) {
                    System.out.println("Scanning " + host);
                    // Call your scanning method here
                    // Example: scanPort(host, 80); // Scanning port 80
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

    // Example method for scanning ports (needs implementation)
    private static void scanPort(String host, int port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            // Implement your port scanning logic here
            System.out.println("Scanning " + host + ":" + port);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        }
    }
}
