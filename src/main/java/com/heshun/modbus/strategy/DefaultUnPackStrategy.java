package com.heshun.modbus.strategy;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.modbus.entity.DefaultConvert;
import com.heshun.modbus.entity.DefaultDevicePacket;
import com.heshun.modbus.entity.PacketCrcIllegalException;
import com.heshun.modbus.util.ELog;

/**
 * 默认解包策略，用于消耗掉脏报文
 * 
 * @author huangxz
 * 
 */
public class DefaultUnPackStrategy extends Abs485UnpackStrategy<DefaultDevicePacket, DefaultConvert> {

	public DefaultUnPackStrategy(IoSession s, IoBuffer b) {
		super(s, b);
	}

	@Override
	protected DefaultDevicePacket handle(String signature, List<Byte> packet, DefaultConvert convert) {
		return null;
	}

	@Override
	public DefaultConvert getConvert(DefaultDevicePacket packet) {
		return null;
	}

	@Override
	public DefaultConvert initConvert(int address) {
		return null;
	}

	@Override
	protected DefaultDevicePacket doUnpack() throws PacketCrcIllegalException {
		ELog.getInstance().log("设备回复乱码如下：" + mBuffer.getHexDump(), mSession);
		// 消费掉所有未处理报文即刻
		mBuffer.position(mBuffer.limit());
		return null;
	}
}
