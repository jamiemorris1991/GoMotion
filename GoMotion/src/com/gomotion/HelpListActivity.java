package com.gomotion;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Screen showing all the help categories
 * available to the user.
 * 
 * @author Jack Hindmarch
 *
 */
public class HelpListActivity extends ListActivity 
{
	  public static String HELP_CATEGORY = "com.gomotion.HelpListActivity.HELP_CATEGORY";

	public void onCreate(Bundle bundle)
	  {
	    super.onCreate(bundle);
	    
	    String[] values = new String[] { 
	    	"Start Page", 
	    	"Home Screen", 
	    	"Outdoor",
	        "Indoor",
	        "History",
	        "Settings"
        };
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
	    
	    setListAdapter(adapter);
	  }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra(HELP_CATEGORY, position);
		startActivity(intent);
	}
	  
	  
} 
