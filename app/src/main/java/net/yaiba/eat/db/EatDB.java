package net.yaiba.eat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class EatDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "eat.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "eat";
    public final static String RECORD_ID = "id";
    public final static String EAT_WHAT = "food_name";//吃了什么
    public final static String EAT_WHEN = "eat_time";//什么时候吃的
    public final static String EAT_WHERE = "eat_where";//在哪吃的
    public final static String REMARK = "remark";//备注
    public final static String CREATE_TIME = "create_time";

    public EatDB(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //登录用
        //eat_time:'breakfast',  'before_lunch',  'lunch',  'before_dinner',  'dinner',  'midnight_snack'
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + RECORD_ID + " INTEGER primary key autoincrement," + EAT_WHAT + " NVARCHAR(100) NOT NULL, "
                + EAT_WHEN +" NVARCHAR(100) NOT NULL, "
                + EAT_WHERE +" NVARCHAR(100) NULL, "
                + REMARK +" TEXT NULL , "
                + CREATE_TIME +" NVARCHAR(100) NOT NULL);";
        db.execSQL(sql);

        //test date
        String sql2 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  'apppai',  'breakfast', '大润发',  '600gx2',	'2016-01-02'		);";
        db.execSQL(sql2);
        String sql3 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  '春卷',  'lunch', '大润发',  '1包',	'2016-05-02'		);";
        db.execSQL(sql3);
        String sql4 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  '巨无霸',  'lunch', '麦当劳',  '套餐',	'2016-05-02'		);";
        db.execSQL(sql4);
        String sql5 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  '珍珍',  'before_dinner', '友宝',  '',	'2017-08-02'		);";
        db.execSQL(sql5);
        String sql6 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  '老口味酸奶',  'before_dinner', '',  '',	'2017-08-02'		);";
        db.execSQL(sql6);
        String sql7 = "INSERT INTO  " + TABLE_NAME
                + " (`id` ,`food_name` ,`eat_time`,`eat_where` ,`remark` ,`create_time`)		VALUES (NULL ,  '炸酱面',  'dinner', '',  '',	'2017-08-01'		);";
        db.execSQL(sql7);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);

        onCreate(db);
    }

    public Cursor getAll(String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
        return cursor;
    }

    public Cursor getOne(long rowId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[] {RECORD_ID, EAT_WHAT, EAT_WHEN, EAT_WHERE, REMARK, CREATE_TIME}, RECORD_ID + "=" + rowId, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getForSearch(String food_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[] {RECORD_ID, EAT_WHAT, EAT_WHEN, EAT_WHERE, REMARK, CREATE_TIME}, EAT_WHAT
                + " LIKE '%" + food_name + "%' or " + EAT_WHERE + " LIKE '%" + food_name + "%' or " + REMARK + " LIKE '%" + food_name + "%' ", null, null, null, null, null);
        return cursor;
    }

    public long insert(String food_name,String eat_time,String eat_where,String remark,String create_time){
        SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(EAT_WHAT, food_name);
        cv.put(EAT_WHEN, eat_time);
        if(eat_where.equals(null)){
            eat_where = "";
        }
        cv.put(CREATE_TIME, create_time);
        cv.put(EAT_WHERE, eat_where);
        if(remark.equals(null)){
            remark = "";
        }
        cv.put(REMARK, remark);

        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = RECORD_ID + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete(TABLE_NAME, where, whereValue);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        //set ai=0
        String where = "name = ?";
        String[] whereValue = { TABLE_NAME };
        ContentValues cv = new ContentValues();
        cv.put("seq", 0);
        db.update("sqlite_sequence", cv, where, whereValue);
    }

    public void update(int id, String food_name,String eat_time,String eat_where,String remark,String create_time){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = RECORD_ID + " = ?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put(EAT_WHAT, food_name);
        cv.put(EAT_WHEN, eat_time);
        if(eat_where.equals(null)){
            eat_where = "";
        }
        cv.put(CREATE_TIME, create_time);
        cv.put(EAT_WHERE, eat_where);
        if(remark.equals(null)){
            remark = "";
        }
        cv.put(REMARK, remark);

        db.update(TABLE_NAME, cv, where, whereValue);
    }


}
