package com.heshun.modbus.strategy.pd194z._2;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.modbus.strategy.Abs485UnpackStrategy;
import com.heshun.modbus.util.Utils;

 
public class PD194ZStrategy extends Abs485UnpackStrategy<PD194ZPacket, PD194ZConvert> {

	public PD194ZStrategy(IoSession s, IoBuffer b) {
		super(s, b);
	}

	@Override
	public PD194ZConvert getConvert(PD194ZPacket packet) {
		return new PD194ZConvert(packet);
	}

	@Override
	public PD194ZConvert initConvert(int address) {
		return new PD194ZConvert(new PD194ZPacket(address));
	}

	@Override
	protected PD194ZPacket handle(String signature, List<Byte> packet, PD194ZConvert convert) {
		PD194ZPacket p = convert.getOriginal();
		// 按回复报文的签名辨别报文顺序，从而解析到对应字段
		switch (signature) {
		// 01 03 16 ,03 01 04 00 ,09 4D 09 51 09 55, 10 1F 10 26 10 22, 02 BC 01
		// F1 02 9D, 03 E2
		case "3-46":
			p.set_ua(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_ub(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_uc(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_uab(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_ubc(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_uca(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_ia(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_ib(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_ic(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_pa(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_pb(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_pc(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_ptotal(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_qa(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_qb(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_qc(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_qtotal(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_sa(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_sb(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_sc(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_stotal(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			//
			p.set_pftotal(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			p.set_freq(Utils.bytes2Short(packet.remove(0), packet.remove(0), true));
			break;
		// 01 03 22 13 89 00 73 00 47 00 79 01 3D 00 61 00 61 00 65 01 27 00 97
		// 00 79 00 A3 01 BD 03 23 02 2F 02 D9 02 C8 BE 11
		case "3-16":
			p.set_epi(Utils.byte4toLong(new byte[] { packet.remove(0), packet.remove(0), packet.remove(0),
					packet.remove(0) }));

			packet.remove(0);
			packet.remove(0);
			packet.remove(0);
			packet.remove(0);
			p.set_eql(Utils.byte4toLong(new byte[] { packet.remove(0), packet.remove(0), packet.remove(0),
					packet.remove(0) }));
			break;
		// 01 03 0C 00 00 DE B4 00 00 00 01 00 00 EE 07 C0 89
	 
		}
		return p;
		
	}
}
