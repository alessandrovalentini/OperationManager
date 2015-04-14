package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.operationmanager.R;

import mainFragments.AllTasksFragment;
import mainFragments.MapsFragment;
import mainFragments.SelectedTasksFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	public static final int[] tabs = { R.string.all, R.string.selected, R.string.map};
    private static final int tabsCount = 5;

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0: return new AllTasksFragment();
		case 1: return new Fragment();
		case 2: return new SelectedTasksFragment();
		case 3: return new Fragment();
		case 4: return new MapsFragment();
		//case 5: return new Fragment();
		//case 6: return new NewInterventionFragment();
		}
		return null;
	}

	public int itemIndex(int position){
		switch(position){
		case 0: return 0;
		case 1: return 2;
		case 2: return 4;
		//case 3: return 6;
		default: return 1;
		}
	}

	@Override
	public int getCount() {
		return tabsCount;
	}

} 