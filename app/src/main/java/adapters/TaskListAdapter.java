package adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.operationmanager.MainActivity;
import com.example.operationmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entities.Intervention;
import utils.Logger;

public class TaskListAdapter extends ArrayAdapter<Intervention> {
	private final Context context;
	private MainActivity mainActivity;
	private ArrayList<Intervention> ls = new ArrayList<Intervention>();
	private boolean selectedOnly;

	public TaskListAdapter(Context context, List<Intervention> values, boolean selectedOnly) {
		super(context, R.layout.tasks_list_layout, values);
		this.context = context;
		if (values.size() > 0 )
			this.ls = (ArrayList<Intervention>) values;
		this.selectedOnly = selectedOnly;

		//logMessage("values.size()"+values.size());
	}


	@SuppressLint("ViewHolder") public View getView(final int position, View convertView, ViewGroup parent) {
		mainActivity = (MainActivity) context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.tasks_list_layout, parent, false);
		ImageView icon = (ImageView) layout.findViewById(R.id.icon);
		TextView firstLine = (TextView) layout.findViewById(R.id.firstLine);
		TextView secondLine = (TextView) layout.findViewById(R.id.secondLine);
		
		//image
		final int index = position;
		if (dateDinstance(ls.get(index).getDeadline()) > 3)
			icon.setImageResource(R.drawable.ok);
		else if (dateDinstance(ls.get(index).getDeadline()) < 0)
			icon.setImageResource(R.drawable.error);
		else if (dateDinstance(ls.get(index).getDeadline()) < 3)
			icon.setImageResource(R.drawable.warning);

		//text
		firstLine.setText(ls.get(position).getTitle());
		secondLine.setText(ls.get(position).getContact().getAddress());

		//select button
		Button addButton = (Button) layout.findViewById(R.id.addButton);	
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Logger.debug(this,"Toggling in DB");
                ls.get(position).toogleSelection();

                /**********************************************************************************/
                Logger.debug(this,"Performing visual changes");
                //Perform visual changes
               if (selectedOnly) {
                    Logger.debug(this, "Remove element #" + position + " from selectedInterventions");
                    mainActivity.allInterventions.add(ls.get(position));
                    Logger.debug(this, "Added to all");
                    mainActivity.selectedInterventions.remove(position);
                   Logger.debug(this, "Removed from selected");

                }
                else{
                  Logger.debug(this, "Remove element #" + position + " from allInterventions");
                   mainActivity.selectedInterventions.add(ls.get(position));
                   Logger.debug(this, "Added to selected");
                   mainActivity.allInterventions.remove(position);
                   Logger.debug(this, "Removed from all");

                }
                /**********************************************************************************/

				notifyDataSetChanged();
			}
		});
		
		/*if (selectedOnly){
			addButton.setOnClickListener(new UnselectOnClick(position));
		}
		else{
			addButton.setOnClickListener(new SelectOnClick(position));
		}*/
		
		//delete button
		Button deleteButton = (Button) layout.findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 removeFromListView(position);
				 notifyDataSetChanged();
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
			    .setTitle(R.string.delete)
			    .setMessage(R.string.deleteMessage)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	removeFromListView(position);
						 notifyDataSetChanged();
						 dialog.dismiss();
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	dialog.dismiss();
			        }
			     })
			     .show();
			}
		});

		return layout;
	}
	
	public void removeFromListView(int position){
		final int index = position;
		Intervention i = ls.get(index);
		i.delete();
		ls.remove(index);
		notifyDataSetChanged();	
	}
	

	/*private final class SelectOnClick implements OnClickListener {
		int index;
		public SelectOnClick(int position) {
			index=position;
		}

		@Override
		public void onClick(View v) {
			logMessage("Item Selected");
			mainActivity.select(index);
			notifyDataSetChanged();
		}
	}

	private final class UnselectOnClick implements OnClickListener {
		int index;
		public UnselectOnClick(int position) {
			index=position;
		}
		
		@Override
		public void onClick(View v) {
			logMessage("Item Unselected");
			mainActivity.unselect(index);
			notifyDataSetChanged();
		}
	}*/

	
	
	private int dateDinstance(Date d){
		Calendar today = Calendar.getInstance();
		Calendar date  = Calendar.getInstance();
		date.setTime(d);
		
		
		long timediff = date.getTimeInMillis() - today.getTimeInMillis(); 
		final long day = 1000*3600*24;
		long days = timediff%day;
		
		/*Logger.warn(this, "Today:" + today + " ms:"+today.getTime());
		Logger.warn(this, "Date:" + date + " ms:"+date.getTime());
		Logger.warn(this, "Day Difference is "+days);*/
		
		Integer diff = Integer.valueOf(""+days);
		
		return diff;				
	}

}
