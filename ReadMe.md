# Port Scanner CLI Application

## Overview:
>This project is a command-line tool for network port scanning, similar to popular tools like Nmap. It provides different types of scans to identify open ports and active hosts within a network. This scanner is useful for network administrators, security professionals, and developers interested in understanding network security.

## Features:
- Basic IP Port Scan: Scans a specific IP and port to check if the port is open.
- Vanilla Scan: Scans all 65,535 ports on a given host to identify open ports.
- Sweep Scan: Scans a specified port across a range of hosts to identify active devices.
- Network Scan: Scans multiple hosts and ports in parallel for efficient and fast scanning.
- SYN Scan: Performs a stealthy half-open scan by sending SYN packets and identifying open ports without completing the TCP handshake.

## Implementation:
>The tool is implemented in Java and uses multithreading for parallel scanning, enhancing the speed and efficiency of port scanning. The application includes the following main components:
components:

- Basic IP Port Scan: Connects to a given IP and port to determine if the port is open. If a connection is successful, the port is reported as open.
- Vanilla Scan: Attempts to connect to all ports on a specified host by iterating through port numbers 1 to 65535, using full TCP connections.
- Sweep Scan: Scans the same port across a list of hosts or a network range. It helps identify active devices within a network.
- Network Scan: Tests multiple ports across multiple hosts simultaneously, using Java's concurrency utilities for faster results.
- SYN Scan: Sends SYN packets to target ports to detect open ports without completing the TCP handshake, allowing for stealthier scans.

## How It Works:
1. Input: The user specifies the scan type, target IP or range of IPs, and ports through command-line arguments.
2. Packet Crafting: For SYN scans, raw packets are crafted to send SYN requests to target ports.
3. Connection Testing: For basic, vanilla, sweep, and network scans, the tool tries to establish a TCP connection. If successful, the port is reported as open.
4. Multithreading: Parallel scanning is implemented using Java’s concurrency framework to handle multiple ports and hosts simultaneously.
5. Output: Results are displayed in the command line, showing which ports are open for the specified hosts.

## Usage:

### Requirements:
- Java 8 or higher
- pcap4j library for packet crafting (used in SYN scan)
- npcap installed for packet capture and sending on Windows (or equivalent for other OS)

### Commands:
- Basic IP Port Scan:
  > java -cp target/classes portScanner.postScan <host> <port>
- Vanilla Scan:
  >java -cp target/classes portScanner.NetworkVanillaScan <host>
- Sweep Scan:
  > java -cp target/classes portScanner.SweepScanner <hosts>
- Network Scan:
  > java -cp target/classes portScanner.HostPortScanner <network>
- SYN Scan:
  > java -cp target/classes portScanner.SynScanner <host> <port>
## Disclaimer
>Use Responsibly: Only run port scans on hosts that you own or have permission to test. Unauthorized scanning can be illegal and against policies.

## Contribution:
>Contributions, suggestions, and improvements are welcome! Please submit a pull request or raise an issue to start contributing.

## License:
MIT License
