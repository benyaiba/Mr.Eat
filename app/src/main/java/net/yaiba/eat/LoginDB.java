package net.yaiba.eat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LoginDB extends SQLiteOpenHelper{
	
	private final static String DATABASE_NAME = "EAT.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME_LOGIN = "login_table";
	public final static String RECORD_ID = "id";
	public final static String PASSWORD = "password";
	public final static String TYPE = "type";
	
	private final static String TABLE_NAME_FOOD = "food_table";
	public final static String RECORD_ID_FOOD = "id";
	public final static String FOOD_NAME = "food_name";
	public final static String EAT_TIME = "eat_time";
	public final static String CREATE_TIME = "create_time";
	 
	public LoginDB(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//创建table
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//密码用
		String sql = "CREATE TABLE " + TABLE_NAME_FOOD + " (" + RECORD_ID_FOOD
				+ " INTEGER primary key autoincrement, " + FOOD_NAME + " NVARCHAR(100), "+ EAT_TIME +" NVARCHAR(100), "+ CREATE_TIME +" NVARCHAR(100) );";
		db.execSQL(sql);
		//登录用
		String sql2 = "CREATE TABLE " + TABLE_NAME_LOGIN + " (" + RECORD_ID + " INTEGER primary key autoincrement, " + PASSWORD + " NVARCHAR(100), "+ TYPE +" NVARCHAR(100));";
		db.execSQL(sql2);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME_FOOD;
		db.execSQL(sql);
		
		String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME_LOGIN;
		db.execSQL(sql2);
		
		onCreate(db);
	}
	 
	public Cursor getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_LOGIN, null, null, null, null, null, null);
		return cursor;
	}
	
	 public Cursor getOne(long rowId) {
		 SQLiteDatabase db = this.getReadableDatabase();
		 Cursor cursor = db.query(true, TABLE_NAME_LOGIN, new String[] {RECORD_ID, PASSWORD, TYPE}, RECORD_ID + "=" + rowId, null, null, null, null, null);
         if(cursor != null) {
        	 cursor.moveToFirst();
         }
         return cursor;
     }
	 
	 public int isCurrentPassword(String passwordstr) {
		 SQLiteDatabase db = this.getReadableDatabase();
		 Cursor cursor = db.query(true, TABLE_NAME_LOGIN, new String[] {RECORD_ID, PASSWORD, TYPE}, PASSWORD + "='" + passwordstr+"'", null, null, null, null, null);
         if(cursor.getCount()>0) {
        	 cursor.moveToFirst();
        	 if(passwordstr.equals(cursor.getString(1))){
        		//path for 0.1.3
        		 deleteOthers(db, cursor.getInt(0));
        		 return cursor.getInt(0);
        	 }
         }
         return -1;
     }
	
	public void insert(String password){
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(PASSWORD, password);
		cv.put(TYPE, "normal");
		db.insert(TABLE_NAME_LOGIN, null, cv);
	}
	
	public void delete(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RECORD_ID + " = ?";
		String[] whereValue ={ Integer.toString(id) };
		db.delete(TABLE_NAME_LOGIN, where, whereValue);
	}
	
	public void update(int id, String newPassword){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RECORD_ID + " = ?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put(PASSWORD, newPassword);
		cv.put(TYPE, "normal");
		db.update(TABLE_NAME_LOGIN, cv, where, whereValue);
	}
	
	//path
	//for 0.1.3
	public void deleteOthers(SQLiteDatabase db, int id){
		
		Cursor cursor = db.query(TABLE_NAME_LOGIN, null, null, null, null, null, null);
		
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) { 
			
			if(cursor.getInt(0) != id){
				this.delete(id);
			}
		}
	}
	
	

}
