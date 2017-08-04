package net.yaiba.eat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.yaiba.eat.db.EatDB;

import java.util.Calendar;

import static net.yaiba.eat.utils.Custom.getEatTimeIndex;
import static net.yaiba.eat.utils.Custom.getEatTimeValue;

public class EditActivity extends Activity {
	private EatDB EatDB;
	private Cursor mCursor;
	private EditText FoodName;
	private Spinner EatTime;
	private EditText CreateTime;
	private EditText EatWhere;
	private EditText Remark;

	private EditText showDate = null;
	private int mYear;
	private int mMonth;
	private int mDay;

	private int RECORD_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		EatDB = new EatDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);

		setUpViews();

		Button bn_add = (Button) findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (update()) {
					Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
					startActivity(mainIntent);
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					setResult(RESULT_OK, mainIntent);
					finish();
				}
			}
		});
	}

	public void getDate(View v) {

		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;

				CreateTime.
						setText(new StringBuilder().append(mYear).append("-")
						.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
						.append((mDay < 10) ? "0" + mDay : mDay));
			}
		}, mYear, mMonth-1, mDay).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(EditActivity.this,MainActivity.class);
			   startActivity(mainIntent);
			   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			   setResult(RESULT_OK, mainIntent);  
			   finish(); 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@SuppressWarnings("deprecation")
	public Boolean update(){
	
		String foodname = FoodName.getText().toString().replace("\n","");
		String eattime = EatTime.getSelectedItem().toString();
		String eatwhere = EatWhere.getText().toString().replace("\n","");
		String createTime = CreateTime.getText().toString().replace("\n","");
		String remark = Remark.getText().toString();

		if (foodname.equals("")){
			Toast.makeText(this, "[名称]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (foodname.length() >30){
			Toast.makeText(this, "[名称]长度不能超过30个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (eatwhere.length() >30){
			Toast.makeText(this, "[密码]长度不能超过30个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (createTime.length() > 14){
			Toast.makeText(this, "[日期]长度不能超过14个文字，请检查格式是否正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (createTime.length() < 10){
			Toast.makeText(this, "[日期]长度不能小于10个文字，请检查格式是否正确", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (remark.length() >200){
			Toast.makeText(this, "[备注]长度不能超过200个文字", Toast.LENGTH_SHORT).show();
			return false;
		}


		try {
			EatDB.update(RECORD_ID, foodname, getEatTimeValue(eattime),createTime, eatwhere, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mCursor.requery();
		Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	public void setUpViews(){
		EatDB = new EatDB(this);
		mCursor = EatDB.getOne(RECORD_ID);

		FoodName = (EditText)findViewById(R.id.food_name);
		EatTime = (Spinner)findViewById(R.id.eat_time);
		CreateTime = (EditText)findViewById(R.id.create_time);
		EatWhere = (EditText)findViewById(R.id.eat_where);
		Remark = (EditText)findViewById(R.id.remark);

		FoodName.setText(mCursor.getString(1));
		EatTime.setSelection(getEatTimeIndex(mCursor.getString(2)),true);
		CreateTime.setText(mCursor.getString(5));
		EatWhere.setText(mCursor.getString(3));
		Remark.setText(mCursor.getString(4));


		String[] data = mCursor.getString(5).split("-");
		mYear = Integer.parseInt(data[0]);
		mMonth = Integer.parseInt(data[1]);
		mDay = Integer.parseInt(data[2]);
	}
}
