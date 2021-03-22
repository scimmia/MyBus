package com.scimmia.mybus.utils.bean;

public class BusPostionNew {
    private String busno;

    private String linename;

    private String upordown;

    private String inorder;

    private double weidu;

    private double jingdu;

    private String nowstate;

    private String speed;

    private String gpstime;

    public void setBusno(String busno){
        this.busno = busno;
    }
    public String getBusno(){
        return this.busno;
    }
    public void setLinename(String linename){
        this.linename = linename;
    }
    public String getLinename(){
        return this.linename;
    }
    public void setUpordown(String upordown){
        this.upordown = upordown;
    }
    public String getUpordown(){
        return this.upordown;
    }
    public void setInorder(String inorder){
        this.inorder = inorder;
    }
    public String getInorder(){
        return this.inorder;
    }
    public void setWeidu(double weidu){
        this.weidu = weidu;
    }
    public double getWeidu(){
        return this.weidu;
    }
    public void setJingdu(double jingdu){
        this.jingdu = jingdu;
    }
    public double getJingdu(){
        return this.jingdu;
    }
    public void setNowstate(String nowstate){
        this.nowstate = nowstate;
    }
    public String getNowstate(){
        return this.nowstate;
    }
    public void setSpeed(String speed){
        this.speed = speed;
    }
    public String getSpeed(){
        return this.speed;
    }
    public void setGpstime(String gpstime){
        this.gpstime = gpstime;
    }
    public String getGpstime(){
        return this.gpstime;
    }
}
