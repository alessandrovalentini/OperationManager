package com.example.operationmanager;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

import adapters.CustomViewPager;
import adapters.TabsPagerAdapter;
import entities.Intervention;
import services.AlarmService;
import utils.Logger;
import utils.appLanguage;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	///private ViewPager viewPager;
	private CustomViewPager viewPager;
	private TabsPagerAdapter tabsAdapter;
	private ActionBar actionBar;
	private int[] tabs = TabsPagerAdapter.tabs;
	//Shared variables between tabs
	public ArrayList<Intervention> allInterventions; 
	public ArrayList<Intervention> selectedInterventions;
	
	//Preferences
	public boolean manualLanguage;
	public String lang;
	public String defaultLocation;
	public Integer zoomLevel;
    public String defaultLang;
	
	//Tab marker
	int tabSelected = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBar = getActionBar();

        allInterventions = new ArrayList<Intervention>();
        selectedInterventions = new ArrayList<Intervention>();

        //Load lists, avoid some issues on "selected"
        Intervention intervention = new Intervention(this);
        selectedInterventions = intervention.getSelectedOnly();
        allInterventions = intervention.getNotSelectedOnly();

		//Mockup Class
		/*Interventions interventions = new Interventions(this);		
		allInterventions = interventions.getAll();
		selectedInterventions = interventions.getSelected();*/

		// Initilization
		//this.viewPager = (ViewPager) findViewById(R.id.pager);
		this.viewPager = (CustomViewPager) findViewById(R.id.pager);
		tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(tabsAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);   

		// Adding Tabs in ActionBar
		for (int tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}
		/*
        //on swiping the viewpager make respective tab selected
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page make respected tab selected
            	logMessage("Tab selected: "+position);
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });*/

        defaultLang = Locale.getDefault().toString();


        Logger.warn(this,"Launching alarm from MainActivity");
        Intent startServiceIntent = new Intent(this, AlarmService.class);
        this.startService(startServiceIntent);
	}
	
	@Override
	protected void onStart() {
		//Reading preferences
		loadPreferences();

		//set Language
		if (manualLanguage){
            Logger.debug(this,"New language selected: " + lang);
			appLanguage.setLanguage(this, lang);
        }
        else {
            Logger.debug(this, "Automatic language selected: " + defaultLang);
            appLanguage.setLanguage(this, defaultLang);
        }
		super.onStart();
	}
	
	@Override
	protected void onResume() {
        Logger.debug(this, "Resuming tab " + tabSelected);
        viewPager.setCurrentItem(tabSelected);

        //Update tab name accordingly with selected language
        int i = 0;
        for (int tab_name : tabs) {
            actionBar.getTabAt(i).setText(tab_name);
            i++;
        }
		super.onResume();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu); //Settings menu
        return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
        case R.id.action_addnew:
            Intent addIntent = new Intent(this, AddActivity.class);
            startActivity(addIntent);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Logger.debug(this, "Tab Selected");
		tabSelected = tabsAdapter.itemIndex(tab.getPosition());
		Logger.debug(this, "tabSelected="+tabSelected);
		viewPager.setCurrentItem(tabSelected);
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	
	public void select(int index){
		Intervention element = allInterventions.remove(index);
		selectedInterventions.add(element);
	}

	
	public void unselect(int index){
		Intervention element = selectedInterventions.remove(index);
		allInterventions.add(element);
	}
	
	private void loadPreferences(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Locale currentLocale = getResources().getConfiguration().locale;
		
		manualLanguage = sharedPref.getBoolean("manual_language", false);
		lang = sharedPref.getString("language", currentLocale.getLanguage());
		defaultLocation = sharedPref.getString("defaultLocation",null);
		zoomLevel = sharedPref.getInt("zoom_level", 13);
		
		Logger.debug(this, "manualLanguage = "+manualLanguage);
        Logger.debug(this, "lang = " + lang);
        Logger.debug(this, "defaultLocation = " + defaultLocation);
        Logger.debug(this, "zoomLevel = " + zoomLevel);
	}
	
	//GETTER AND SETTER
	public boolean isManualLanguage() {
		return manualLanguage;
	}

	public String getLang() {
		return lang;
	}

	public String getDefaultLocation() {
		return defaultLocation;
	}

	public Integer getZoomLevel() {
		return zoomLevel;
	}
	
	
	
}
