package com.hyphenate.easeui.game.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ue.common.App;
import com.ue.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.hyphenate.easeui.game.db.DBConstants.TABLE_LAYOUT;

/**
 * Created by hawk on 2016/11/28.
 */

public class GameDbManager {
    private static final String TAG="GameDbManager";
    private static GameDbManager instance;
    private GameDbHelper gameDbHelper;

    private GameDbManager() {
        gameDbHelper = GameDbHelper.getInstance(App.getInstance());
    }

    public static GameDbManager getInstance() {
        if (null == instance) {
            instance = new GameDbManager();
        }
        return instance;
    }

    public boolean saveChessRecord(String tableName,String data){
        try {
            SQLiteDatabase db = gameDbHelper.getWritableDatabase();
            if (db.isOpen()) {
                String sql=String.format("insert into %s(%s) values(\"%s\");",tableName,DBConstants.COLUMN_DATA,data);
                db.execSQL(sql);
                return true;
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "saveArmyChessRecord，failed:" + e.getMessage());
        }
        return false;
    }

    public boolean deleteChessRecord(String tableName,int id) {
        try {
            SQLiteDatabase db = gameDbHelper.getWritableDatabase();
            if (db.isOpen()) {
                String sqlStr = String.format("delete from %s where id=%d", tableName, id);

                db.execSQL(sqlStr);
                return true;
            }
        } catch (Exception exp) {
            LogUtil.i(TAG, "deleteArmyChessRecord，failed:" + exp.getMessage());
        }
        return false;
    }

    public boolean clearChessRecord(String tableName) {
        try {
            SQLiteDatabase db = gameDbHelper.getWritableDatabase();
//            LogUtil.i(TAG, "clearArmyChessRecord,db=" + db);
            if (db.isOpen()) {
                String sqlStr = String.format("delete from %s", tableName);

                db.execSQL(sqlStr);
                return true;

            }
        } catch (Exception exp) {
            LogUtil.i(TAG, "clearArmyChessRecord，failed:" + exp.getMessage());
        }
        return false;
    }

    public void closeDB() {
        if(null!= gameDbHelper){
            gameDbHelper.closeDB();
        }
        instance = null;
    }

    public boolean addLayoutItem(String name,String data){
        try {
            SQLiteDatabase db = gameDbHelper.getWritableDatabase();
            if (db.isOpen()) {
                String sql=String.format("insert into %s(%s,%s) values('%s','%s')",
                        TABLE_LAYOUT,DBConstants.COLUMN_NAME,DBConstants.COLUMN_DATA,name,data);
                db.execSQL(sql);
                return true;
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "addLayoutItem，failed:" + e.getMessage());
        }
        return false;
    }

    public boolean updateLayoutItem(int id,String name,String data){
        try {
            SQLiteDatabase db = gameDbHelper.getWritableDatabase();
            if (db.isOpen()) {
                String sql=String.format("update %s set %s='%s',%s='%s' where %s=%d;",
                        DBConstants.TABLE_LAYOUT,DBConstants.COLUMN_NAME,name,DBConstants.COLUMN_DATA,data,DBConstants.COLUMN_ID,id);
                db.execSQL(sql);
                return true;
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "saveArmyChessRecord，failed:" + e.getMessage());
        }
        return false;
    }

    public List<LayoutItem>getLayoutItems(){
        SQLiteDatabase db = gameDbHelper.getReadableDatabase();
        if (db.isOpen()) {
            String sqlStr = String.format("select * from %s;", DBConstants.TABLE_LAYOUT);
            Cursor cursor = db.rawQuery(sqlStr, null);
            List<LayoutItem>layoutItems=new ArrayList<>();
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_ID));
                String name=cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME));
                String data=cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_DATA));
                layoutItems.add(new LayoutItem(id,name,data));
            }
            return layoutItems;
        }
        return null;
    }

    public Object[]getLastTwoRecords(String tableName,DataParser dataParser){
        SQLiteDatabase db = gameDbHelper.getReadableDatabase();
        if (db.isOpen()) {
            String sqlStr = String.format("select * from %s order by %s desc limit 2;", tableName, DBConstants.COLUMN_ID);
            Cursor cursor = db.rawQuery(sqlStr, null);
            Object[]lastTwoRecords=new Object[2];
            int index=0;
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_ID));
                String data = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_DATA));
                try{
                    lastTwoRecords[index]=dataParser.parse(id,data);
                    index++;
                }catch (Exception exp){
                    LogUtil.i(TAG,"parse data exp="+exp.getMessage());
                }
            }
            cursor.close();
            return lastTwoRecords;
        }
        return null;
    }
}