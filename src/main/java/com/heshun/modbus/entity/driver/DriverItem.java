package com.heshun.modbus.entity.driver;

/**
 * 仪表虚拟驱动的item条目描述类
 */
public class DriverItem {

    private String group;
    /**
     * 最终输出json的key
     */
    private String tag;
    /**
     * 数据报中对应的index
     */
    private int index;
    /**
     * 数据报中字节长度
     */
    private int offset;
    /**
     * 数据类型
     */
    private DataType dataType;
    /**
     * 是否高低位翻转
     */
    private boolean isReverse;
    /**
     * 倍率转换比率
     */
    private int ratio;

    /**
     * //type[1-8],offset[1-4],reverse[boolean],ratio[int],index[1-],,tag[String],group[String]
     * 1,1,1,1,0,dpt,3-22
     *
     * @param configLine
     */
    public DriverItem(String configLine) {

        String[] items = configLine.split(",");
        if (items.length != 7)
            throw new IllegalStateException();
        try {
            this.dataType = DataType.lookup(Integer.valueOf(items[0].trim()));
            this.offset = Integer.valueOf(items[1].trim());
            this.isReverse = items[2].equals("1");
            this.ratio = Integer.valueOf(items[3].trim());
            this.tag = items[4];
            this.group = items[5];
            this.index = Integer.valueOf(items[6].trim());
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }


    public String getKey() {
        return String.format("%s,%s", group, index);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public enum DataType {

        BYT(0), SHT(1), INT(2), SHT_UNS(3), INT_UNS(4), LNG(5), FLT(6), DBLE(7), STR(8);

        private int code;

        DataType(int i) {
            this.code = i;
        }

        public static DataType lookup(int code) {
            for (DataType item : values())
                if (item.code == code)
                    return item;
            return SHT;
        }
    }

}
