package com.heshun.modbus.util;

import com.heshun.modbus.common.Config;
import com.heshun.modbus.helper.SystemHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    public static byte[] updateWithCrc16(byte[] arr_buff) {
        byte[] result = Arrays.copyOf(arr_buff, arr_buff.length + 2);
        int len = arr_buff.length;

        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }

        byte[] _crc = getBytes(crc);
        result[result.length - 2] = _crc[0];
        result[result.length - 1] = _crc[1];

        return result;
    }

    public static long bytes2Long(byte[] high, byte[] low) {

        byte[] bytes = new byte[8];
        bytes[4] = high[1];
        bytes[5] = high[0];
        bytes[6] = low[1];
        bytes[7] = low[0];

        return ByteUtils.byte8ToLong(bytes) & 0xFFFFFFFFl;
    }

    public static long byte4toLongR(byte[] bs) {
        byte[] arr = new byte[8];
        arr[4] = bs[2];
        arr[5] = bs[3];
        arr[6] = bs[0];
        arr[7] = bs[1];

        return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
                | ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24) | ((long) (arr[5] & 0xff) << 16)
                | ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
    }

    public static long byte4toLong(byte[] bs) {
        byte[] arr = new byte[8];
        arr[4] = bs[0];
        arr[5] = bs[1];
        arr[6] = bs[2];
        arr[7] = bs[3];

        return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
                | ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24) | ((long) (arr[5] & 0xff) << 16)
                | ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
    }

    public static byte[] getBytes(short s) {
        int count = 2;
        byte[] arr = new byte[count];
        for (int i = 0; i < count; i++) {
            arr[i] = (byte) ((int) (s >> i * 8 & 0xFF));
        }
        return arr;
    }

    public static byte[] getBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static void printBitString(short s) {
    }

    /*
     * 字节转二进制字符串
     */
    public static String getBitString(byte b) {
        int z = b;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    /**
     * 字节数组转int

     */
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /*
     * 将二进制字符串转换回字节
     */
    public static byte bit2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    /*
     * char转byte数组
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * short转二进制字符串
     */
    public static String short2bitString(short s) {
        byte[] bytes = getBytes(s);
        return bytes[0] + "" + bytes[1];

    }

    public static short bytes2Short(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    public static short bytes2Short(byte high, byte low) {
        return bytes2Short(high, low, false);
    }

    /**
     * 高低顺序反的情况reverse为true
     */
    public static short bytes2Short(byte high, byte low, boolean reverse) {
        if (reverse) {
            return (short) (((high << 8) | low & 0xff));
        } else {
            return (short) (((low << 8) | high & 0xff));
        }
    }

    /**
     * 高低顺序反的情况reverse为true
     */
    public static short bytes2Short(byte[] origin, boolean reverse) {
        byte high = origin[0];
        byte low = origin[1];
        if (reverse) {
            return (short) (((high << 8) | low & 0xff));
        } else {
            return (short) (((low << 8) | high & 0xff));
        }
    }

    public static int bytes2UnsiginShort(byte high, byte low, boolean reverse) {
        if (reverse) {
            return ((high << 8) | low & 0xff) & 0xFFFF;
        } else {
            return ((low << 8) | high & 0xff) & 0xFFFF;
        }
    }


    public static int bytes2UnsiginShort(byte[] origin, boolean reverse) {
        byte high = origin[0];
        byte low = origin[1];
        if (reverse) {
            return ((high << 8) | low & 0xff) & 0xFFFF;
        } else {
            return ((low << 8) | high & 0xff) & 0xFFFF;
        }
    }

    // public static long bytes2Long(byte[] high, byte[] low) {
    //
    // byte[] bytes = new byte[8];
    // bytes[4] = high[1];
    // bytes[5] = high[0];
    // bytes[6] = low[1];
    // bytes[7] = low[0];
    //
    // return ByteUtils.byte8ToLong(bytes) & 0xFFFFFFFFl;
    // }

    /**
     * @param date 需要取各位的byte
     * @return 以低高的顺序输出
     */
    public static int[] getEachBit(byte date) {
        int[] array = new int[8];
        for (int i = 0; i < 8; i++) {
            array[i] = (date & (1 << i)) >>> i;
        }
        return array;
    }

    // public static byte[] fetchRequest1(int pid, int funcCode, int start, int
    // offset) {
    // byte[] request = new byte[] { 01, 03, 00, 01, 00, 01 };
    // request[0] = (byte) pid;
    // request[1] = (byte) funcCode;
    // request[3] = (byte) start;
    // request[5] = (byte) offset;
    //
    // return updateWithCrc16(request);
    //
    // }

    public static byte[] fetchRequest(int pid, int funcCode, int start, int offset) {
        byte[] request = new byte[]{(byte) pid, (byte) funcCode};
        request = Arrays.copyOf(request, 6);

        byte[] _start = ByteUtils.shortToByte2((short) start);
        byte[] _offset = ByteUtils.shortToByte2((short) offset);

        request[2] = _start[0];
        request[3] = _start[1];
        request[4] = _offset[0];
        request[5] = _offset[1];

        return updateWithCrc16(request);

    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    /**
     * byte数组转float
     *
     * @param b
     * @return
     */
    public static float intBytes2Float(byte[] b) {
        return Float.intBitsToFloat(Utils.byteArrayToInt(b, 0));
    }

    /**
     * 移出offset个byte到新的数组里
     *
     * @param list
     * @param offset
     * @return
     */
    public static byte[] listPop2byteArray(List<Byte> list, int offset) {
        if (offset <= list.size()) {
            byte[] result = new byte[offset];
            for (int i = 0; i < offset; i++) {
                result[i] = list.remove(0);
            }
            return result;
        } else
            return new byte[0];

    }


    /**
     * /dri/%s.dr
     */
    public static File getConfigFile(String path, String fileName) throws FileNotFoundException {
        try {
            if (Config.isDebug)
                return new File("src/main/resource/" + path + "/" + fileName);
            else {
                File dir = new File(SystemHelper.class.getResource("/").getPath());
                File dri = new File(dir, path);

                return new File(dri, fileName);
            }
        } catch (NullPointerException e) {
            throw new FileNotFoundException(path);
        }
    }
}
