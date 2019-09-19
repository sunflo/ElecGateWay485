package com.heshun.modbus.helper;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.heshun.modbus.entity.Device;
import com.heshun.modbus.entity.PacketCrcIllegalException;
import com.heshun.modbus.strategy.Abs485UnpackStrategy;
import com.heshun.modbus.strategy.DecodeStrategyFactory;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.SessionUtils;

/**
 * 报文解析器
 *
 * @author huangxz
 *
 */
public class GateWay485Decoder extends CumulativeProtocolDecoder {

	private Abs485UnpackStrategy<?, ?> mUnPacker;

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		in.mark();
		byte _head = in.get();
		in.reset();
		switch (_head) {
		// 标识
		case 0x72:
			try {
				if (in.remaining() < 8) {
					return false;
				}
				byte[] _logotype = new byte[5];
				// 前3个字节，[rtu],取出来丢弃即刻，重点是后面的5个数字
				// 特殊字符用于辨别DTU设备，预留供通用网关整合用
				in.get(new byte[3]);
				in.get(_logotype);
				// DTU是按string类型传输心跳、标识的。
				String s = new String(_logotype);
				// 一個session通道必定對應一個唯一的logotype,所以放入 session对象中，备用
				SessionUtils.setLogoType(s, session);

				// 一个session通道关联一个DTU,DTU下挂设备固定，所以将设备字典放入session对象中。备用
				if (!SessionUtils.bindDevices(s, session)) {
					session.closeNow();
				}
				ELog.getInstance().log(s, Integer.valueOf(s));
			} catch (NumberFormatException e) {
				ELog.getInstance().err("标识解析错误，异常链接，关闭。。。。。。" + e.getMessage(), session);
				session.closeNow();

			} catch (IOException e) {
				ELog.getInstance().err("未找到该ID的注册信息，异常链接，关闭。。。。。。" + e.getMessage(), session);
				session.closeNow();
			}

			return true;

		default:
			// 如果是返回的查询报文，做解包处理
			// 01 04 12 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 47
			// 35
			try {
				in.mark();
				int address = (int) in.get();
				in.get();
				int size = (int) in.get();
				// 剩余报文长度不够，证明断包，等待下次接收
				if (in.remaining() < size + 2) {
					return false;
				}
				in.reset();
				Device d = SessionUtils.getDevices(session).get(address);
				mUnPacker = DecodeStrategyFactory.fetchUnPacker(d, session, in);
				// 往IOHandler写出，其实handler中只需要通知界面刷新即刻
				Object msg = mUnPacker.unPack();
				if (msg != null)
					out.write(msg);
			} catch (PacketCrcIllegalException e) {
				// CRC校验未通过的情况，暂时未开放
				e.printStackTrace();
			}

		}

		return true;
	}
}
