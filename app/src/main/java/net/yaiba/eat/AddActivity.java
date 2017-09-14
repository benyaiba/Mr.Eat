package net.yaiba.eat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.R.id.list;
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

	private TextView tv_qh;
	private TextView tv_qb;


	private EditText showDate = null;
	private int mYear;
	private int mMonth;
	private int mDay;

	private Cursor mCursor;


	private CheckBox CheckboxUseNum;
	private CheckBox CheckboxUseWordLowcase;
	private CheckBox CheckboxUseWordUpcase;
	private CheckBox CheckboxUseSymbol;

	private LinearLayout passwordOption;

	private boolean isButton = true;


	private final static int DIALOG=1;
	boolean[] foodNameClickFlags=null;//初始复选情况
	String[] foodNameitems=null;
	String foodNameSelectedResults = "";
	
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

        eatWhere = (EditText)findViewById(R.id.eat_where);
		FoodName = (EditText)findViewById(R.id.food_name);

		tv_qh = (TextView)findViewById(R.id.quick_home);
		tv_qh.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				eatWhere.setText("家");
			}
		});

		tv_qb = (TextView)findViewById(R.id.quick_bussiness);
		tv_qb.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				eatWhere.setText("单位");
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


		Button button = (Button) findViewById(R.id.food_name_frequent);
		button.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// 显示对话框
				showDialog(1);
			}
		});


	}


	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
			case 1:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("我吃过以下食品...");

				Map<String, Integer> foodsStringMap = new HashMap<String, Integer>();
				EatDB = new EatDB(AddActivity.this);
				mCursor = EatDB.getAllFoodName();
				for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
					String foodName = mCursor.getString(mCursor.getColumnIndex("food_name"));
					//Log.v("debug",foodName);

					foodName = foodName.replaceAll(" ","");//替换空格
					foodName = foodName.replaceAll("　","");//替换空格
					foodName = foodName.replaceAll("，",",");//全角逗号替换成半角逗号
					foodName = foodName.replaceAll("[',']+", ",");//将一个或多个半角逗号变成一个半角逗号
					String[] foodNamesTmp = foodName.split(",");

					for (int i = 0; i < foodNamesTmp.length; i++) {
						foodName = foodNamesTmp[i];
						if(foodsStringMap.containsKey(foodName)){
							foodsStringMap.put(foodName, foodsStringMap.get(foodName)+1);
						} else {
							foodsStringMap.put(foodName, 1);
						}
					}
				}

				List<Map.Entry<String, Integer>> foodInfosMap =	new ArrayList<Map.Entry<String, Integer>>(foodsStringMap.entrySet());

				//对map排序
				Collections.sort(foodInfosMap, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
						//升序，按照名称升序排序
						//return (o1.getKey()).compareTo(o2.getKey());
						//降序，按照使用次数降序排序
						//return (o2.getValue()).compareTo(o1.getValue());
                        String name1=o1.getKey();
                        String name2=o2.getKey();
                        Collator instance = Collator.getInstance(Locale.CHINA);
                        return instance.compare(name1, name2);


					}
				});

				//每次点击时清空选择项
				foodNameClickFlags=null;

				if(!foodInfosMap.equals(null)){
					foodNameitems = new String [foodInfosMap.size()];
					foodNameClickFlags = new boolean [foodInfosMap.size()];
				}

				//Log.v("debug","=====map info===sort===");
				int foodNameIndex = 0;
				for(Map.Entry<String,Integer> mapping:foodInfosMap){
					//Log.v("debug",mapping.getKey()+":"+mapping.getValue().toString());
					if(mapping.getValue()>=5){
						foodNameitems[foodNameIndex] = mapping.getKey()+" ("+mapping.getValue()+"次)";
					} else {
						foodNameitems[foodNameIndex] = mapping.getKey();
					}

					foodNameClickFlags[foodNameIndex] = false;//设置默认选中状态
					//Log.v("debug","view->"+mapping.getKey()+":"+mapping.getValue().toString());
					foodNameIndex++;
				}
				//Log.v("debug","=====items===");
				//Log.v("debug",foodNameitems.length+"");
				builder.setMultiChoiceItems(foodNameitems, foodNameClickFlags, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						foodNameClickFlags[which]=isChecked;
						foodNameSelectedResults = "";
						for (int i = 0; i < foodNameClickFlags.length; i++) {
							if(foodNameClickFlags[i])
							{
								String n = foodNameitems[i];
								String [] m = n.split(" ");
								foodNameSelectedResults=foodNameSelectedResults + m[0]+",";
							}
						}
						//去掉结尾的逗号
						if(!foodNameSelectedResults.isEmpty()){
							foodNameSelectedResults = foodNameSelectedResults.substring(0,foodNameSelectedResults.length()-1);
						}

                        //Log.v("debug","我点了！which:"+which+",name:"+foodNameitems[which]);
					}
				});
				builder.setPositiveButton("就选这些（点击后，之前填写的将被清空）", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fn = FoodName.getText().toString();
						//选择的食物名称赋值到前台文本框中
						FoodName.setText(foodNameSelectedResults);
					}
				});
				dialog = builder.create();
				break;

			default:
				break;
		}

		return dialog;
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
