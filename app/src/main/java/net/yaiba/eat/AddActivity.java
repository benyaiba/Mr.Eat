package net.yaiba.eat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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

import static net.yaiba.eat.utils.Custom.getEatTimeFromTime;
import static net.yaiba.eat.utils.Custom.getEatTimeIndex;
import static net.yaiba.eat.utils.Custom.getEatTimeValue;

public class AddActivity extends Activity {
	
	private EatDB EatDB;

	private EditText FoodName;
	private Spinner eatTime;
	private EditText CreateTime;
	private EditText eatWhere;
	private EditText Remark;


	private EditText showDate = null;
	private int mYear;
	private int mMonth;
	private int mDay;


	private CheckBox CheckboxUseNum;
	private CheckBox CheckboxUseWordLowcase;
	private CheckBox CheckboxUseWordUpcase;
	private CheckBox CheckboxUseSymbol;

	private LinearLayout passwordOption;

	private boolean isButton = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		EatDB = new EatDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_add);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		CreateTime = (EditText) findViewById(R.id.create_time);

		eatTime = (Spinner)findViewById(R.id.eat_time);




		eatTime.setSelection(getEatTimeIndex(getEatTimeFromTime()));//早餐

		setDateTime(true);




		Button bn_add = (Button)findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)
			   {  
				   if(add()){
					   Intent mainIntent = new Intent(AddActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });

//		eatTime = (Spinner) findViewById(R.id.eat_time);
//
//		eatTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//									   int pos, long id) {
//
//				String[] languages = getResources().getStringArray(R.array.eat_time);
//				Toast.makeText(AddActivity.this, "languages[pos]是:"+languages[pos]+",,getSelectedItem是:"+eatTime.getSelectedItem(), Toast.LENGTH_SHORT).show();
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// Another interface callback
//			}
//		});


	}

	private void setDateTime(Boolean flag){
		if(flag){
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}
		CreateTime.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
				.append((mDay < 10) ? "0" + mDay : mDay));
	}


	public void getDate(View v) {

		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				AddActivity.this.mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				setDateTime(false);
			}
		}, mYear, mMonth, mDay).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(AddActivity.this,MainActivity.class);
            startActivity(myIntent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

	protected Boolean add(){
		FoodName = (EditText)findViewById(R.id.food_name);
		eatTime = (Spinner)findViewById(R.id.eat_time);
		CreateTime = (EditText)findViewById(R.id.create_time);
		eatWhere = (EditText)findViewById(R.id.eat_where);
		Remark = (EditText)findViewById(R.id.remark);

		
		String foodname = FoodName.getText().toString().replace("\n","");
		String eattime = eatTime.getSelectedItem().toString();
		String createTime = CreateTime.getText().toString().replace("\n","");
		String eatwhere = eatWhere.getText().toString().replace("\n","");
		String remark = Remark.getText().toString();
		
		if (foodname.equals("")){
			Toast.makeText(this, "[吃的啥]没填", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (foodname.length() >100){
			Toast.makeText(this, "[吃的啥]食物过多，不能超过100个文字", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (eattime.equals("")){
			Toast.makeText(this, "[饭点儿]没有选择，请选择", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (eatwhere.length() >100){
			Toast.makeText(this, "[享用地点]文字过多，不能超过100个文字", Toast.LENGTH_SHORT).show();
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
			EatDB.insert(foodname, getEatTimeValue(eattime) , eatwhere, remark, createTime );
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		return true;
	}
}
