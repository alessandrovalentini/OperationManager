package mainFragments;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.example.operationmanager.MainActivity;
import com.example.operationmanager.R;

import java.util.ArrayList;

import adapters.TaskListAdapter;
import dialogs.DetailsDialog;
import entities.Intervention;
import utils.Logger;

public class SelectedTasksFragment extends Fragment {
	Context context;
	ListView listview = null;
	private View layoutDropArea = null;
	TaskListAdapter adapter = null;
	MainActivity mainActivity;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		logMessage("Selected: onCreateView");
		context = this.getActivity();
		mainActivity = (MainActivity) this.getActivity();
		rootView = inflater.inflate(R.layout.fragment_selected,container, false);	
		layoutDropArea = rootView.findViewById(R.id.selected_layout);

		listview = (ListView) rootView.findViewById(R.id.selected_listview);
		
		return rootView;
	}

	@Override
	public void onResume() {
		logMessage("Selected: onResume");
		super.onResume();
		Intervention intervention = new Intervention(context);
/***************************************************************************************************/
        if(mainActivity.selectedInterventions.isEmpty())
            mainActivity.selectedInterventions = intervention.getSelectedOnly();
        adapter = new TaskListAdapter(context,  mainActivity.selectedInterventions, true);

        //adapter = new TaskListAdapter(context,  intervention.getSelectedOnly(), false);
/***************************************************************************************************/
		listview.setAdapter(adapter);
		setupDragDrop();
		listview.setOnItemClickListener(new DetailsOnClicListner()); // show details
	}

    @Override
    public void onPause(){
        Logger.debug(this, "onPause Called");
        super.onPause();
        ArrayList <Intervention> ls = new ArrayList<Intervention>();

        int n = listview.getAdapter().getCount();
        for (int i = 0; i < n; i++){
            ls.add((Intervention) listview.getAdapter().getItem(i));
        }

        for (Intervention i : ls){
            String descr = " Element "+i+" = "+i.getTitle();
            Logger.debug(this, descr);
        }
        mainActivity.selectedInterventions = ls;
    }

	private final class DetailsOnClicListner implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			final Intervention intervention = (Intervention) parent.getItemAtPosition(position);
			DetailsDialog d = new DetailsDialog(intervention);
			FragmentManager fm = mainActivity.getFragmentManager();
			d.show(fm, "");
		}
	}


	/* SUPPORT METHODS */

    //Update list maintaining the order
    public ArrayList<Intervention> updateList(ArrayList<Intervention> oldLs, ArrayList<Intervention> newLs){
        Logger.debug(this,"Updating List: newLs.size()="+newLs.size());
        if (oldLs != null) Logger.debug(this,"Updating List: oldLs.size()="+oldLs.size());

        if (oldLs == null) {
            Logger.debug(this, "oldLS is null");
            return newLs;
        }
        else if (oldLs.containsAll(newLs) && newLs.containsAll(oldLs)) { //Nothing changed, return
            Logger.debug(this,"Nothing changed");
            return oldLs;
        }
        else if(oldLs.containsAll(newLs)){ //Something removed, remove from old
            Logger.debug(this,"Something has been deleted");
            oldLs.retainAll(newLs);
            return oldLs;
        }
        else if(newLs.containsAll(oldLs)){ //Something added, add new elements
            Logger.debug(this,"Something has been added");
            newLs.removeAll(oldLs);
            oldLs.addAll(newLs);
            return oldLs;
        }
        else{
            Logger.error(this,"Something wrong with lists! You should not reach this point!");
            return null;
        }
    }

	// DRAG and DROP
	public void setupDragDrop() {
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View v,	int position, long arg3) {

				ClipData dragData = ClipData.newPlainText("oldPosition", ""+ position);// pass only the index of the object to moved
				v.startDrag(dragData, new DragShadowBuilder(v), null, 0);
				return true;
			}
		});

		myDragListener mDragListener = new myDragListener();
		layoutDropArea.setOnDragListener(mDragListener);
	}

	protected class myDragListener implements OnDragListener {
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:
				int newPosition = listview.pointToPosition(
						(int) (event.getX()), (int) event.getY());
				Log.d("Position", Integer.toString(newPosition));
				if (newPosition != ListView.INVALID_POSITION)
					return onDrop(event, newPosition);
				else
					return false;
			default:
				return true;
			}
		}

		private boolean onDrop(DragEvent event, int newPosition) {
			ClipData data = event.getClipData();
			if (data != null) {
				if (data.getItemCount() > 0) {
					String item = (String) data.getItemAt(0).getText();
					Integer oldPosition = Integer.valueOf(item);
					moveItem(oldPosition, newPosition);
					return true;
				}
			}
			return false;
		}

		private void moveItem(int oldIndex, int newIndex) {
			Intervention listItem = adapter.getItem(oldIndex);

			Log.d("RemoveItem", "Position: " + oldIndex);
			adapter.remove(listItem);

			Log.d("InsertItem", "Position: " + newIndex);
			adapter.insert(listItem, newIndex);
			adapter.notifyDataSetChanged();
		}
	}

	private void logMessage(String message){
		Log.d(""+this.getClass(), message);		
	}
}	