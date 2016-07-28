package com.qkmoc.moc.Bean;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/28 16:05
 */
public class MocBean {
    private static MocBean mocBean;
    private static String Airplane;
    private static String BatterySourceLabel;
    private static String BatteryLevel;
    private static String ConnTypeName;
    private static String ConnIsConnected;
    private static String imei;

    public static MocBean getInstance() {
        if (mocBean == null) {
            mocBean = new MocBean();
        }
        return mocBean;
    }

    public String getAirplane() {
        return Airplane;
    }

    public void setAirplane(String airplane) {
        Airplane = airplane;
    }

    public String getBatterySourceLabel() {
        return BatterySourceLabel;
    }

    public void setBatterySourceLabel(String batterySourceLabel) {
        BatterySourceLabel = batterySourceLabel;
    }

    public String getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        BatteryLevel = batteryLevel;
    }

    public String getConnTypeName() {
        return ConnTypeName;
    }

    public void setConnTypeName(String connTypeName) {
        ConnTypeName = connTypeName;
    }

    public String getConnIsConnected() {
        return ConnIsConnected;
    }

    public void setConnIsConnected(String connIsConnected) {
        ConnIsConnected = connIsConnected;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


}
