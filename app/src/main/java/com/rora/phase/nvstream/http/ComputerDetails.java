package com.rora.phase.nvstream.http;

import com.google.gson.annotations.Expose;
import com.rora.phase.model.Host;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.List;


public class ComputerDetails implements Serializable {
    public String hostId;
    public int httpsPort1;
    private int httpPort;

    public enum State {
        ONLINE, OFFLINE, UNKNOWN
    }

    // Persistent attributes
    public String uuid;
    public String name;
    public String localAddress;
    public String remoteAddress;
    public String manualAddress;
    public String ipv6Address;
    public String macAddress;
    public X509Certificate serverCert;

    // Transient attributes
    public State state;
    public String activeAddress;
    public PairingManager.PairState pairState;
    public int runningGameId;
    public String rawAppList;
    public List<NvApp> appList;
    public NvApp remoteApp;

    public ComputerDetails() {
        // Use defaults
        state = State.UNKNOWN;
    }

    public ComputerDetails(ComputerDetails details) {
        // Copy details from the other computer
        update(details);
    }

    public ComputerDetails(Host host) {
        //this.hostId = host.getIdHost();
        //this.manualAddress = host.getPublicIP();
        //this.httpsPort1 = host.getPort();
        //state = State.UNKNOWN;
        this.hostId = host.getIdHost();
        this.name = host.getIdHost().toUpperCase();
        this.activeAddress = host.getPublicIP();
        this.manualAddress = host.getPublicIP();
        this.localAddress = host.getLanIP();
        this.macAddress = host.getMacAddress();
        this.httpsPort1 = host.getPort();
        state = State.ONLINE;
        pairState = PairingManager.PairState.NOT_PAIRED;
    }

    public void update(ComputerDetails details) {
        this.state = details.state;
        this.name = details.name;
        this.uuid = details.uuid;
        if (details.activeAddress != null) {
            this.activeAddress = details.activeAddress;
        }
        // We can get IPv4 loopback addresses with GS IPv6 Forwarder
        if (details.localAddress != null && !details.localAddress.startsWith("127.")) {
            this.localAddress = details.localAddress;
        }
        if (details.remoteAddress != null) {
            this.remoteAddress = details.remoteAddress;
        }
        if (details.manualAddress != null) {
            this.manualAddress = details.manualAddress;
        }
        if (details.ipv6Address != null) {
            this.ipv6Address = details.ipv6Address;
        }
        if (details.macAddress != null && !details.macAddress.equals("00:00:00:00:00:00")) {
            this.macAddress = details.macAddress;
        }
        if (details.serverCert != null) {
            this.serverCert = details.serverCert;
        }
        this.pairState = details.pairState;
        this.runningGameId = details.runningGameId;
        this.rawAppList = details.rawAppList;
    }

    public NvApp getRemoteApp() {
        return remoteApp;
    }

    public void setRemoteApp(NvApp remoteApp) {
        this.remoteApp = remoteApp;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Name: ").append(name).append("\n");
        str.append("State: ").append(state).append("\n");
        str.append("Active Address: ").append(activeAddress).append("\n");
        str.append("UUID: ").append(uuid).append("\n");
        str.append("Local Address: ").append(localAddress).append("\n");
        str.append("Remote Address: ").append(remoteAddress).append("\n");
        str.append("IPv6 Address: ").append(ipv6Address).append("\n");
        str.append("Manual Address: ").append(manualAddress).append("\n");
        str.append("MAC Address: ").append(macAddress).append("\n");
        str.append("Pair State: ").append(pairState).append("\n");
        str.append("Running Game ID: ").append(runningGameId).append("\n");
        return str.toString();
    }
}
