package com.heshun.modbus.entity.driver;

import com.heshun.modbus.util.Utils;

public class CommandBuilder {
    private int code;
    private int start;
    private int offset;

    public CommandBuilder(String configLine) {
//        @3,10,19
        String[] split = configLine.split(",");

        this.code = Integer.parseInt(split[0]);
        this.start = Integer.parseInt(split[1]);
        this.offset = Integer.parseInt(split[2]);
    }

    /**
     * generate modbus request package
     *
     * @param cpu
     * @return
     */
    public byte[] generate(int cpu) {
        return Utils.fetchRequest(cpu, code, start, offset);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


}
