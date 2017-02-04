/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.game.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.hyphenate.easeui.game.db.DBConstants.TABLE_LAYOUT;


public class GameDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lac_game.db";
    private static final int DATABASE_VERSION = 1;
    private static GameDbHelper instance;

    private GameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static GameDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GameDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private String getCreateTableSql(String tableName) {
        return new StringBuffer("CREATE TABLE ")
                .append(tableName)
                .append("(")
                .append(DBConstants.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(DBConstants.COLUMN_DATA).append(" TEXT")
                .append(");")
                .toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSql(DBConstants.TABLE_CHESS));
        db.execSQL(getCreateTableSql(DBConstants.TABLE_CNCHESS));
        db.execSQL(getCreateTableSql(DBConstants.TABLE_MOON));
        db.execSQL(getCreateTableSql(DBConstants.TABLE_REVERSI));
        db.execSQL(getCreateTableSql(DBConstants.TABLE_ARMY_M));
        db.execSQL(getCreateTableSql(DBConstants.TABLE_ARMY_A));

        String layoutSql=new StringBuffer("CREATE TABLE ")
                .append(TABLE_LAYOUT)
                .append("(")
                .append(DBConstants.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(DBConstants.COLUMN_NAME).append(" TEXT,")
                .append(DBConstants.COLUMN_DATA).append(" TEXT")
                .append(");")
                .toString();
        db.execSQL(layoutSql);
        insertLayoutData(db);
    }

    private void insertLayoutData(SQLiteDatabase db) {
        String[] layoutData = new String[]{
                "[0, 2, 4, 4, 6, 3, 3, 0, 1, 10, 8, 7, 2, 1, 0, 5, 9, 9, 9, 10, 6, 1, 2, 11, 5]",
                "[4, 3, 6, 2, 8, 10, 0, 1, 4, 6, 5, 2, 1, 3, 5, 9, 0, 1, 0, 10, 9, 2, 7, 11, 9]",
                "[5, 6, 2, 0, 2, 1, 0, 1, 10, 10, 5, 3, 1, 2, 0, 3, 9, 4, 8, 7, 9, 4, 9, 11, 6]",
                "[0, 1, 5, 1, 0, 4, 4, 8, 1, 6, 10, 0, 2, 3, 6, 3, 5, 2, 10, 9, 9, 7, 2, 11, 9]",
                "[1, 8, 2, 4, 7, 0, 0, 4, 2, 6, 1, 5, 6, 3, 10, 1, 2, 9, 10, 9, 5, 11, 3, 0, 9]"
        };
        for(int i=0;i<5;i++){
            String sql=String.format("insert into %s(%s,%s) values('%s','%s')",
                    TABLE_LAYOUT,DBConstants.COLUMN_NAME,DBConstants.COLUMN_DATA,"布局-"+i,layoutData[i]);
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if (oldVersion < 6) {
//		    db.execSQL("ALTER TABLE " + InviteMessgeDao.TABLE_NAME + " ADD COLUMN " +
//		            InviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT;");
//		}
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}