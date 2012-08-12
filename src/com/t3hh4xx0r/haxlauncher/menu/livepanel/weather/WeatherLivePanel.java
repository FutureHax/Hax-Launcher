package com.t3hh4xx0r.haxlauncher.menu.livepanel.weather;

import java.io.IOException;
import java.util.Date;

import org.w3c.dom.Document;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.t3hh4xx0r.haxlauncher.StyledTextFoo;
import android.widget.Toast;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.preferences.PreferencesProvider;

public class WeatherLivePanel extends RelativeLayout {
	
    private StyledTextFoo mWeatherCity, mWeatherCondition, mWeatherLowHigh, mWeatherTemp, mUpdateTime;
    private ImageView mWeatherImage;
    private View mWeatherPanel;
    
	public WeatherLivePanel(Context context) {
		super(context);
		   LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   mWeatherPanel = layoutInflater.inflate(R.layout.weather_lp, this);
		   		   
	        mWeatherPanel = (RelativeLayout) findViewById(R.id.weather_panel);
	        mWeatherCity = (StyledTextFoo) findViewById(R.id.weather_city);
	        mWeatherCondition = (StyledTextFoo) findViewById(R.id.weather_condition);
	        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
	        mWeatherTemp = (StyledTextFoo) findViewById(R.id.weather_temp);
	        mWeatherLowHigh = (StyledTextFoo) findViewById(R.id.weather_low_high);
	        mUpdateTime = (StyledTextFoo) findViewById(R.id.update_time);

	        refreshWeather(false);

	}
	
//    @Override
//    protected void onAttachedToWindow() {
//    	refreshWeather(false);
//    }
    
    /*
     * CyanogenMod Lock screen Weather related functionality
     */
    private static final String URL_YAHOO_API_WEATHER = "http://weather.yahooapis.com/forecastrss?w=%s&u=";
    private static WeatherInfo mWeatherInfo = new WeatherInfo();
    public final static int QUERY_WEATHER = 0;
    private static final int UPDATE_WEATHER = 1;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case QUERY_WEATHER:
                Thread queryWeather = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocationManager locationManager = (LocationManager) getContext().
                                getSystemService(Context.LOCATION_SERVICE);
                        //change me
                        boolean useCustomLoc = false;
                        String customLoc = null;
                        String woeid = null;

                        // custom location
                        if (customLoc != null && useCustomLoc) {
                            try {
                                woeid = YahooPlaceFinder.GeoCode(getContext().getApplicationContext(), customLoc);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        // network location
                        } else {
                            Criteria crit = new Criteria();
                            crit.setAccuracy(Criteria.ACCURACY_COARSE);
                            String bestProvider = locationManager.getBestProvider(crit, true);
                            Location loc = null;
                            if (bestProvider != null) {
                                loc = locationManager.getLastKnownLocation(bestProvider);
                            } else {
                                loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                            }
                            try {
                                woeid = YahooPlaceFinder.reverseGeoCode(getContext(), loc.getLatitude(),
                                        loc.getLongitude());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what = UPDATE_WEATHER;
                        msg.obj = woeid;
                        mHandler.sendMessage(msg);
                    }
                });
                queryWeather.setPriority(Thread.MIN_PRIORITY);
                queryWeather.start();
                break;
            case UPDATE_WEATHER:
               String woeid = (String) msg.obj;
                if (woeid != null) {
                    WeatherInfo w = null;
                    try {
                        w = parseXml(getDocument(woeid));
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                    if (w == null) {
                        setNoWeatherData();
                    } else {
                        setWeatherData(w);
                        mWeatherInfo = w;
                    }
                } else {
                    if (mWeatherInfo.temp.equals(WeatherInfo.NODATA)) {
                        setNoWeatherData();
                    } else {
                        setWeatherData(mWeatherInfo);
                    }
                }
                break;
            }
        }
    };

    /**
     * Reload the weather forecast
     */
    public void refreshWeather(boolean force) {
        boolean showWeather = PreferencesProvider.Interface.LivePanel.getEnableWeather(getContext());
        if (showWeather) {
            final long interval = PreferencesProvider.Interface.LivePanel.getWeatherInterval(getContext()); // Default to hourly
            if (((System.currentTimeMillis() - mWeatherInfo.last_sync) / 60000) >= interval) {
                mHandler.sendEmptyMessage(QUERY_WEATHER);
            } else {
                setWeatherData(mWeatherInfo);
            }
        } 
    }

    /**
     * Display the weather information
     * @param w
     */
    private void setWeatherData(WeatherInfo w) {
        final Resources res = getContext().getResources();
        if (mWeatherPanel != null) {
            if (mWeatherCity != null) {
                mWeatherCity.setText(w.city);
                mWeatherCity.setVisibility(View.VISIBLE);
            }
            if (mWeatherCondition != null) {
                mWeatherCondition.setText(w.condition);
            }
            if (mUpdateTime != null) {
                Date lastTime = new Date(mWeatherInfo.last_sync);
                String date = DateFormat.getDateFormat(getContext()).format(lastTime);
                String time = DateFormat.getTimeFormat(getContext()).format(lastTime);
                mUpdateTime.setText(date + " " + time);
                mUpdateTime.setVisibility(View.GONE);
            }
            if (mWeatherTemp != null) {
                mWeatherTemp.setText(w.temp);
            }
            if (mWeatherLowHigh != null) {
                mWeatherLowHigh.setText(w.low + " | " + w.high);
            }

            if (mWeatherImage != null) {
                String conditionCode = w.condition_code;
                String condition_filename = "weather_" + conditionCode;
                int resID = res.getIdentifier(condition_filename, "drawable",
                        getContext().getPackageName());


                if (resID != 0) {
                    mWeatherImage.setImageDrawable(res.getDrawable(resID));
                } else {
                    mWeatherImage.setImageResource(R.drawable.weather_na);
                }
            }

            // Show the Weather panel view
            mWeatherPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * There is no data to display, display 'empty' fields and the
     * 'Tap to reload' message
     */
    private void setNoWeatherData() {
        boolean useMetric = PreferencesProvider.Interface.LivePanel.getUseMetric(getContext());

        if (mWeatherPanel != null) {
            if (mWeatherCity != null) {
                mWeatherCity.setText("CM Weather");  //Hard coding this on purpose
                mWeatherCity.setVisibility(View.VISIBLE);
            }
            if (mWeatherCondition != null) {
                mWeatherCondition.setText(R.string.weather_tap_to_refresh);
            }
            if (mUpdateTime != null) {
                mUpdateTime.setVisibility(View.GONE);
            }
            if (mWeatherTemp != null) {
                mWeatherTemp.setText(useMetric ? "0°c" : "0°f");
            }
            if (mWeatherLowHigh != null) {
                mWeatherLowHigh.setText("0° | 0°");
            }
            if (mWeatherImage != null) {
                mWeatherImage.setImageResource(R.drawable.weather_na);
            }

            // Show the Weather panel view
            mWeatherPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get the weather forecast XML document for a specific location
     * @param woeid
     * @return
     */
    private Document getDocument(String woeid) {
        try {
            boolean celcius = PreferencesProvider.Interface.LivePanel.getUseMetric(getContext());
            String urlWithDegreeUnit;

            if (celcius) {
                urlWithDegreeUnit = URL_YAHOO_API_WEATHER + "c";
            } else {
                urlWithDegreeUnit = URL_YAHOO_API_WEATHER + "f";
            }

            return new HttpRetriever().getDocumentFromURL(String.format(urlWithDegreeUnit, woeid));
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return null;
    }

    /**
     * Parse the weather XML document
     * @param wDoc
     * @return
     */
    private WeatherInfo parseXml(Document wDoc) {
        try {
            return new WeatherXmlParser(getContext()).parseWeatherResponse(wDoc);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
//    public void onClick(View v) {
////        if (v == mEmergencyCallButton) {
////            mCallback.takeEmergencyCallAction();
//        } else if (v == mWeatherPanel) {
//            if (!mHandler.hasMessages(QUERY_WEATHER)) {
//                mHandler.sendEmptyMessage(QUERY_WEATHER);
//            }
//        }
//    }
}
