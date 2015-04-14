package mainFragments;

import android.annotation.SuppressLint;
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

@SuppressLint("NewApi")
public class AllTasksFragment extends Fragment {
	Context context;
	ArrayList<String> selectedls = new ArrayList<String>();
	ListView listview = null;
	private View layoutDropArea = null;
	TaskListAdapter adapter = null;
	View rootView;
	MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		logMessage("All: onCreateView");
		context = this.getActivity();	
		rootView = inflater.inflate(R.layout.fragment_all,container, false);
		layoutDropArea = rootView.findViewById(R.id.all_layout);
		mainActivity = (MainActivity) this.getActivity();
		listview = (ListView) rootView.findViewById(R.id.all_listview);

		return rootView;
	}
	
	@Override
	public void onResume() {
		logMessage("All: onResume");
		super.onResume();
		
		Intervention intervention = new Intervention(context);
        /***************************************************************************************************/
        mainActivity.allInterventions = intervention.getNotSelectedOnly();
        adapter = new TaskListAdapter(context,  mainActivity.allInterventions, false);

        //adapter = new TaskListAdapter(context,  intervention.getNotSelectedOnly(), false);
/***************************************************************************************************/
		listview.setAdapter(adapter);
		//adapter.notifyDataSetChanged();
		setupDragDrop();
		
		listview.setOnItemClickListener(new DetailsOnClicListner()); // show details
	}

	/* SUPPORT METHODS */
	// Show details on clic
	private final class DetailsOnClicListner implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			final Intervention intervention = (Intervention) parent.getItemAtPosition(position);
			
			DetailsDialog d = new DetailsDialog(intervention);
			FragmentManager fm = mainActivity.getFragmentManager();
			d.show(fm, "");
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
					Integer oldPosition =  Integer.valueOf(item);
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
	
	/*private void addIntervention(){
		Intent intent = new Intent(); //setup properly
		
		EditText titleEdit = (EditText) rootView.findViewById(R.id.titleField);
		EditText detailsEdit = (EditText) rootView.findViewById(R.id.descriptionField);
		DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.descriptionField);
		
		String title = titleEdit.getText().toString();
		String details = detailsEdit.getText().toString();
		Date date = new Date(datePicker.getCalendarView().getDate());
		//get contact ID or contact object
	}*/
	
	private void logMessage(String message){
		Log.d(""+this.getClass(), message);
	}
	
}	