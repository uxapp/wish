package uxapp.arezoo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Home extends Activity {
	TextView HEADER_top;
	TextView HEADER_sub;
	ImageView HEADER_pp;
	PullToRefreshListView lv;
	SlidingMenu menu;
	Animation fadeIn;
	Animation fadeOut;
	AnimationSet hint;
	AQuery aq;

	public class WishListView extends ArrayAdapter<String> {
		public String[] from;
		public String[] items;
		public String[] hour;
		public String[] pics;
		public String[] urls;
		public String[] ids;

		public WishListView(Context context, int resource,
				int TxtViewResourceId, String[] a, String[] b, String[] c,
				String[] d, String[] e, String[] f) {

			super(context, resource, TxtViewResourceId, a);

			from = a;
			items = b;
			hour = c;
			pics = d;
			urls = e;
			ids = f;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_feed, parent, false);
			TextView txtfrom = (TextView) row.findViewById(R.id.from);
			TextView txt = (TextView) row.findViewById(R.id.txt);
			TextView txthour = (TextView) row.findViewById(R.id.hour);
			final RatingBar rt = (RatingBar) row
					.findViewById(R.id.rtbProductRating);
			final ImageView postimage = (ImageView) row
					.findViewById(R.id.postimage);

			final TextView rh = (TextView) row.findViewById(R.id.ratinghint);
			fadeIn = new AlphaAnimation(0.f, 1.f);
			fadeIn.setDuration(1000);

			fadeOut = new AlphaAnimation(1.f, 0.f);
			fadeOut.setDuration(1000);
			hint = new AnimationSet(false); // change to false
			hint.addAnimation(fadeIn);
			hint.addAnimation(fadeOut);
			Random r = new Random();
			int i1 = 50;
			fadeIn.setDuration(i1);
			//row.startAnimation(fadeIn);

			fadeIn.setDuration(1000);

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					fadeOut.setDuration(200);
					fadeIn.setDuration(200);
					// TODO Auto-generated method stub
					if (!pics[position].equals("")) {
						if (rt.getVisibility() == View.VISIBLE) {
							postimage.startAnimation(fadeOut);
							rt.startAnimation(fadeOut);
							postimage.setVisibility(View.GONE);
							rt.setVisibility(View.GONE);
						} else {
							postimage.setVisibility(View.INVISIBLE);
							rt.setVisibility(View.INVISIBLE);
							postimage.startAnimation(fadeIn);
							rt.startAnimation(fadeIn);
							postimage.setVisibility(View.VISIBLE);
							rt.setVisibility(View.VISIBLE);
						}

					} else {
						if (rt.getVisibility() == View.VISIBLE) {
							rt.startAnimation(fadeOut);
							rt.setVisibility(View.GONE);
						} else {
							rt.setVisibility(View.INVISIBLE);
							rt.startAnimation(fadeIn);
							rt.setVisibility(View.VISIBLE);
						}

					}

				}
			});

			fadeOut.setDuration(1000);
			fadeIn.setDuration(1000);
			try {
				txtfrom.setText(from[position]);
				txt.setText(items[position]);
				txthour.setText(hour[position]);

			} catch (Exception err) {
				err.printStackTrace();
			}
			rt.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar arg0, float arg1,
						boolean arg2) {
					// TODO Auto-generated method stub
					rh.startAnimation(hint);
				}
			});
			return row;

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		fadeIn = new AlphaAnimation(0.f, 1.f);
		fadeIn.setDuration(1000);

		fadeOut = new AlphaAnimation(1.f, 0.f);
		fadeOut.setDuration(1000);

		hint = new AnimationSet(false); // change to false
		hint.addAnimation(fadeIn);
		hint.addAnimation(fadeOut);
		aq = new AQuery(Home.this);
		HEADER_pp = (ImageView) findViewById(R.id.Home_HEADER_profile);
		HEADER_top = (TextView) findViewById(R.id.Home_HEADER_top);
		HEADER_sub = (TextView) findViewById(R.id.Home_HEADER_sub);
		HEADER_pp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.toggle();
			}
		});
		lv = (PullToRefreshListView) findViewById(R.id.lv);

		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// Do work to refresh the list here.
				RefreshList();

			}
		});

		String dirpath = Environment.getExternalStorageDirectory() + "/arezoo/";
		File imgFile = new File(dirpath + "profilepicture.jpg");
		if (imgFile.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			HEADER_pp.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 96,
					96, false));
			HEADER_pp.setScaleType(ScaleType.MATRIX);

		}
		HEADER_top.setText("آرش رسول زاده");
		HEADER_sub.setText("۲۵۰ امتیاز");
		menu = new SlidingMenu(getApplicationContext());

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu);
		RefreshList();
	}

	private void RefreshList() {
		// TODO Auto-generated method stub

		final String dirpath = Environment.getExternalStorageDirectory()
				+ "/arezoo";
		String url = getString(R.string.url) + "w-latest.zip";

		final File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File target = new File(Environment.getExternalStorageDirectory()
				+ "/arezoo/w-latest.lib");

		aq.download(url, target, new AjaxCallback<File>() {
			public void callback(String url, File file, AjaxStatus status) {
				if (file != null) {
					// showResult("File:" + file.length() + ":" + file, status);
					String dirpath = Environment.getExternalStorageDirectory()
							+ "/arezoo/";
					UnpackZip.unpackZip(dirpath, "w-latest.lib");
					String[] a = new String[5];
					String[] b = new String[5];
					String[] c = new String[5];
					String[] d = new String[5];
					String[] e = new String[5];
					String[] f = new String[5];
					for (int i = 1; i <= 5; i++) {
						// String furl = dirpath + "1.json";
						// Toast.makeText(aq.getContext(), furl, 1).show();
						try {
							File yourFile = new File(dirpath, String.valueOf(i)
									+ ".json");
							FileInputStream stream = new FileInputStream(
									yourFile);
							String jString = null;
			                FileChannel fc = stream.getChannel();
			                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			                /* Instead of using default, pass in a decoder. */
			                jString = Charset.defaultCharset().decode(bb).toString();
			                
		                    JSONObject jObject = new JSONObject(jString); 
		                    //Toast.makeText(aq.getContext(), jString,1).show();
		                    
		                    b[i-1]=jObject.optString("msg").toString();
		                    c[i-1]=jObject.optString("time").toString();
		                    d[i-1]=jObject.optString("image").toString();
		                    e[i-1]=jObject.optString("prof.php").toString();
		                    f[i-1]=jObject.optString("prof").toString();
		                    a[i-1]=jObject.optString("by").toString();
			                stream.close(); 
						} catch (Exception err) {
							err.printStackTrace();
						}

					}
					WishListView adapter = new WishListView(Home.this,
							android.R.layout.simple_list_item_1, R.id.txt, a,
							b, c, d, e, f);
					lv.setAdapter(adapter);
					lv.onRefreshComplete();
				} else {
					// showResult("Failed", status);

					finish();
				}
			}
		});

	}
}
