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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import net.yaiba.eat.db.EatDB;
import net.yaiba.eat.utils.UpdateTask;

import static net.yaiba.eat.utils.Custom.getEatTimeName;
//import net.yaiba.eat.data.ListViewData;


public class MainActivity extends Activity implements  AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private static final int MENU_ABOUT = 0;
    private static final int MENU_SUPPORT = 1;
    private static final int MENU_WHATUPDATE = 2;
    private static final int MENU_IMPORT_EXPOERT = 3;
    private static final int MENU_CHANGE_LOGIN_PASSWORD = 4;
    private static final int MENU_CHECK_UPDATE = 5;

    private EatDB EatDB;
    private Cursor mCursor;
    private ListView RecordList;
    private EditText SearchInput;

    private UpdateTask updateTask;

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

        SearchInput = (EditText)findViewById(R.id.searchInput);
        SearchInput.clearFocus();
        SearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(SearchInput.getText().toString().trim().length()!=0){
                    try {
                        setUpViews("search",SearchInput.getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setUpViews("all",null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Toast.makeText(LoginActivity.this, "beforeTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(LoginActivity.this, "afterTextChanged", Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void setUpViews(String type, String value){
        EatDB = new EatDB(this);
        if("all".equals(type)){
            mCursor = EatDB.getAll("create_time desc");
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
//            map.put("remark", remark);
            String[] data = createTime.split("-");
            if(data.length==3){
                map.put("createTime", data[1]+"/"+data[2]);
            }else {
                map.put("createTime", "/");
            }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_IMPORT_EXPOERT, 0, this.getString(R.string.menu_inport_export));//备份与恢复
        //menu.add(Menu.NONE, MENU_CHANGE_LOGIN_PASSWORD, 0, this.getString(R.string.menu_change_login_password));//修改登录密码
        menu.add(Menu.NONE, MENU_WHATUPDATE, 0, this.getString(R.string.menu_whatupdate));//更新信息
        menu.add(Menu.NONE, MENU_CHECK_UPDATE, 0, this.getString(R.string.menu_checkupdate));//检查更新
        menu.add(Menu.NONE, MENU_SUPPORT, 0, this.getString(R.string.menu_support));//技术支持
        menu.add(Menu.NONE, MENU_ABOUT, 0, this.getString(R.string.menu_about));//关于Keep
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        String title = "";
        String msg = "";
        //Context mContext = null;

        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case MENU_ABOUT://关于Eat
                title = this.getString(R.string.menu_about);
                msg = this.getString(R.string.about_eat);
                msg = msg + "\n\n";
                msg = msg + "@"+getAppVersion();
                showAboutDialog(title,msg);
                break;
            case MENU_SUPPORT://技术支持
                title = this.getString(R.string.menu_support);
                msg = this.getString(R.string.partners);
                showAboutDialog(title,msg);
                break;
            case MENU_WHATUPDATE://更新信息
                title = this.getString(R.string.menu_whatupdate);
                msg = msg + this.getString(R.string.what_updated);
                msg = msg + "\n\n\n";
                showAboutDialog(title,msg);
                break;
            case MENU_CHECK_UPDATE://检查更新
//                title = this.getString(R.string.menu_checkupdate);
//                msg = "本功能正在升级";//1.增加双服务器检测更新机制\n2.检查更新\n\n以上功能
                updateTask = new UpdateTask(MainActivity.this,true);
                updateTask.update();

//                showAboutDialog(title,msg);
                break;
            case MENU_IMPORT_EXPOERT://备份与恢复
                Intent mainIntent = new Intent(MainActivity.this, DataManagementActivity.class);
                startActivity(mainIntent);
                setResult(RESULT_OK, mainIntent);
                finish();
                break;
//            case MENU_CHANGE_LOGIN_PASSWORD:
//                Intent mainIntent2 = new Intent(MainActivity.this, LoginPEditActivity.class);
//                startActivity(mainIntent2);
//                setResult(RESULT_OK, mainIntent2);
//                finish();
//                break;
        }
        return true;
    }

    public void showAboutDialog(String title,String msg){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", null);
        builder.create().show();
    }

    private String getAppVersion() {
        try {
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
            //int versionCode = this.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }
}
