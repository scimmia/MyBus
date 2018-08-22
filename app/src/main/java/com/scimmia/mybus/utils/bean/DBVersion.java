package com.scimmia.mybus.utils.bean;

/**
 * Created by lk on 2017/8/21.
 */
public class DBVersion {
    int dbVersion;
    String description;
    double dbSize;

    public int getDbVersion() {
        return dbVersion;
    }

    public double getDbSize() {
        return dbSize;
    }

    @Override
    public String toString() {
        return "DBVersion{" +
                "dbVersion=" + dbVersion +
                ", description='" + description + '\'' +
                ", dbSize=" + dbSize +
                '}';
    }
}
