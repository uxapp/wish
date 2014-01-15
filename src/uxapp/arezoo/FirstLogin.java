package uxapp.arezoo;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class FirstLogin extends Activity {
	AQuery aq;
	ProgressBar pb;
	TextView sts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstlogin);
		final String dirpath=Environment.getExternalStorageDirectory()
				+ "/arezoo";
		aq = new AQuery(this);
		Intent me = getIntent();
		final String path = me.getStringExtra("zippath");
		pb = (ProgressBar) findViewById(R.id.FirstLoginPB);
		sts = (TextView) findViewById(R.id.FirstLoginSTS);
		String url = getString(R.string.url) + path;
		Toast.makeText(aq.getContext(),url, 1).show();
		final File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File target = new File(Environment.getExternalStorageDirectory()
				+ "/arezoo/latest.lib");

		aq.download(url, target, new AjaxCallback<File>() {
			public void callback(String url, File file, AjaxStatus status) {
				if (file != null) {
					// showResult("File:" + file.length() + ":" + file, status);
					String dirpath = Environment.getExternalStorageDirectory() + "/arezoo/";
					UnpackZip.unpackZip(dirpath, "latest.lib");
					Intent home= new Intent(FirstLogin.this,Home.class);
					home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(home);
				} else {
					// showResult("Failed", status);
					
					finish();
				}
			}
		});

	}
}
