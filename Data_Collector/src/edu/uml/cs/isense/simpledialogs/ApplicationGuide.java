package edu.uml.cs.isense.simpledialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import edu.uml.cs.isense.collector.R;

public class ApplicationGuide extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_guide);
		
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		final Button back = (Button) findViewById(R.id.application_guide_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}	
		});
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}