package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getHardwareAddress() != null) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        //System.out.println("******"+inetAddress);
                        if (inetAddress instanceof Inet4Address) {
                            processLocalIp(inetAddress);
                            //System.out.println(NetworkInterface.getByInetAddress(inetAddress));
                            break;
                        }
                    }
                }
            }
        } catch (SocketException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void processLocalIp(InetAddress address) throws IOException, InterruptedException {
        System.out.println("---NEW-INTERFACE---");
        NetworkInterface anInterface = NetworkInterface.getByInetAddress(address);
        byte[] mac = anInterface.getHardwareAddress();
        String macPC = Transform.getMACfromRawBytes(mac);
        String hostName = address.getHostName();
        System.out.println("Name: " + hostName);
        System.out.println("MAC: " + macPC);

        List<InterfaceAddress> interfaceAddressesPC = anInterface.getInterfaceAddresses();
        InterfaceAddress interfaceAddress = interfaceAddressesPC.get(0);

        int maskLen = interfaceAddress.getNetworkPrefixLength();
        byte[] mask = Transform.getMask(maskLen);
        String displayMask = Transform.getIpForDisplay(mask);
        System.out.println("Subnet Mask: " + displayMask);

        byte[] localIp = address.getAddress();
        byte[] subnet = Transform.getSubnet(localIp, mask);
        String displaySubnet = Transform.getIpForDisplay(subnet);
        System.out.println("Subnet: " + displaySubnet);

        long maxHostsCount = Transform.getMaxHostsCount(mask);

        for (long nodeNumber = 1; nodeNumber <= maxHostsCount; nodeNumber++) {
            byte[] currIp = Transform.getIpFromSubnetAndNodeNumber(localIp, nodeNumber, maskLen);
            String displayIp = Transform.getIpForDisplay(currIp);
            InetAddress i = InetAddress.getByName(displayIp);
            i.isReachable(250);
        }


        System.out.println();
        for (long nodeNumber = 1; nodeNumber <= maxHostsCount; nodeNumber++) {
            byte[] currIp = Transform.getIpFromSubnetAndNodeNumber(localIp, nodeNumber, maskLen);
            String displayIp = Transform.getIpForDisplay(currIp);
            List<String> lines;
            Process p = Runtime.getRuntime().exec("arp -a " + displayIp);
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            lines = inputStream.lines().collect(Collectors.toList());
            if (lines.size() > 1) {
                String[] ss = lines.get(3).trim().split("[ ]+");
                //long t1=System.currentTimeMillis();
                InetAddress i = InetAddress.getByName(ss[0]);
                //long t2=System.currentTimeMillis();
                //System.out.println("1|"+(t2-t1));
                System.out.println("Name: "+i.getHostName()+
                        "\nIP: "+ss[0]+"\nMAC: "+ss[1]);
                //long t3 = System.currentTimeMillis();
                //System.out.println("2|"+(t3-t2));
                System.out.println("===");
            }

        }

    }
}