package com.aware.byob;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.google.activity_recognition.ContextCard;
import com.aware.plugin.google.activity_recognition.Plugin;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class BYOB extends ActionBarActivity {

    private static RelativeLayout main;
    private static TextView intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_byob);

        main = (RelativeLayout) findViewById(R.id.byob_main);
        intro = (TextView) findViewById(R.id.byob_introduction);

        Intent framework = new Intent(this, Aware.class);
        startService(framework);

        if( Aware.getSetting(this, "study_id").length() == 0 ) {
            Intent join_study = new Intent(this, Aware_Preferences.StudyConfig.class);
            join_study.putExtra("study_url", "https://api.awareframework.com/index.php/webservice/index/164/5bzvA0711TYB");
            startService(join_study);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( ! Applications.isAccessibilityServiceActive(getApplicationContext()) ) {
            Dialog dialog = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please activate BYOB on the Accessibility Services!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent accessibilitySettings = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    accessibilitySettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                    startActivity(accessibilitySettings);

                    Toast.makeText(getApplicationContext(), "Turn BYOB ON here...", Toast.LENGTH_LONG).show();
                }
            });
            dialog = builder.create();
            dialog.show();
        }

        refreshUI(this);
    }

    private static void refreshUI(Context c) {
        if( main == null ) return;

        LinearLayout activity_cards = (LinearLayout) main.findViewById(R.id.card_view);
        if( activity_cards.getChildCount() > 0 ) {
            activity_cards.removeViewAt(0);
        }

        View activity_card = ContextCard.getContextCard(c);
        if( activity_card != null ) {
            activity_cards.addView(activity_card);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.byob, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Only track accelerometer and gyroscope if the user is walking, on foot or running
     */
    public static class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( intent.getAction().equals("ACTION_AWARE_GOOGLE_ACTIVITY_RECOGNITION") ) {
                refreshUI(context);
            }
        }
    }
}
