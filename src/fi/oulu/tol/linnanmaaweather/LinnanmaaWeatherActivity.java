package fi.oulu.tol.linnanmaaweather;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import fi.oulu.tol.linnanmaaweather.LinnanmaaWeatherService.LinnanmaaWeatherBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LinnanmaaWeatherActivity extends Activity {
	private static final String WEATHER_URI = "http://weather.willab.fi/weather.xml";
	static final String TAG = "LinnanmaaWeather Activity";
	private TextView mTemperatureLabel;
	private Button mRefreshButton;
	private LinnanmaaWeatherService mWeatherService;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName name, IBinder binder) {
	        LinnanmaaWeatherBinder weatherBinder = (LinnanmaaWeatherBinder)binder;
	        mWeatherService = weatherBinder.getService();
	        mTemperatureLabel.setText(mWeatherService.getTemperature());
	    }
	    public void onServiceDisconnected(ComponentName name) {
	        mWeatherService = null;
	    }
	};
	
	@Override
	protected void onStart() {
	    super.onStart();
	    Intent intent = new Intent(this, LinnanmaaWeatherService.class);
	    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	    
	@Override
	protected void onStop() {
	    super.onStop();
	    if (mWeatherService != null) {
	        unbindService(mServiceConnection);
	        mWeatherService = null;
	    }
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTemperatureLabel = (TextView) findViewById(R.id.temperature_label);
        mRefreshButton = (Button) findViewById(R.id.refresh_button);
        new RefreshAsyncTask().execute();
        Intent intent = new Intent(this, LinnanmaaWeatherService.class);
        startService(intent);
    }
    public void onRefreshButtonClick(View view) {
    	if (mWeatherService != null)
            mTemperatureLabel.setText(mWeatherService.getTemperature());
        //new RefreshAsyncTask().execute();    	
    }
    
    private String getStringRegion(String string, String before, String after) {
    	try {
    		String tmpString = string;
	    	int start = string.indexOf(before);

	    	if (start == -1)
	    		return null;
	    	start += before.length();
	    	int end = string.indexOf(after, start);
	    	int tmpEnd = end;
	    	tmpEnd -= start;

	    	if (tmpEnd == -1)
	    		return null;
	    	return tmpString.substring(start, end);
    	} catch (IndexOutOfBoundsException exception) {
    		return null;
    	}
    }
        
    private class RefreshAsyncTask extends AsyncTask<Void, Void, String> {
    	@Override
		protected void onPreExecute() {
    		mTemperatureLabel.setText(R.string.temperature_label);
    		mRefreshButton.setEnabled(false);
    	}
    	@Override
		protected String doInBackground(Void... params) {
    		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Linnanmaa Weather (Android Example Application)");
    		HttpUriRequest request = new HttpGet(WEATHER_URI);
			try {
				HttpResponse response = httpClient.execute(request);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
					return "Server error.";
				HttpEntity entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				String temperature = getStringRegion(content, "<tempnow unit=\"C\">", "</tempnow>");
				Log.d(TAG, "############### TEMP :"+ temperature);
				if (temperature == null)
					return "Parse error.";
				return temperature + " °C";
			} catch (IOException e) {
				return "Connection error.";
			}
		}
		@Override
		protected void onPostExecute(String result) {
			mTemperatureLabel.setText(result);
    		mRefreshButton.setEnabled(true);
		}
    	
    }
}