package com.gomotion;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Screen which shows text for a selected category
 * from the HelpListActivity.
 * 
 * @author Jack Hindmarch
 *
 */
public class HelpActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
     	getActionBar().setDisplayHomeAsUpEnabled(true);	
		
		LinearLayout content = (LinearLayout) findViewById(R.id.content);
		int category = getIntent().getIntExtra(HelpListActivity.HELP_CATEGORY, 0);
		
		switch(category)
		{
			case 0:
				setTitle("Start Page Help");
				
				TextView offlineModeTitle = new TextView(this);
				TextView offlineModeDesc = new TextView(this);
				
				String offlineModeTitleStr = "Offline Mode";
				String offlineModeDescStr = "The basic mode for this app is “Offline Mode”. This has all the exercise features of the app," +
						" and you can see your own exercise history and view routes.";
				
				offlineModeTitle.setText(offlineModeTitleStr);
				offlineModeDesc.setText(offlineModeDescStr);
				
				offlineModeTitle.setTextSize(22);
				offlineModeDesc.setTextSize(14);
				
				TextView facebookTitle = new TextView(this);
				TextView facebookDesc = new TextView(this);
				
				String facebookTitleStr = "Facebook";
				String facebookDescStr = "With Facebook mode you can connect the app to your Facebook account," +
						" adding social networking features. You can see your friends’ exercises and also share your" +
						" own to your Facebook news feed.";
				
				facebookTitle.setText(facebookTitleStr);
				facebookDesc.setText(facebookDescStr);
				
				facebookTitle.setTextSize(22);
				facebookDesc.setTextSize(14);	
				
				facebookTitle.setPadding(0, 20, 0, 0);
				
				content.addView(offlineModeTitle);
				content.addView(offlineModeDesc);
				
				content.addView(facebookTitle);
				content.addView(facebookDesc);
				
				break;
			case 1:
				setTitle("Home Screen Help");
				
				TextView timelineTitle = new TextView(this);
				TextView timelineDesc = new TextView(this);
				
				String timelineTitleStr = "News Feed";
				String timelineDescStr = "The main screen you will see in the app is the News Feed. It is only available" +
						" when logged into Facebook, as it shows your friends shared exercises. For outdoor exercises you can " +
						"click on a friend’s post to view the route they travelled.";
				
				timelineTitle.setText(timelineTitleStr);
				timelineDesc.setText(timelineDescStr);
				
				timelineTitle.setTextSize(22);
				timelineDesc.setTextSize(14);
				
				TextView leaderboardsTitle = new TextView(this);
				TextView leaderboardsDesc = new TextView(this);
				
				String leaderboardsTitleStr = "Leaderboards";
				String leaderboardsDescStr = "As well as timeline view, in Facebook mode you can sort your friends" +
						" and your exercises into a “Leaderboard”. This way you can see who ran the furthest or cycled " +
						"the fastest! Everybody likes healthy competition to spur you on to achieve more.";
				
				leaderboardsTitle.setText(leaderboardsTitleStr);
				leaderboardsDesc.setText(leaderboardsDescStr);
				
				leaderboardsTitle.setTextSize(22);
				leaderboardsDesc.setTextSize(14);	
				
				leaderboardsTitle.setPadding(0, 20, 0, 0);
				
				content.addView(timelineTitle);
				content.addView(timelineDesc);
				
				content.addView(leaderboardsTitle);
				content.addView(leaderboardsDesc);
				
				break;
			case 2:
				setTitle("Outdoor Help");
				
				TextView outdoorTitle = new TextView(this);
				TextView outdoorDesc = new TextView(this);
				
				String outdoorTitleStr = "Outdoor";
				String outdoorDescStr = "With outdoor exercises you can track any route by selecting the type - “run”, “walk” or" +
						" ”cycle”.  You need to have GPS enabled on your device to allow the app to track your route! Press start to" +
						" begin tracking the route, and remember to click finish when you’re done.";
				
				outdoorTitle.setText(outdoorTitleStr);
				outdoorDesc.setText(outdoorDescStr);
				
				outdoorTitle.setTextSize(22);
				outdoorDesc.setTextSize(14);
				
				content.addView(outdoorTitle);
				content.addView(outdoorDesc);				
				
				break;
			case 3:
				setTitle("Indoor Help");
				
				TextView pushUpsTitle = new TextView(this);
				TextView pushUpsDesc = new TextView(this);
				
				String pushUpsTitleStr = "Push Ups";
				String pushUpsDescStr = "To complete a push ups exercise, the phone should be placed on the floor (or at a suitable height)" +
						" where your nose will be. As you lower yourself into the push up, your nose should touch the screen and register that " +
						"you have completed a push up. You then need to push back up and repeat the process for the amount of reps and sets you selected.";
				
				pushUpsTitle.setText(pushUpsTitleStr);
				pushUpsDesc.setText(pushUpsDescStr);
				
				pushUpsTitle.setTextSize(22);
				pushUpsDesc.setTextSize(14);
				
				TextView sitUpsTitle = new TextView(this);
				TextView sitUpsDesc = new TextView(this);
				
				String sitUpsTitleStr = "Sit Ups";
				String sitUpsDescStr = "When completing sit ups you must have the phone in your right hand and then complete a sit up as you would" +
						" normally, until you have completed the amount of reps you selected. You can then rest between sets whilst waiting for the timer" +
						" to reach zero.";
				
				sitUpsTitle.setText(sitUpsTitleStr);
				sitUpsDesc.setText(sitUpsDescStr);
				
				sitUpsTitle.setTextSize(22);
				sitUpsDesc.setTextSize(14);	
				
				sitUpsTitle.setPadding(0, 20, 0, 0);
				
				TextView customTitle = new TextView(this);
				TextView customDesc = new TextView(this);
				
				String customTitleStr = "Custom";
				String customDescStr = "There is also section for a custom exercises where you can input a name of your choice and then use the application" +
						" by tapping the screen. Get creative and try new exercises that even your friends haven’t tried yet!";
				
				customTitle.setText(customTitleStr);
				customDesc.setText(customDescStr);
				
				customTitle.setTextSize(22);
				customDesc.setTextSize(14);	
				
				customTitle.setPadding(0, 20, 0, 0);
				
				content.addView(pushUpsTitle);
				content.addView(pushUpsDesc);
				
				content.addView(sitUpsTitle);
				content.addView(sitUpsDesc);
				
				content.addView(customTitle);
				content.addView(customDesc);
				
				break;
			case 4:
				setTitle("History Help");
				
				TextView viewRouteTitle = new TextView(this);
				TextView viewRouteDesc = new TextView(this);
				
				String viewRouteTitleStr = "View Route";
				String viewRouteDescStr = "This allows you to view the route that you have travelled on a Google Map, showing the start and end point," +
						" as well as everything in between. (only available on outdoor exercises)";
				
				viewRouteTitle.setText(viewRouteTitleStr);
				viewRouteDesc.setText(viewRouteDescStr);
				
				viewRouteTitle.setTextSize(22);
				viewRouteDesc.setTextSize(14);
				
				TextView shareRouteTitle = new TextView(this);
				TextView shareRouteDesc = new TextView(this);
				
				String shareRouteTitleStr = "Share Route";
				String shareRouteDescStr = "You can share your route on your facebook wall as well as on the GoMotion wall for your friends to see" +
						" your achievements. This requires you to be in Facebook Mode.";
				
				shareRouteTitle.setText(shareRouteTitleStr);
				shareRouteDesc.setText(shareRouteDescStr);
				
				shareRouteTitle.setTextSize(22);
				shareRouteDesc.setTextSize(14);	
				
				shareRouteTitle.setPadding(0, 20, 0, 0);
				
				TextView deleteTitle = new TextView(this);
				TextView deleteDesc = new TextView(this);
				
				String deleteTitleStr = "Delete";
				String deleteDescStr = "If you want to remove an exercise that you’ve done, just press delete to remove it.";
				
				deleteTitle.setText(deleteTitleStr);
				deleteDesc.setText(deleteDescStr);
				
				deleteTitle.setTextSize(22);
				deleteDesc.setTextSize(14);	
				
				deleteTitle.setPadding(0, 20, 0, 0);
				
				content.addView(viewRouteTitle);
				content.addView(viewRouteDesc);
				
				content.addView(shareRouteTitle);
				content.addView(shareRouteDesc);
				
				content.addView(deleteTitle);
				content.addView(deleteDesc);
				
				break;
			case 5:
				setTitle("Settings Help");
				
				TextView unitsTitle = new TextView(this);
				TextView unitsDesc = new TextView(this);
				
				String unitsTitleStr = "Units";
				String unitsDescStr = "You can choose to view and share your data either with metric or imperial units to suit your preference.";
				
				unitsTitle.setText(unitsTitleStr);
				unitsDesc.setText(unitsDescStr);
				
				unitsTitle.setTextSize(22);
				unitsDesc.setTextSize(14);
				
				TextView routeColourTitle = new TextView(this);
				TextView routeColourDesc = new TextView(this);
				
				String routeColourTitleStr = "Route Colour";
				String routeColourDescStr = "This will change the colour of the route drawn on the map to show your tracked outdoor exercises. " +
						"The options are red, green or blue.";
				
				routeColourTitle.setText(routeColourTitleStr);
				routeColourDesc.setText(routeColourDescStr);
				
				routeColourTitle.setTextSize(22);
				routeColourDesc.setTextSize(14);	
				
				routeColourTitle.setPadding(0, 20, 0, 0);
				
				TextView routeOpacityTitle = new TextView(this);
				TextView routeOpacityDesc = new TextView(this);
				
				String routeOpacityTitleStr = "Route Opacity";
				String routeOpacityDescStr = "This changes how transparent the route appears on the map. A higher opacity is less see-through," +
						" whereas a lower opacity allows you to see the map underneath the line.";
				
				routeOpacityTitle.setText(routeOpacityTitleStr);
				routeOpacityDesc.setText(routeOpacityDescStr);
				
				routeOpacityTitle.setTextSize(22);
				routeOpacityDesc.setTextSize(14);	
				
				routeOpacityTitle.setPadding(0, 20, 0, 0);
				
				TextView routeAccuracyTitle = new TextView(this);
				TextView routeAccuracyDesc = new TextView(this);
				
				String routeAccuracyTitleStr = "Route Tracking Accuracy";
				String routeAccuracyDescStr = "To save battery life you can reduce the accuracy of the route tracking. However this will make your " +
						"route less detailed and the distance may not be as accurate.";
				
				routeAccuracyTitle.setText(routeAccuracyTitleStr);
				routeAccuracyDesc.setText(routeAccuracyDescStr);
				
				routeAccuracyTitle.setTextSize(22);
				routeAccuracyDesc.setTextSize(14);	
				
				routeAccuracyTitle.setPadding(0, 20, 0, 0);
				
				TextView defaultSettingsTitle = new TextView(this);
				TextView defaultSettingsDesc = new TextView(this);
				
				String defaultSettingsTitleStr = "Default Indoor Settings";
				String defaultSettingsDescStr = "This is where you can change your default settings so that when you start an indoor activity you are" +
						" able to select default for ease and speed.";
				
				defaultSettingsTitle.setText(defaultSettingsTitleStr);
				defaultSettingsDesc.setText(defaultSettingsDescStr);
				
				defaultSettingsTitle.setTextSize(22);
				defaultSettingsDesc.setTextSize(14);	
				
				defaultSettingsTitle.setPadding(0, 20, 0, 0);
				
				content.addView(unitsTitle);
				content.addView(unitsDesc);
				
				content.addView(routeColourTitle);
				content.addView(routeColourDesc);
				
				content.addView(routeOpacityTitle);
				content.addView(routeOpacityDesc);
				
				content.addView(routeAccuracyTitle);
				content.addView(routeAccuracyDesc);
				
				content.addView(defaultSettingsTitle);
				content.addView(defaultSettingsDesc);
				break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
