package com.heshun.modbus.entity.driver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 驱动抽象类，用于描述103报文向json实体转换的必要参数
 */
public class DeviceDriver extends HashMap<String, DriverItem> {

    private String name;

    private Set<CommandBuilder> commands;

    /**
     * 网络传输别名key，与服务端统一
     */
    private String mask = "eqa300";

    public DeviceDriver(String... name) {
        commands = new HashSet<>();
        if (name == null || name.length == 0)
            return;
        this.name = name[0];
        if (name.length >= 2)
            this.mask = name[1];
    }

    public void addCommand(CommandBuilder cw) {
        commands.add(cw);
    }

    public Set<CommandBuilder> getCommands(){
        return commands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void register(DriverItem item) {
        put(item.getKey(), item);
    }

    @Override
    public String toString() {
        return "DeviceDriver{" +
                "name='" + name + '\'' +
                ", mask='" + mask + '\'' +
                '}';
    }
}
