package com.scimmia.mybus.utils.bean;

public class DBInfo {
    private Data data;

    private String info;

    private int status;

    public void setData(Data data){
        this.data = data;
    }
    public Data getData(){
        return this.data;
    }
    public void setInfo(String info){
        this.info = info;
    }
    public String getInfo(){
        return this.info;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public String getDbPath(){
        return this.data.dbPath;
    }
    public String getVersion(){
        return this.data.version;
    }
    static class Data {
        private String dbPath;

        private String version;

        public void setDbPath(String dbPath){
            this.dbPath = dbPath;
        }
        public String getDbPath(){
            return this.dbPath;
        }
        public void setVersion(String version){
            this.version = version;
        }
        public String getVersion(){
            return this.version;
        }

    }
}
