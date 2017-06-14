package net.yaiba.eat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.yaiba.eat.tool.DESUtils;

public class RecordDetailActivity extends Activity {
	private FoodDB FoodDB;
	private Cursor mCursor;
	private TextView SiteName;
	private TextView UserName;
	private EditText PasswordValue;
	private TextView Remark;
	private int RECORD_ID = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		FoodDB = new FoodDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_detail);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);
		setUpViews();
//		Button bn_back = (Button)findViewById(R.id.back);
//		bn_back.setOnClickListener(new OnClickListener(){
//			   public void  onClick(View v)
//			   {
//					   Intent mainIntent = new Intent(RecordDetailActivity.this,MainActivity.class);
//					   startActivity(mainIntent);
//					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//					   setResult(RESULT_OK, mainIntent);
//					   finish();
//			   }
//			  });
	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(RecordDetailActivity.this,MainActivity.class);
			startActivity(mainIntent);
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			setResult(RESULT_OK, mainIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
 	public void setUpViews(){
		FoodDB = new FoodDB(this);
		mCursor = FoodDB.getOne(RECORD_ID);
		 
		SiteName = (TextView)findViewById(R.id.site_name);
		UserName = (TextView)findViewById(R.id.user_name);
		PasswordValue = (EditText)findViewById(R.id.word_value);
		Remark = (TextView)findViewById(R.id.remark);
		
		SiteName.setText(mCursor.getString(1));
		UserName.setText(mCursor.getString(2));
		try {
			PasswordValue.setText(DESUtils.decryptDES(mCursor.getString(3)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Remark.setText(mCursor.getString(4));
	}

}
