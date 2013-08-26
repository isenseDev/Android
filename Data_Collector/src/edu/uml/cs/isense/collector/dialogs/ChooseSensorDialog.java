package edu.uml.cs.isense.collector.dialogs;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.uml.cs.isense.collector.R;
import edu.uml.cs.isense.dfm.SensorCompatibility;
import edu.uml.cs.isense.dfm.SensorCompatibility.SensorTypes;

public class ChooseSensorDialog extends Activity implements OnClickListener {

	SensorCompatibility sensors;
	LinearLayout scrollViewLayout;
	public static LinkedList<String> acceptedFields;
	public static boolean compatible;
	
	private int nullViewCount = 0;
	private boolean isEmpty = false;
	private TextView errorTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosesensor);
		
		String expNum = "", expName = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			expNum  = extras.getString("expnum");
			expName = extras.getString("expname");
		}
		
		TextView tvtext = (TextView) findViewById(R.id.choose_sensor_text);
		tvtext.setText(getResources().getString(R.string.select_fields_for) + expNum + ", " + expName + ".");

		compatible = true;
		Context mContext = this;

		LinkedList<String> fields = Step1Setup.dfm.getOrderList();

		sensors = Step1Setup.sc;

		scrollViewLayout = (LinearLayout) findViewById(R.id.sensorscrollview);

		if (fields.isEmpty()) {
			isEmpty = true;
		} else {
			for (String field : fields) {
				View v = View.inflate(mContext, R.layout.sensorelement, null);

				CheckedTextView ctv = (CheckedTextView) v
						.findViewById(R.id.sensorlabel);
				ctv.setText(field);

				TextView tv = (TextView) v.findViewById(R.id.subsensorlabel);

				if (field.equals(getString(R.string.null_string))
						|| field.equals(getString(R.string.time))
						|| field.equals(getString(R.string.latitude))
						|| field.equals(getString(R.string.longitude))) {
					setCompatibility(tv, ctv);

				} else if (field.equals(getString(R.string.accel_x))
						|| field.equals(getString(R.string.accel_y))
						|| field.equals(getString(R.string.accel_z))
						|| field.equals(getString(R.string.accel_total))) {
					setCompatibility(tv, ctv, SensorTypes.ACCELEROMETER);

				} else if (field.equals(getString(R.string.magnetic_x))
						|| field.equals(getString(R.string.magnetic_y))
						|| field.equals(getString(R.string.magnetic_z))
						|| field.equals(getString(R.string.magnetic_total))) {
					setCompatibility(tv, ctv, SensorTypes.MAGNETIC_FIELD);

				} else if (field.equals(getString(R.string.heading_deg))
						|| field.equals(getString(R.string.heading_rad))) {
					setCompatibility(tv, ctv, SensorTypes.ORIENTATION);

				} else if (field.equals(getString(R.string.temperature_c))
						|| field.equals(getString(R.string.temperature_f))
						|| field.equals(getString(R.string.temperature_k))) {
					setCompatibility(tv, ctv, SensorTypes.AMBIENT_TEMPERATURE);

				} else if (field.equals(getString(R.string.pressure))) {
					setCompatibility(tv, ctv, SensorTypes.PRESSURE);

				} else if (field.equals(getString(R.string.luminous_flux))) {
					setCompatibility(tv, ctv, SensorTypes.LIGHT);

				} else if (field.equals(getString(R.string.altitude))) {
					setCompatibility(tv, ctv, SensorTypes.PRESSURE,
							SensorTypes.AMBIENT_TEMPERATURE);
				}

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

				layoutParams.setMargins(1, 1, 1, 1);
				
				scrollViewLayout.addView(v, layoutParams);
				v.setOnClickListener(this);

				if (field.equals(getString(R.string.null_string))) {
					nullViewCount++;
					v.setVisibility(View.GONE);
				}
			}

		}

		if (nullViewCount == scrollViewLayout.getChildCount()) {
			errorTV = new TextView(mContext);
			errorTV.setBackgroundColor(Color.TRANSPARENT);
			if (isEmpty) errorTV.setText(getString(R.string.invalidProject));
			else {
				errorTV.setText(getString(R.string.noCompatibleFields));
				compatible = false;
			}
			scrollViewLayout.addView(errorTV);
			
			isEmpty = false;
		}

		Button okay = (Button) findViewById(R.id.sensor_ok);
		okay.setOnClickListener(this);

	}

	// Automatically Compatible
	void setCompatibility(TextView tv, CheckedTextView ctv) {
		tv.setTextColor(Color.parseColor("#00AA00"));
		tv.setText(R.string.compatible);
		ctv.setChecked(true);
	}

	// Check compatibility against SensorTypes
	void setCompatibility(TextView tv, CheckedTextView ctv, SensorTypes sensor) {
		if (sensors.isCompatible(sensor)) {
			tv.setTextColor(Color.parseColor("#00AA00"));
			tv.setText(R.string.compatible);
			ctv.setChecked(true);
		} else {
			tv.setTextColor(Color.parseColor("#AA0000"));
			tv.setText(R.string.incompatible);
			ctv.setChecked(false);
			ctv.setCheckMarkDrawable(R.drawable.red_x);
		}
	}

	// Double compatibility check for fields like altitude
	void setCompatibility(TextView tv, CheckedTextView ctv,
			SensorTypes sensor1, SensorTypes sensor2) {
		if (sensors.isCompatible(sensor1) && sensors.isCompatible(sensor2)) {
			tv.setTextColor(Color.parseColor("#00AA00"));
			tv.setText(R.string.compatible);
			ctv.setChecked(true);
		} else {
			tv.setTextColor(Color.parseColor("#AA0000"));
			tv.setText(R.string.incompatible);
			ctv.setChecked(false);
			ctv.setCheckMarkDrawable(R.drawable.red_x);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.sensor_ok:
			setAcceptedFields();
			setResult(RESULT_OK);
			finish();
			break;

		case R.id.check_layout:
			CheckedTextView ctv = (CheckedTextView) v
					.findViewById(R.id.sensorlabel);
			if (ctv.isChecked())
				ctv.setCheckMarkDrawable(R.drawable.red_x);
			else
				ctv.setCheckMarkDrawable(R.drawable.checkmark);
			ctv.toggle();
			break;
		}

	}

	private void setAcceptedFields() {
		acceptedFields = new LinkedList<String>();
		scrollViewLayout.removeView(errorTV);
		for (int i = 0; i < scrollViewLayout.getChildCount(); i++) {
			View v = scrollViewLayout.getChildAt(i);

			CheckedTextView ctv = (CheckedTextView) v
					.findViewById(R.id.sensorlabel);
			if (ctv.isChecked())
				acceptedFields.add(ctv.getText().toString());
			else
				acceptedFields.add(getString(R.string.null_string));
		}
		
		buildPrefsString();

	}
	
	private void buildPrefsString() {
		SharedPreferences mPrefs = getSharedPreferences("PROJID", 0);
		SharedPreferences.Editor mEdit = mPrefs.edit();
		
		StringBuilder sb = new StringBuilder();
		
		for (String s : acceptedFields) {
			sb.append(s).append(",");
		}
		
		String prefString = sb.toString();
		if(prefString.length() == 0)
			return;
		prefString = prefString.substring(0, prefString.length() - 1);
		
		mEdit.putString("accepted_fields", prefString).commit();
		
		Log.d("Jeremy", prefString);
		
	}
	
	@Override
	public void onBackPressed() {
		setAcceptedFields();
		setResult(RESULT_OK);
		finish();
	}

}
