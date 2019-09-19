package com.heshun.modbus.helper;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


/**
 * 网关组包，拆包工厂
 *
 * @author huangxz
 *
 */
public class GateWay485CodecFactory implements ProtocolCodecFactory {

	ProtocolDecoder mDecoder;

	ProtocolEncoder mEncoder;

	public GateWay485CodecFactory() {

		mDecoder = new GateWay485Decoder();

		mEncoder = new GateWay485Encoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return mDecoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return mEncoder;
	}

}
