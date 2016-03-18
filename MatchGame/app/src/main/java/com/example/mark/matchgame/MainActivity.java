package com.example.mark.matchgame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;


public class MainActivity extends Activity {

    private TextView selectTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectTextView = (TextView)findViewById(R.id.selectTextView);
        findViewById(R.id.SbS_button).setOnClickListener(sbsListener);
        findViewById(R.id.FbF_button).setOnClickListener(fbfListener);
        findViewById(R.id.fancy6by6_button).setOnClickListener(fancy6b6Listener);
    }

    public OnClickListener fancy6b6Listener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this,Fancy6By6_Activity.class));
        }
    };

    public OnClickListener sbsListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, SixBySix_Activity.class));
        }
    };

    public OnClickListener fbfListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this,FourByFour_Activity.class));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
