package com.heshun.modbus.entity;

import com.heshun.modbus.entity.driver.CommandBuilder;
import com.heshun.modbus.entity.driver.DeviceDriver;
import com.heshun.modbus.entity.driver.DeviceDriverLoader;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.SessionUtils;
import com.heshun.modbus.util.Utils;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Device {
    public String model;
    public int vCpu;
    public int length;
    public List<byte[]> requestPack;
    public int timeGap = 1000;
    /**
     * 偏移量
     */
    public int offset = 0;

    public Device(int vCpu, String type, int length) {
        this.vCpu = vCpu;
        this.model = type;
        this.length = length;
        genRequestPack(type);
    }

    public Device(String type, int length) {
        this(1, type, length);
    }

    private void genRequestPack(String type) {
        requestPack = new ArrayList<>();
        DeviceDriver driver = DeviceDriverLoader.load(type);
        if (driver != null) {
            for (CommandBuilder c : driver.getCommands())
                requestPack.add(c.generate(vCpu));
            return;
        }


        switch (type) {

            case "WTDL":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0, 4));
                break;
            case "Acuvim":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x105, 4));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x130, 30));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x156, 6));
                break;
            case "spm32":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0, 21));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 21, 8));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 46, 5));
                break;
            case "NZL308M":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x01, 17));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x12, 13));
                break;
            case "aem96":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x50, 28));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x7C, 6));
                break;
            case "pd194z-1":
                // requestPack.add(Utils.fetchRequest(vCpu, 3, 0x3D, 23));
                // requestPack.add(Utils.fetchRequest(vCpu, 3, 0x54, 6));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x06, 40));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x2E, 6));
                break;
            case "pd194z-2":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x3D, 23));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x54, 6));
                break;
            case "disd683":
            case "yh396e":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 14, 11));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 26, 17));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 54, 6));
                break;
            case "eqa300":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x1E, 11));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x2A, 17));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x3C, 6));
                break;
            case "eqa300h":
                requestPack.add(Utils.fetchRequest(vCpu, 4, 0x0B, 14));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x19, 16));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x34, 8));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x3E, 90));
                requestPack.add(Utils.fetchRequest(vCpu, 4, 0x98, 90));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0xF2, 14));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x108, 18));
                break;
            case "PD204E":
                requestPack.add(Utils.fetchRequest(vCpu, 4, 0x06, 18));
                requestPack.add(Utils.fetchRequest(vCpu, 4, 0x18, 30));
                break;
            case "PZ96LE4C":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x23, 44));
                break;
            case "PZ96LE4H":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 242, 39));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 365, 88));
                requestPack.add(Utils.fetchRequest(vCpu, 3, 463, 96));
                break;
            case "yht2tr":
                requestPack.add(Utils.fetchRequest(vCpu, 3, 0x06, 18));
                break;
            default:
                break;
            case "HY194E":
                requestPack.add(Utils.fetchRequest(vCpu, 4, 20, 10));
                requestPack.add(Utils.fetchRequest(vCpu, 4, 30, 17));
                requestPack.add(Utils.fetchRequest(vCpu, 4, 47, 9));
                break;

        }

    }

    public static String getSingnature(byte[] s) {
        return String.format("%s-%s-%s", s[0], s[1], s[5] * 2);
    }

    public void sendRequests(IoSession s) {
        int i = 1;
        for (byte[] command : requestPack) {
            ELog.getInstance().log(String.format("发送查询[%s]，查询命令(%s)--->%s", SessionUtils.getLogoType(s), i++,
                    Arrays.toString(command)), s);
            s.write(command);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
