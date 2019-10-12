package com.heshun.modbus.entity.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * "  [id] INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
 * "  [logotype] INTEGER, \n" +
 * "  [json] TEXT, \n" +
 * "  [url] TEXT, \n" +
 * "  [failtime] TIME, \n" +
 * "  [succeedtime] TIME, \n" +
 * "  [retry] INTEGER, \n" +
 * "  [extra] TEXT);\n"
 */
@DatabaseTable(tableName = "errorLog")
public class AbnormalRecord {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "logotype")
    private int logotype;
    @DatabaseField(columnName = "json")
    private String json;
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "failtime")
    private String occurTimestamp;
    @DatabaseField(columnName = "succeedtime")
    private String succeedTimestamp;
    @DatabaseField(columnName = "retry")
    private int times;
    @DatabaseField(columnName = "extra")
    private String extra;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLogotype() {
        return logotype;
    }

    public void setLogotype(int logotype) {
        this.logotype = logotype;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOccurTimestamp() {
        return occurTimestamp;
    }

    public void setOccurTimestamp(String occurTimestamp) {
        this.occurTimestamp = occurTimestamp;
    }

    public String getSucceedTimestamp() {
        return succeedTimestamp;
    }

    public void setSucceedTimestamp(String succeedTimestamp) {
        this.succeedTimestamp = succeedTimestamp;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "AbnormalRecord{" +
                "id=" + id +
                ", logotype=" + logotype +
                ", json='" + json + '\'' +
                ", url='" + url + '\'' +
                ", occurTimestamp='" + occurTimestamp + '\'' +
                ", succeedTimestamp='" + succeedTimestamp + '\'' +
                ", times=" + times +
                ", extra='" + extra + '\'' +
                '}';
    }
}
