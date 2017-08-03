package net.yaiba.eat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import net.yaiba.eat.db.EatDB;

import static net.yaiba.eat.utils.Custom.getEatTimeName;
//import net.yaiba.eat.data.ListViewData;


public class MainActivity extends Activity implements  AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private  EatDB EatDB;
    private Cursor mCursor;
    private ListView RecordList;

    private int RECORD_ID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews("all",null);

        Button bn_go_add = (Button)findViewById(R.id.go_add);
        bn_go_add.setOnClickListener(new View.OnClickListener(){
            public void  onClick(View v)
            {
                Intent mainIntent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(mainIntent);
                setResult(RESULT_OK, mainIntent);
                finish();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    public void setUpViews(String type, String value){
        EatDB = new EatDB(this);
        if("all".equals(type)){
            mCursor = EatDB.getAll("id asc");
        } else if("search".equals(type)) {
            mCursor = EatDB.getForSearch(value);
        }

        RecordList = (ListView)findViewById(R.id.recordslist);

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

        for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
            int idColumn = mCursor.getColumnIndex("id");
            int foodColumn = mCursor.getColumnIndex("food_name");
            int eatTimeColumn = mCursor.getColumnIndex("eat_time");
            int eatWhereColumn = mCursor.getColumnIndex("eat_where");
            int remarkColumn = mCursor.getColumnIndex("remark");
            int createTimeColumn = mCursor.getColumnIndex("create_time");
            /*String resNo = "["+mCursor.getString(resNoColumn)+"]"; */
            String id = mCursor.getString(idColumn);
            String foodName = mCursor.getString(foodColumn);
            String eatTime = mCursor.getString(eatTimeColumn);
            String eatWhere = mCursor.getString(eatWhereColumn);
            String remark = mCursor.getString(remarkColumn);
            String createTime = mCursor.getString(createTimeColumn);

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            map.put("foodName", foodName);
            map.put("eatTime", getEatTimeName(eatTime));
            map.put("eatWhere", eatWhere);
            map.put("remark", remark);
            map.put("createTime", createTime);
            listItem.add(map);
        }

        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,R.layout.record_items,
            /*new String[] {"res_no", "site_name", "user_name"},
            new int[] {R.id.res_no, R.id.site_name,R.id.user_name} */
                new String[] { "foodName","createTime","eatWhere","eatTime","remark"},
                new int[] {R.id.food_name,R.id.create_time,R.id.eat_where,R.id.eat_time,R.id.remark}
        );

        RecordList.setAdapter(listItemAdapter);
        RecordList.setOnItemClickListener(this);
        RecordList.setOnItemLongClickListener(this);
        RecordList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("操作");
                menu.add(0, 0, 0, "编辑");
                menu.add(0, 1, 0, "删除");
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
        //Toast.makeText(this, "点击了长按菜单里面的第"+item.getItemId()+"个项目", Toast.LENGTH_SHORT).show();
        super.onContextItemSelected(item);
        switch (item.getItemId())
        {
            case 0:
                go_update();
                break;
            case 1:
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("确认");
                builder.setMessage("确定要删除这条记录吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        delete();
                        setUpViews("all",null);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //保存当前一览位置
        //saveListViewPositionAndTop();
        //迁移到详细页面

        Intent mainIntent = new Intent(MainActivity.this,DetailActivity.class);
        mCursor.moveToPosition(position);
        RECORD_ID = mCursor.getInt(0);
        mainIntent.putExtra("INT", RECORD_ID);
        startActivity(mainIntent);
        setResult(RESULT_OK, mainIntent);
        finish();
    }

    @SuppressWarnings("deprecation")
    public void delete(){
        if (RECORD_ID == 0) {
            return;
        }
        EatDB.delete(RECORD_ID);
        mCursor.requery();
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }

    public void go_update(){
        //保存当前位置
        //saveListViewPositionAndTop();
        //画面迁移到edit画面
        Intent mainIntent = new Intent(MainActivity.this,EditActivity.class);
        mainIntent.putExtra("INT", RECORD_ID);
        startActivity(mainIntent);
        setResult(RESULT_OK, mainIntent);
        finish();
    }




    /**
     * 保存当前页签listView的第一个可见的位置和top
     */
//    private void saveListViewPositionAndTop() {
//
//        final ListViewData app = (ListViewData)getApplication();
//
//        app.setFirstVisiblePosition(RecordList.getFirstVisiblePosition());
//        View item = RecordList.getChildAt(0);
//        app.setFirstVisiblePositionTop((item == null) ? 0 : item.getTop());
//    }




    public class RecordListAdapter extends BaseAdapter {
        private Context mContext;
        private Cursor mCursor;
        public RecordListAdapter(Context context,Cursor cursor) {

            mContext = context;
            mCursor = cursor;
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView mTextView = new TextView(mContext);
            mCursor.moveToPosition(position);
            mTextView.setText(mCursor.getString(1) + "___" + mCursor.getString(2)+ "___" + mCursor.getString(3)+ "___" + mCursor.getString(4));
            return mTextView;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mCursor.moveToPosition(position);
        RECORD_ID = mCursor.getInt(0);
        return false;
    }
}
