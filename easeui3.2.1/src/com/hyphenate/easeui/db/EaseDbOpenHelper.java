/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyphenate.easeui.DemoHelper;

public class EaseDbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static EaseDbOpenHelper instance;

	private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
			+ EaseUserDao.TABLE_NAME + " ("
			+ EaseUserDao.COLUMN_NAME_NICK + " TEXT, "
			+ EaseUserDao.COLUMN_NAME_AVATAR + " TEXT, "
			+ EaseUserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

	private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + EaseUserDao.PREF_TABLE_NAME + " ("
            + EaseUserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + EaseUserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

	private EaseDbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}
	
	public static EaseDbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new EaseDbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	private static String getUserDatabaseName() {
        return  DemoHelper.getInstance().getCurrentUserName() + "_lac.db";
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USERNAME_TABLE_CREATE);
		db.execSQL(CREATE_PREF_TABLE);
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
