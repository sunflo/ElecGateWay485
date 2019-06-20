package com.heshun.modbus.strategy.common;

import com.heshun.modbus.entity.driver.DeviceDriver;
import com.heshun.modbus.entity.driver.DriverItem;
import com.heshun.modbus.entity.driver.DriverLoader;
import com.heshun.modbus.strategy.Abs485UnpackStrategy;
import com.heshun.modbus.util.Utils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.List;
import java.util.Map;

/**
 * t 通用解析策略，从驱动配置文件获取解析要素
 */
public class CommonUnPackStrategy extends Abs485UnpackStrategy<CommonDevicePack, CommonDeviceConvert> {
    private DeviceDriver mDriver;

    public CommonUnPackStrategy(IoSession s, IoBuffer b, String deviceModel) {
        super(s, b);
        mDriver = DriverLoader.load(deviceModel);
    }

    @Override
    protected CommonDevicePack handle(String signature, List<Byte> packet, CommonDeviceConvert convert) {
        CommonDevicePack original = convert.getOriginal();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            //3-22,1
            String key = entry.getKey();
            String[] split = key.split(",");
            String group = split[0];
            if (!group.equalsIgnoreCase(signature))
                continue;
//            int index = Integer.valueOf(split[1]);
            DriverItem rule = mDriver.get(key);
            original.put(key, getValue(rule, packet));
        }
        return original;
    }

    private Object getValue(DriverItem rule, List<Byte> packet) {
        DriverItem.DataType dataType = rule.getDataType();
        int index = rule.getIndex();
        int offset = rule.getOffset();
        boolean reverse = rule.isReverse();
        byte[] temp = new byte[offset];
        for (int i = 0; i < offset; i++) {
            temp[i] = packet.get(index + i);
        }
        switch (dataType) {
            case BYT:
                return temp[0] & 0xf;
            case LNG:
                return Utils.byte4toLong(temp);
            case FLT:
            case DBLE:
                return Utils.intBytes2Float(temp);
            case SHT_UNS:
            case INT_UNS:
                return Utils.bytes2UnsiginShort(temp, reverse);
            default:
            case SHT:
            case INT:
                return Utils.bytes2Short(temp, reverse);
        }


    }

    @Override
    public CommonDeviceConvert getConvert(CommonDevicePack packet) {
        return new CommonDeviceConvert(packet);
    }

    @Override
    public CommonDeviceConvert initConvert(int address) {
        return new CommonDeviceConvert(new CommonDevicePack(address, mDriver));
    }

    @Override
    protected String getLogTag() {
        return mDriver.getName();
    }
}
