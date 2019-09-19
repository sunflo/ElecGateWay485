package com.heshun.modbus.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.modbus.common.GlobalStorage;
import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.entity.DefaultDevicePacket;
import com.heshun.modbus.entity.PacketCrcIllegalException;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.SessionUtils;

public abstract class Abs485UnpackStrategy<T extends DefaultDevicePacket, V extends AbsJsonConvert<T>> {
    IoSession mSession;
    IoBuffer mBuffer;

    public Abs485UnpackStrategy(IoSession s, IoBuffer b) {
        this.mBuffer = b;
        this.mSession = s;
    }

    public V unPack() throws PacketCrcIllegalException {
        ELog.getInstance().log(String.format("原始报文[%s]:\n %s", getLogTag(), mBuffer.getHexDump()), mSession);
        return getConvert(doUnpack());
    }

    protected String getLogTag() {
        String simpleName = getClass().getSimpleName();
        return simpleName.replace("Strategy", "");
    }

    protected T doUnpack() throws PacketCrcIllegalException {
        // 预留CRC校验方法,后期可能要加上容错机制
        if (!validateCrc())
            throw new PacketCrcIllegalException();

        int address = (int) mBuffer.get();
        int command = (int) mBuffer.get();

        int size = mBuffer.get() & 0xff;

        String signature = String.format("%s-%s", command, size);
        List<Byte> packet = new ArrayList<>();
        for (int i = 0; i < size; i++)
            packet.add(mBuffer.get());
        // 消费掉两个字节的CRC校验
        mBuffer.get();
        mBuffer.get();

        int logotype = SessionUtils.getLogoType(mSession);

        AbsJsonConvert<? extends DefaultDevicePacket> convert = GlobalStorage.getInstance().getConvertInStorage(logotype, address,
                initConvert(address));
        convert.updateGatherTime();

        return handle(signature, packet, (V) convert);

    }

    private boolean validateCrc() {
        return true;
    }

    protected abstract T handle(String signature, List<Byte> packet, V convert);

    public abstract V getConvert(T packet);

    public abstract V initConvert(int address);

}
