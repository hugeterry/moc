package com.qkmoc.moc.Bean;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/28 16:05
 */
public class MocBean {

    private static MocBean mocBean;
    private String Airplane = ""; //飞行模式
    private String WIFI = ""; //wifi
    private String BatterySourceLabel = ""; //连接类型（USB）
    private int BatteryLevel = 100;//电量
    private String ConnTypeName = "";//网络通信类型
    private String ConnIsConnected = "";//是否有网络通信
    private String imei = "";//imei

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

    public String getWIFI() {
        return WIFI;
    }

    public void setWIFI(String WIFI) {
        this.WIFI = WIFI;
    }

    public String getBatterySourceLabel() {
        return BatterySourceLabel;
    }

    public void setBatterySourceLabel(String batterySourceLabel) {
        BatterySourceLabel = batterySourceLabel;
    }

    public int getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
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
