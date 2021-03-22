package com.scimmia.mybus.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
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
        dirPath = context.getFilesDir().getParentFile().getPath() + "/databases/bus.db";
        DebugLog.e(dirPath);
    }

    public LinkedList<LineInfo> queryLine(String line) {
        LinkedList<LineInfo> result = new LinkedList<>();
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            if (StringUtils.isNotEmpty(line)) {
                Cursor c = db.rawQuery("SELECT sub_lineid as lineID,linename,upordown as attach,startsite as stationa,endsite as stationb \n" +
                        "FROM ytcx_line " +
                        "WHERE linename LIKE '%" + line.toUpperCase() + "%' ORDER BY 0+linename", null);
                while (c.moveToNext()) {
                    LineInfo temp = new LineInfo();
                    temp.setLineID("" + c.getInt(c.getColumnIndex("lineID")));
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

    public SingleLineInfo queryUpDownLine(LineInfo lineInfo) {
        SingleLineInfo result = new SingleLineInfo(lineInfo);
        try {
            if (StringUtils.isNotEmpty(lineInfo.getLinename())) {
                result.getUpList().addAll(queryLineStations(lineInfo.getLinename(), GlobalData.upStr));
                result.getDownList().addAll(queryLineStations(lineInfo.getLinename(), GlobalData.downStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public LinkedList<StationInfo> queryLineStations(String linename, String upordown) {
        LinkedList<StationInfo> stationList = new LinkedList<>();
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            if (StringUtils.isNotEmpty(linename)) {
                Cursor cursor = db.rawQuery("SELECT ytcx_station.station_name,ytcx_station.jingdu,ytcx_station.weidu ,ytcx_line_station.inorder\n" +
                        "from ytcx_line_station,ytcx_station \n" +
                        "where ytcx_line_station.station_id = ytcx_station.id and ytcx_line_station.sub_lineid=(SELECT sub_lineid from ytcx_line where linename= ? and upordown = ?)\n" +
                        "ORDER BY ytcx_line_station.inorder", new String[]{linename, upordown});
                while (cursor.moveToNext()) {
                    StationInfo stationInfo = new StationInfo();
                    stationInfo.setLineStatus("1");
                    stationInfo.setStationID("" + cursor.getInt(cursor.getColumnIndex("inorder")));
                    stationInfo.setStationName(cursor.getString(cursor.getColumnIndex("station_name")));
                    stationInfo.setLon(cursor.getDouble(cursor.getColumnIndex("jingdu")));
                    stationInfo.setLat(cursor.getDouble(cursor.getColumnIndex("weidu")));
                    stationList.add(stationInfo);
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationList;
    }

    public String getLineNamebyIDAttache(String idAttache) {
        String result = "";
        if (StringUtils.isNotEmpty(idAttache)) {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT BUSLINENAME FROM BusLine " +
                    "WHERE ID=? and attach=?", StringUtils.split(idAttache, '-'));
            while (c.moveToNext()) {
                result = c.getString(c.getColumnIndex("BUSLINENAME"));
                DebugLog.e(result);
            }
            c.close();
            db.close();
        }
        return result;
    }

    public String getstationNamebyID(String id, String attach) {
        String result = "";
        if (StringUtils.isNotEmpty(id)) {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT STATIONNAME FROM STATION " +
                    "WHERE ID='" + id + "' and attach='" + attach + "'", null);
            while (c.moveToNext()) {
                result = c.getString(c.getColumnIndex("STATIONNAME"));
                DebugLog.e(result);
            }
            c.close();
            db.close();
        }
        return result;
    }

    public void close() {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceFav(FavStation favStation) {
        if (favStation != null) {
            try {
                String sql = String.format("REPLACE INTO favstation (lineid,attach,stationid,linestatus,tag,linename,stationa,stationb,stationname,lat,lon) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s',%f,%f )",
                        favStation.getLineID(), favStation.getAttach(), favStation.getStationID(), favStation.getLineStatus(), favStation.getTag(),
                        favStation.getLinename(), favStation.getStationa(), favStation.getStationb(), favStation.getStationname(),
                        favStation.getLat(), favStation.getLon());
                SQLiteDatabase temp = new BusDBHelper(mContext).getWritableDatabase();
                temp.execSQL(sql);
                temp.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFav(FavStation favStation) {
        if (favStation != null) {
            try {
                String sql = String.format("delete from favstation where lineid ='%s' and attach ='%s' and stationid ='%s' and linestatus ='%s' and tag ='%s'",
                        favStation.getLineID(), favStation.getAttach(), favStation.getStationID(), favStation.getLineStatus(), favStation.getTag());
                SQLiteDatabase temp = new BusDBHelper(mContext).getWritableDatabase();
                temp.execSQL(sql);
                temp.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedList<FavStation> getAllFav() {
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

    public LinkedList<FavStation> getFavByTag(String tag) {
        LinkedList<FavStation> result = new LinkedList<>();
        try {
            SQLiteDatabase db = new BusDBHelper(mContext).getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * from favstation where tag='" + tag + "' order by tag", null);
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

    private FavStation getFav(Cursor c) {
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

    public int getDBVersion() {
        int result = 0;
        try {
            db = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("SELECT versioncode FROM version ", null);
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
