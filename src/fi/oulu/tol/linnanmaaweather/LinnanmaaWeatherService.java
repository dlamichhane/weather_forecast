package fi.oulu.tol.linnanmaaweather;

import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LinnanmaaWeatherService extends Service {
	private final LinnanmaaWeatherBinder binder = new LinnanmaaWeatherBinder();
	private final TimerTask timerTask = new TimerTask(){

		@Override
		public void run() {
			
		}
		
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	public String getTemperature() {
	    return "Unknown";
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return START_STICKY;
	}
	
	public class LinnanmaaWeatherBinder extends Binder {
	    LinnanmaaWeatherService getService() {
	        return LinnanmaaWeatherService.this;
	    }
	}
	

}
