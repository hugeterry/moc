package com.qkmoc.moc.Bean;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/28 16:05
 */
public class MocAPPBean {

    private static MocAPPBean mocBean;

    private int id = 0;//id
    private String Airplane = "on"; //飞行模式
    private String WIFI = "false"; //wifi
    private String BatterySourceLabel = "USB"; //连接类型（USB）
    private int BatteryLevel = 100;//电量
    private String ConnTypeName = "LTE";//网络通信类型
    private String ConnIsConnected = "connected";//是否有网络通信
    private String imei = "00000000000000";//imei

    private String Serial="";

    private boolean MinicapState = true;//minicap状态
    private boolean MinitouchState = true;//minitouch状态

    public static MocAPPBean getInstance() {
        if (mocBean == null) {
            mocBean = new MocAPPBean();
        }
        return mocBean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public boolean isMinitouchState() {
        return MinitouchState;
    }

    public void setMinitouchState(boolean minitouchState) {
        MinitouchState = minitouchState;
    }

    public boolean isMinicapState() {
        return MinicapState;
    }

    public void setMinicapState(boolean minicapState) {
        MinicapState = minicapState;
    }

}
