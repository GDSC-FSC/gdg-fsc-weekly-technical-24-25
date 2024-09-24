package com.dnsserver;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {

    String forwardServer = null;

    if (Arrays.asList(args).contains("--resolver")) {
      forwardServer = args[1];
      LOGGER.log(Level.INFO,"Forwarding DNS requests to: {0}", forwardServer);
    }

    try (final DnsServer server = new DnsServer(2053, forwardServer)) {
      LOGGER.log(Level.INFO, "Starting DNS server on port 2053");
      server.start();

    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to start DNS server: {0}", e.getMessage());
    }
  }
}
