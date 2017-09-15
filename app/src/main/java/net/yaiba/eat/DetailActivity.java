package net.yaiba.eat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import net.yaiba.eat.db.EatDB;

import static net.yaiba.eat.utils.Custom.getEatTimeName;


public class DetailActivity extends Activity {
	private EatDB EatDB;
	private Cursor mCursor;
	private TextView FoodName;
	private TextView EatTime;
	private TextView EatWhere;
	private TextView Remark;
	private TextView CreateTime;
	private int RECORD_ID = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		EatDB = new EatDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_detail);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);
		Log.v("debug","(onCreate)RECORD_ID:"+RECORD_ID);
		setUpViews();

		Button bn_go_edit = (Button)findViewById(R.id.go_edit);
		bn_go_edit.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				//画面迁移到edit画面
				Intent mainIntent = new Intent(DetailActivity.this,EditActivity.class);
				mainIntent.putExtra("INT", RECORD_ID);
				startActivity(mainIntent);
				setResult(RESULT_OK, mainIntent);
				finish();
			}
		});


		Button bn_go_del = (Button)findViewById(R.id.go_del);
		bn_go_del.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				AlertDialog.Builder builder= new AlertDialog.Builder(DetailActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("确认");
				builder.setMessage("确定要删除这条记录吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						delete();
						Intent mainIntent = new Intent(DetailActivity.this,MainActivity.class);
						startActivity(mainIntent);
						setResult(RESULT_OK, mainIntent);
						finish();

					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(DetailActivity.this,MainActivity.class);
			startActivity(mainIntent);
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			setResult(RESULT_OK, mainIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
 	public void setUpViews(){
		Log.v("debug","(setUpViews)RECORD_ID:"+RECORD_ID);
		EatDB = new EatDB(this);
		mCursor = EatDB.getRecordInfo(RECORD_ID);
		 
		FoodName = (TextView)findViewById(R.id.food_name);
		EatTime = (TextView)findViewById(R.id.eat_time);
        EatWhere = (TextView)findViewById(R.id.eat_where);
		Remark = (TextView)findViewById(R.id.remark);
		CreateTime = (TextView)findViewById(R.id.create_time);
		
		FoodName.setText(mCursor.getString(1));
		EatTime.setText(getEatTimeName(mCursor.getString(2)));
        EatWhere.setText(mCursor.getString(3));
		Remark.setText(mCursor.getString(4));
		CreateTime.setText(mCursor.getString(5));
	}

	public void delete(){
		if (RECORD_ID == 0) {
			return;
		}
		EatDB.delete(RECORD_ID);
		mCursor.requery();
		Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
	}

}
