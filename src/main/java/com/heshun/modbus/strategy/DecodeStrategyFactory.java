package com.heshun.modbus.strategy;

import com.heshun.modbus.entity.Device;
import com.heshun.modbus.entity.driver.DeviceDriverLoader;
import com.heshun.modbus.strategy.acrel.aem96.AEM96Strategy;
import com.heshun.modbus.strategy.acrel.c.PZ96LE4CStrategy;
import com.heshun.modbus.strategy.acrel.h.PZ96LE4HStrategy;
import com.heshun.modbus.strategy.chnt.nzl.NZL308Strategy;
import com.heshun.modbus.strategy.common.CommonUnPackStrategy;
import com.heshun.modbus.strategy.enertech.disd683.DISD683Strategy;
import com.heshun.modbus.strategy.enertech.yh396e.YH396EStrategy;
import com.heshun.modbus.strategy.enertech.yht2_tr.YHT2TRStrategy;
import com.heshun.modbus.strategy.eqa300.common.EQA300Strategy;
import com.heshun.modbus.strategy.eqa300.h.EQA300HStrategy;
import com.heshun.modbus.strategy.hy194e.HY194EStrategy;
import com.heshun.modbus.strategy.pd204.e.PD204EStrategy;
import com.heshun.modbus.strategy.tonli.acu.AcuvimStrategy;
import com.heshun.modbus.strategy.tonli.pilot.SPM32Strategy;
import com.heshun.modbus.strategy.tonli.temp.WTDLStrategy;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class DecodeStrategyFactory {

    public static Abs485UnpackStrategy<?, ?> fetchUnPacker(Device d, IoSession session, IoBuffer data) {
        if (null == d) {
            return new DefaultUnPackStrategy(session, data);
        }
        String model = d.model.trim();
        if (DeviceDriverLoader.load(model) != null)
            return new CommonUnPackStrategy(session, data, model);
        switch (model) {
            // 同力无线测温
            case "WTDL":
                return new WTDLStrategy(session, data);
            // 同力爱博精电
            case "Acuvim":
                return new AcuvimStrategy(session, data);
            // 同力派诺科技
            case "spm32":
                return new SPM32Strategy(session, data);
            case "NZL308M":
                return new NZL308Strategy(session, data);
            case "aem96":
                return new AEM96Strategy(session, data);
            case "pd194z-1":
                return new com.heshun.modbus.strategy.pd194z._1.PD194ZStrategy(session, data);
            case "pd194z-2":
                return new com.heshun.modbus.strategy.pd194z._2.PD194ZStrategy(session, data);
            case "yh396e":
                return new YH396EStrategy(session, data);
            // 爱能特
            case "disd683":
                return new DISD683Strategy(session, data);
            case "eqa300":
                return new EQA300Strategy(session, data);
            case "HY194E":
                return new HY194EStrategy(session, data);
            case "eqa300h":
                return new EQA300HStrategy(session, data);
            case "PD204E":
                return new PD204EStrategy(session, data);
            case "PZ96LE4C":
                return new PZ96LE4CStrategy(session, data);
            case "PZ96LE4H":
                return new PZ96LE4HStrategy(session, data);
            case "yht2tr":
                return new YHT2TRStrategy(session, data);
            default:
                return new DefaultUnPackStrategy(session, data);
        }

    }
}
