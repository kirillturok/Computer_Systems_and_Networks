package com.company;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Transform {

    public static byte[] getIpFromSubnetAndNodeNumber(byte[] localIp, long nodeNumber, int maskLen) {
        int ipNum = ((localIp[0] & 0xFF) << 24) + ((localIp[1] & 0xFF) << 16) + ((localIp[2] & 0xFF) << 8) + (localIp[3] & 0xFF);
        long ipNumVal = ipNum & 0xFFFFFFFFL;
        long normalizedIpVal = ipNumVal | ((1L << (32 - maskLen)) - 1);
        long maskVal = ((1L << maskLen) - 1) << (32 - maskLen);
        long nodeVal = maskVal | nodeNumber;
        long resultingIpNum = normalizedIpVal & nodeVal;
        return getBytes(resultingIpNum);
    }

    public static byte[] getBytes(long resultingIpNum) {
        byte[] bt = new byte[4];

        byte firstOctant = (byte) (resultingIpNum >> 24);
        byte secondOctant = (byte) ((resultingIpNum & 0x00FFFFFF) >> 16);
        byte thirdOctant = (byte) ((resultingIpNum & 0x0000FFFF) >> 8);
        byte fourthOctant = (byte) (resultingIpNum & 0x000000FF);
        return new byte[]{firstOctant, secondOctant, thirdOctant, fourthOctant};
    }

    public static long getMaxHostsCount(byte[] mask) {
        int maskNum = ((mask[0] & 0xFF) << 24) + ((mask[1] & 0xFF) << 16) + ((mask[2] & 0xFF) << 8) + (mask[3] & 0xFF);
        return (~maskNum) & 0xFFFFFFFFL;
    }

    public static byte[] getSubnet(byte[] localIp, byte[] mask) {
        byte[] subnet = new byte[4];
        for(int i=0;i<4;i++)
            subnet[i] = (byte) (localIp[i]&mask[i]);
        return subnet;
    }

    public static String getIpForDisplay(byte[] ip) {
        int i = 4;
        StringBuilder ipBuilder = new StringBuilder();
        for (byte octant : ip) {
            ipBuilder.append(octant & 0xFF);
            if (--i > 0) {
                ipBuilder.append('.');
            }
        }
        return ipBuilder.toString();
    }

    public static byte[] getMask(int maskLen) {
        long maskVal = ((1L << maskLen) - 1) << (32 - maskLen);
        return getBytes(maskVal);
    }

    public static String getMACfromRawBytes(byte[] mac) {
        if (mac == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length-1; i++)
            sb.append(String.format("%02X%s", mac[i], ":"));
        sb.append(String.format("%02X",mac[mac.length-1]));
        return sb.toString();
    }
}
