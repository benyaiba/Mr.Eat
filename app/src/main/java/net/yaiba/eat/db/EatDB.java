package net.yaiba.eat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static net.yaiba.eat.utils.Custom.getBeginDayOfMonth;
import static net.yaiba.eat.utils.Custom.getBeginDayOfSixMonth;
import static net.yaiba.eat.utils.Custom.getBeginDayOfThreeMonth;
import static net.yaiba.eat.utils.Custom.getBeginDayOfThreeYear;
import static net.yaiba.eat.utils.Custom.getBeginDayOfWeek;
import static net.yaiba.eat.utils.Custom.getBeginDayOfYear;
import static net.yaiba.eat.utils.Custom.getDateToString;
import static net.yaiba.eat.utils.Custom.getEatTimeValue;
import static net.yaiba.eat.utils.Custom.getEndDayOfMonth;
import static net.yaiba.eat.utils.Custom.getEndDayOfWeek;
import static net.yaiba.eat.utils.Custom.getEndDayOfYear;


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);

        onCreate(db);
    }

    public Cursor getAll(String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_NAME, null, null, null, null, null, orderBy,"0,90");
        return cursor;
    }

    public Cursor getAllForBakup(String orderBy) {
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

    //
    public Cursor getForSearch(String food_name, String create_time, String eat_time) {
        String where = "";
        String sql_foodname ="";
        if (!food_name.isEmpty()){
            sql_foodname = "("+EAT_WHAT + " LIKE '%" + food_name + "%' or " + EAT_WHERE + " LIKE '%" + food_name + "%' or " + REMARK + " LIKE '%" + food_name + "%' " + ")";
        }

        String createtime =  "";

        String orderby = "create_time desc";

        String start_date = "";
        String end_date = "";

        if(create_time.isEmpty()){
            create_time = "全部";
        }

        switch(create_time) {
            case "本周":
                start_date = getDateToString(getBeginDayOfWeek());
                end_date = getDateToString(getEndDayOfWeek());
                break;
            case "本月":
                start_date = getDateToString(getBeginDayOfMonth());
                end_date = getDateToString(getEndDayOfMonth());
                break;
            case "三个月内":
                start_date = getDateToString(getBeginDayOfThreeMonth());
                end_date = getDateToString(getEndDayOfMonth());
                break;
            case "六个月内":
                start_date = getDateToString(getBeginDayOfSixMonth());
                end_date = getDateToString(getEndDayOfMonth());
                break;
            case "本年":
                start_date = getDateToString(getBeginDayOfYear());
                end_date = getDateToString(getEndDayOfYear());
                break;
            case "三年内":
                start_date = getDateToString(getBeginDayOfThreeYear());
                end_date = getDateToString(getEndDayOfYear());
                break;
            case "全部":
                start_date = "1900-01-01";
                end_date = "9909-12-31";
                break;
            default: break;
        }

        String sql_create_time = "( "+CREATE_TIME+">='" +start_date +"' and " + CREATE_TIME + "<='" + end_date +"' )";

        if(!food_name.isEmpty()){
            where = sql_foodname + " and "+sql_create_time;
        } else{
            where = sql_create_time;
        }



        if(!eat_time.isEmpty()){
            String sql_eat_time = "( "+EAT_WHEN+ " = '"+getEatTimeValue(eat_time)+"' )";
            where =  where +" and "+sql_eat_time;
        }

        Log.v("debug",where);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[] {RECORD_ID, EAT_WHAT, EAT_WHEN, EAT_WHERE, REMARK, CREATE_TIME},
                where , null, null, null, orderby, null);
        return cursor;
    }

    public Cursor getAllCreateTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_NAME, new String[] {CREATE_TIME}, null, null, null, null, CREATE_TIME+" desc",null);
        return cursor;
    }

    public Cursor getAllFoodName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(false,TABLE_NAME, new String[] {EAT_WHAT}, null, null, null, null, null ,null);
        return cursor;
    }
    public Cursor getAllEatWhere() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(false,TABLE_NAME, new String[] {EAT_WHERE}, null, null, null, null, null ,null);
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
