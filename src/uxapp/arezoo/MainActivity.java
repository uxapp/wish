package uxapp.arezoo;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import android.Manifest.permission;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	Animation fadeout;
	Animation fadein;
	Button btnlogin;
	FrameLayout framebg;
	AQuery aq;
	EditText no, ps;
	String url;
	LinearLayout login_form;
	FrameLayout pbframe;
	ImageView logo;
	public String fonts = "BZar.ttf";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		aq = new AQuery(MainActivity.this);
		login_form = (LinearLayout) findViewById(R.id.login_main_loginform);
		pbframe = (FrameLayout) findViewById(R.id.login_frame_pb);
		logo = (ImageView) findViewById(R.id.logo);
		no = (EditText) findViewById(R.id.login_form_phone);
		ps = (EditText) findViewById(R.id.login_form_pass);
		Typeface face = Typeface.createFromAsset(getAssets(), "font/" + fonts
				+ "");

		aq.id(findViewById(R.id.login_form_detail)).text(
				PersianReshape.reshape(aq
						.id(findViewById(R.id.login_form_detail)).getText()
						.toString()));

		aq.id(findViewById(R.id.login_form_cp)).text(
				PersianReshape.reshape(aq.id(findViewById(R.id.login_form_cp))
						.getText().toString()));

		aq.id(findViewById(R.id.btn_login)).text(
				PersianReshape.reshape(aq.id(findViewById(R.id.btn_login))
						.getText().toString()));

		aq.id(findViewById(R.id.btn_register)).text(
				PersianReshape.reshape(aq.id(findViewById(R.id.btn_register))
						.getText().toString()));

		no.setHint(PersianReshape.reshape(no.getHint().toString()));
		ps.setHint(PersianReshape.reshape(ps.getHint().toString()));

		url = getString(R.string.url);
		framebg = (FrameLayout) findViewById(R.id.bg_login_frame);
		btnlogin = (Button) findViewById(R.id.btn_login);

		fadeout = new AlphaAnimation(1.f, 0.f);
		fadein = new AlphaAnimation(0.f, 1.f);
		fadeout.setDuration(1200);
		fadein.setDuration(1200);

		framebg.setAnimation(fadein);
		btnlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				asyncJson();
			}
		});

	}

	public void asyncJson() {

		// perform a Google search in just a few lines of code

		String url = getString(R.string.url).toString();
		url += "translate.php?id=1&p=1";
		pbframe.setVisibility(View.VISIBLE);
		login_form.setVisibility(View.GONE);
		logo.setVisibility(View.GONE);
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {

				if (json != null) {

					// successful ajax call, show status code and json content

					pbframe.setVisibility(View.GONE);
					login_form.setVisibility(View.VISIBLE);
					logo.setVisibility(View.VISIBLE);
					try {
						if (json.get("status").equals("OK")) {
							String zippath = json.get("path").toString();
							Intent firstlogin = new Intent(MainActivity.this,FirstLogin.class);
							firstlogin.putExtra("zippath", zippath);
							firstlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(firstlogin);
						} else {
							Toast.makeText(aq.getContext(), json.get("status").toString(),
									Toast.LENGTH_LONG).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(aq.getContext(),e.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				} else {
					pbframe.setVisibility(View.GONE);
					login_form.setVisibility(View.VISIBLE);
					logo.setVisibility(View.VISIBLE);
					// ajax error, show error code
					Toast.makeText(
							aq.getContext(),
							"Error:" + status.getCode() + " - "
									+ status.getError(), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// your code
		} else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// your code

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
