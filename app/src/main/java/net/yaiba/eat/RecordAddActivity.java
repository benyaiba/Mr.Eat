package net.yaiba.eat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.yaiba.eat.tool.CommonUtils;
import net.yaiba.eat.tool.DESUtils;

public class RecordAddActivity extends Activity {
	
	private FoodDB FoodDB;

	private EditText EatTime;
	private EditText FoodName;
	private EditText PasswordValue;
	private EditText Remark;
	private EditText PasswordLength;

	private CheckBox CheckboxUseNum;
	private CheckBox CheckboxUseWordLowcase;
	private CheckBox CheckboxUseWordUpcase;
	private CheckBox CheckboxUseSymbol;

	private LinearLayout passwordOption;

	private boolean isButton = true;


	TextView tv_date = null;
	TextView tv_time = null;
	int year = CommonUtils.getYear();
	int month = CommonUtils.getMonthMM();
	int day = CommonUtils.getDaydd();
	int houre = CommonUtils.getHoureHH();
	int minute = CommonUtils.getMinutemm();


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		FoodDB = new FoodDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_record);

		initView();
		
		Button bn_add = (Button)findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)     
			   {  
				   if(add()){
					   Intent mainIntent = new Intent(RecordAddActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });
		
//		Button bn_back = (Button)findViewById(R.id.back);
//		bn_back.setOnClickListener(new OnClickListener(){
//			   public void  onClick(View v)
//			   {
//					   Intent mainIntent = new Intent(AddRecordActivity.this,MainActivity.class);
//					   startActivity(mainIntent);
//					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//					   setResult(RESULT_OK, mainIntent);
//					   finish();
//			   }
//			  });



		//设置密码配置区域展开和隐藏
		Button bn_random_password = (Button)findViewById(R.id.random_password);
		passwordOption = (LinearLayout) findViewById(R.id.password_option);
		bn_random_password.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				if(isButton){
					passwordOption.setVisibility(View.VISIBLE);
					isButton = false;
				}else {
					passwordOption.setVisibility(View.GONE);
					isButton = true;
				}
			}
		});


		//按照选定的值生成随机密码，并填写到密码栏中
		Button bn_create_random_password = (Button)findViewById(R.id.create_random_password);
		bn_create_random_password.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				String setPassword = "";
				int PasswordLengthString = 8;
				Boolean isNumChecked = false;
				Boolean isUseWordLowcaseChecked = false;
				Boolean isUseWordUpcaseChecked = false;
				Boolean isUseSymbolChecked = false;

				CheckboxUseNum=(CheckBox)findViewById(R.id.checkbox_use_num);
				if(CheckboxUseNum.isChecked()){
					isNumChecked = true;
				}

				CheckboxUseWordLowcase=(CheckBox)findViewById(R.id.checkbox_use_word_lowcase);
				if(CheckboxUseWordLowcase.isChecked()){
					isUseWordLowcaseChecked =  true;
				}

				CheckboxUseWordUpcase=(CheckBox)findViewById(R.id.checkbox_use_word_upcase);
				if(CheckboxUseWordUpcase.isChecked()){
					isUseWordUpcaseChecked = true;
				}

				CheckboxUseSymbol=(CheckBox)findViewById(R.id.checkbox_use_symbol);
				if(CheckboxUseSymbol.isChecked()){
					isUseSymbolChecked = true;
				}

				PasswordLength=(EditText)findViewById(R.id.txt_password_length);

				if(PasswordLength.getText().toString().isEmpty() || Integer.parseInt(PasswordLength.getText().toString()) <=4){
					PasswordLengthString = 4;
				} else if(Integer.parseInt(PasswordLength.getText().toString())>16) {
					PasswordLengthString = 16;
				} else {
					PasswordLengthString = Integer.parseInt(PasswordLength.getText().toString());
				}


				String PasswordContainStr = ((EditText)findViewById(R.id.txt_contain)).getText().toString();
				String PasswordNotContainStr = ((EditText)findViewById(R.id.txt_not_contain)).getText().toString();
				String PasswordIndexStr = ((EditText)findViewById(R.id.txt_index)).getText().toString();

				//PasswordContain=(EditText)findViewById(R.id.txt_contain);
				//PasswordNotContain=(EditText)findViewById(R.id.txt_not_contain);
				//PasswordIndex=(EditText)findViewById(R.id.txt_index);


				PassWordCreate createNewPassword = new PassWordCreate();
				setPassword = createNewPassword.getRandomString(
						PasswordLengthString,
						isNumChecked,
						isUseWordLowcaseChecked,
						isUseWordUpcaseChecked,
						isUseSymbolChecked,
						PasswordContainStr,
						PasswordNotContainStr,
						PasswordIndexStr);

				PasswordValue = (EditText) findViewById(R.id.word_value);
				PasswordValue.setText(setPassword);

			}
		});


	}

	private void initView() {
		EatTime = (EditText) findViewById(R.id.eat_time);
		FoodName = (EditText) findViewById(R.id.food_name);

		showDateS();
	}

	// 点击事件,湖区日期
	public void getDate(View v) {

		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				RecordAddActivity.this.year = year;
				month = monthOfYear;
				day = dayOfMonth;

			}
		}, 2016, 10, 8).show();
		showDateS();
	}

	// 点击事件,湖区日期
	public void getTime(View v) {
		new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				houre = hourOfDay;
				RecordAddActivity.this.minute = minute;
			}
		}, 15, 20, true).show();
		showDateS();
	}
	private void showDateS() {
		EatTime.setText(year + "-" + month + "-" + day + " "+ houre + ":" + minute);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(RecordAddActivity.this,MainActivity.class);
            startActivity(myIntent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	

	protected Boolean add(){
		FoodName = (EditText)findViewById(R.id.food_name);
		EatTime = (EditText)findViewById(R.id.eat_time);
		PasswordValue = (EditText)findViewById(R.id.word_value);
		Remark = (EditText)findViewById(R.id.remark);
		
		String foodname = FoodName.getText().toString().replace("\n","");
		String eattime = EatTime.getText().toString().replace("\n","");
		
		if (foodname.equals("")){
			Toast.makeText(this, "[名称]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (foodname.length() >30){
			Toast.makeText(this, "[名称]长度不能超过30个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (eattime.equals("")){
			Toast.makeText(this, "[登陆名]不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (eattime.length() >30){
			Toast.makeText(this, "[登陆名]长度不能超过30个文字", Toast.LENGTH_SHORT).show();
			return false;
		}




		
		try {
			FoodDB.insert(foodname, eattime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		return true;
	}

}
