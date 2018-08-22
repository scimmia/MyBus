package com.scimmia.mybus.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.bean.FavStation;
import com.scimmia.mybus.utils.bean.LineInfo;
import com.scimmia.mybus.utils.bean.SingleLineInfo;
import com.scimmia.mybus.utils.bean.StationInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.util.LinkedList;

/**
 * Created by lk on 2017/8/12.
 */
public class BusDBManager {
    String dirPath;
    Context mContext;
    SQLiteDatabase db;


    public BusDBManager(Context context) {
        mContext = context;
        dirPath = context.getFilesDir().getParentFile().getPath()+"/databases/bus.db";
        DebugLog.e(dirPath);
    }

    public LinkedList<LineInfo> queryLine(String line){
        LinkedList<LineInfo> result = new LinkedList<>();
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            if (StringUtils.isNotEmpty(line)){
                Cursor c = db.rawQuery("SELECT BusLine.ID as lineID,BusLine.BUSLINENAME as linename,BusLine.ATTACH as attach,a.STATIONNAME as stationa,b.STATIONNAME as stationb \n" +
                        "FROM BusLine INNER JOIN STATION AS a ON (BusLine.STATIONA = a.ID AND BusLine.\"ATTACH\" = a.\"ATTACH\")  INNER JOIN STATION as b ON (BusLine.STATIONB = b.ID AND BusLine.\"ATTACH\" = b.\"ATTACH\")\n" +
                        "WHERE BusLine.BUSLINENAME LIKE '"+line.toUpperCase()+"%' ORDER BY 0+linename", null);
                while (c.moveToNext()) {
                    LineInfo temp = new LineInfo();
                    temp.setLineID(c.getString(c.getColumnIndex("lineID")));
                    temp.setLinename(c.getString(c.getColumnIndex("linename")));
                    temp.setAttach(c.getString(c.getColumnIndex("attach")));
                    temp.setStationa(c.getString(c.getColumnIndex("stationa")));
                    temp.setStationb(c.getString(c.getColumnIndex("stationb")));
                    result.add(temp);
                }
                c.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public SingleLineInfo queryUpDownLine(LineInfo lineInfo){
        SingleLineInfo result = new SingleLineInfo(lineInfo);
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            if (StringUtils.isNotEmpty(lineInfo.getLineID())){
                Cursor c = db.rawQuery("SELECT DIRECTION,ID from BusLineDetail where BUSLINEID = "+lineInfo.getLineID()+" AND \"ATTACH\" = "+lineInfo.getAttach()+"", null);
                while (c.moveToNext()) {
                    String tempDirection = c.getString(c.getColumnIndex("DIRECTION"));
                    String tempID = c.getString(c.getColumnIndex("ID"));
                    if (StringUtils.equals(tempDirection,"上行")){
                        result.setUpID(tempID);
                        Cursor up = db.rawQuery("SELECT STATION.ID,STATION.STATIONNAME,STATION.GPSX2,STATION.GPSY2 FROM STATION INNER JOIN LINESTATION ON (LINESTATION.STATIONID = STATION.ID AND LINESTATION.\"ATTACH\" = STATION.\"ATTACH\")\n" +
                                "where LINESTATION.BUSLINEID = "+lineInfo.getLineID()+" AND LINESTATION.\"ATTACH\" = "+lineInfo.getAttach()+" AND LINESTATION.BUSLINEDETAIL = "+tempID,null);
                        while (up.moveToNext()){
                            StationInfo stationInfo = new StationInfo();
                            stationInfo.setBuslineDetail(tempID);
                            stationInfo.setLineStatus("2");
                            stationInfo.setStationID(up.getString(up.getColumnIndex("ID")));
                            stationInfo.setStationName(up.getString(up.getColumnIndex("STATIONNAME")));
                            stationInfo.setLat(up.getDouble(up.getColumnIndex("GPSY2")));
                            stationInfo.setLon(up.getDouble(up.getColumnIndex("GPSX2")));
                            result.addUpStation(stationInfo);
                        }
                        up.close();
                    }else if (StringUtils.equals(tempDirection,"下行")){
                        result.setDownID(tempID);
                        Cursor down = db.rawQuery("SELECT STATION.ID,STATION.STATIONNAME,STATION.GPSX2,STATION.GPSY2 FROM STATION INNER JOIN LINESTATION ON (LINESTATION.STATIONID = STATION.ID AND LINESTATION.\"ATTACH\" = STATION.\"ATTACH\")\n" +
                                "where LINESTATION.BUSLINEID = "+lineInfo.getLineID()+" AND LINESTATION.\"ATTACH\" = "+lineInfo.getAttach()+" AND LINESTATION.BUSLINEDETAIL = "+tempID,null);
                        while (down.moveToNext()){
                            StationInfo stationInfo = new StationInfo();
                            stationInfo.setBuslineDetail(tempID);
                            stationInfo.setLineStatus("1");
                            stationInfo.setStationID(down.getString(down.getColumnIndex("ID")));
                            stationInfo.setStationName(down.getString(down.getColumnIndex("STATIONNAME")));
                            stationInfo.setLat(down.getDouble(down.getColumnIndex("GPSY2")));
                            stationInfo.setLon(down.getDouble(down.getColumnIndex("GPSX2")));
                            result.addDownStation(stationInfo);
                        }
                        down.close();
                    }
                }
                c.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getLineNamebyIDAttache(String idAttache){
        String result = "";
        if (StringUtils.isNotEmpty(idAttache)){
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT BUSLINENAME FROM BusLine " +
                    "WHERE ID=? and attach=?", StringUtils.split(idAttache,'-'));
            while (c.moveToNext()) {
                result = c.getString(c.getColumnIndex("BUSLINENAME"));
                DebugLog.e(result);
            }
            c.close();
            db.close();
        }
        return result;
    }

    public String getstationNamebyID(String id,String attach){
        String result = "";
        if (StringUtils.isNotEmpty(id)){
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT STATIONNAME FROM STATION " +
                    "WHERE ID='"+id+"' and attach='"+attach+"'",null);
            while (c.moveToNext()) {
                result = c.getString(c.getColumnIndex("STATIONNAME"));
                DebugLog.e(result);
            }
            c.close();
            db.close();
        }
        return result;
    }

    public void close(){
        if (db!= null){
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void replaceFav(FavStation favStation){
        if (favStation != null){
            try {
                String sql = String.format("REPLACE INTO favstation (lineid,attach,stationid,linestatus,tag,linename,stationa,stationb,stationname,lat,lon) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s',%f,%f )",
                        favStation.getLineID(),favStation.getAttach(),favStation.getStationID(),favStation.getLineStatus(),favStation.getTag(),
                        favStation.getLinename(),favStation.getStationa(),favStation.getStationb(),favStation.getStationname(),
                        favStation.getLat(),favStation.getLon());
                SQLiteDatabase temp = new BusDBHelper(mContext).getWritableDatabase();
                temp.execSQL(sql);
                temp.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteFav(FavStation favStation){
        if (favStation != null){
            try {
                String sql = String.format("delete from favstation where lineid ='%s' and attach ='%s' and stationid ='%s' and linestatus ='%s' and tag ='%s'",
                        favStation.getLineID(),favStation.getAttach(),favStation.getStationID(),favStation.getLineStatus(),favStation.getTag());
                SQLiteDatabase temp = new BusDBHelper(mContext).getWritableDatabase();
                temp.execSQL(sql);
                temp.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public LinkedList<FavStation> getAllFav(){
        LinkedList<FavStation> result = new LinkedList<>();
        try {
            SQLiteDatabase db = new BusDBHelper(mContext).getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * from favstation order by tag", null);
            while (c.moveToNext()) {
                result.add(getFav(c));
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public LinkedList<FavStation> getFavByTag(String tag){
        LinkedList<FavStation> result = new LinkedList<>();
        try {
            SQLiteDatabase db = new BusDBHelper(mContext).getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * from favstation where tag='"+tag+"' order by tag", null);
            while (c.moveToNext()) {
                result.add(getFav(c));
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private FavStation getFav(Cursor c){
        FavStation temp = new FavStation();
        temp.setLineID(c.getString(c.getColumnIndex("lineid")));
        temp.setAttach(c.getString(c.getColumnIndex("attach")));
        temp.setStationID(c.getString(c.getColumnIndex("stationid")));
        temp.setLineStatus(c.getString(c.getColumnIndex("linestatus")));
        temp.setTag(c.getString(c.getColumnIndex("tag")));
        temp.setLinename(c.getString(c.getColumnIndex("linename")));
        temp.setStationa(c.getString(c.getColumnIndex("stationa")));
        temp.setStationb(c.getString(c.getColumnIndex("stationb")));
        temp.setStationname(c.getString(c.getColumnIndex("stationname")));
        temp.setLat(c.getDouble(c.getColumnIndex("lat")));
        temp.setLon(c.getDouble(c.getColumnIndex("lon")));
        return temp;
    }

    public int getDBVersion(){
        int result = 0;
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT versioncode FROM version ",null);
            while (c.moveToNext()) {
                result = c.getInt(c.getColumnIndex("versioncode"));
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
