package net.yaiba.eat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import net.yaiba.eat.db.EatDB;

import static net.yaiba.eat.utils.Custom.getEatTimeValue;

public class AddActivity extends Activity {
	
	private EatDB EatDB;

	private EditText FoodName;
	private Spinner eatTime;
	private EditText eatWhere;
	private EditText Remark;
	private EditText PasswordLength;

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
		eatWhere = (EditText)findViewById(R.id.eat_where);
		Remark = (EditText)findViewById(R.id.remark);
		
		String foodname = FoodName.getText().toString().replace("\n","");
		String eattime = eatTime.getSelectedItem().toString();
		String eatwhere = eatWhere.getText().toString().replace("\n","");
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
		if (remark.length() >200){
			Toast.makeText(this, "[备注]长度不能超过200个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		try {
			EatDB.insert(foodname, getEatTimeValue(eattime), eatwhere, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		return true;
	}
}
