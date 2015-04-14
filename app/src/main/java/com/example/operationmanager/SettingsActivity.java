package com.example.operationmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import preferencesFragment.SettingsFragment;

public class SettingsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//Intent intent = getIntent();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}
	
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
	}
}
