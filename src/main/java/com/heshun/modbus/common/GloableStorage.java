package com.heshun.modbus.common;

import java.util.HashMap;
import java.util.Map;

import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.entity.DefaultDevicePacket;

public class GloableStorage {

	private static GloableStorage instance;

	private Map<Integer, Map<Integer, AbsJsonConvert<?>>> DATABUFFER;

	private GloableStorage() {
		DATABUFFER = new HashMap<Integer, Map<Integer, AbsJsonConvert<?>>>();
	}

	public static GloableStorage getInstance() {
		synchronized (GloableStorage.class) {
			if (null == instance) {
				instance = new GloableStorage();
			}
			return instance;
		}
	}

	public void putDataBuffer(Integer key, Map<Integer, AbsJsonConvert<?>> data) {
		DATABUFFER.put(key, data);
	}

	public Map<Integer, Map<Integer, AbsJsonConvert<?>>> getDataBuffer() {
		return DATABUFFER;
	}

	public void removeDataBuffer() {
		DATABUFFER.clear();
	}

	public synchronized AbsJsonConvert<? extends DefaultDevicePacket> getConvertInStorage(int logotype, int address,
			AbsJsonConvert<?> c) {
		if (DATABUFFER.get(logotype) == null) {
			HashMap<Integer, AbsJsonConvert<?>> map = new HashMap<Integer, AbsJsonConvert<?>>();
			map.put(address, c);
			DATABUFFER.put(logotype, map);
		} else {
			if (DATABUFFER.get(logotype).get(address) == null) {
				DATABUFFER.get(logotype).put(address, c);
			}
		}
		return DATABUFFER.get(logotype).get(address);
	}
}
