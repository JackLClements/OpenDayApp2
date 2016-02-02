package uk.ac.uea.carfinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

public class HelpPage extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                onClickBack();
                break;
            case R.id.button2:
                onClickBack();
                break;
            case R.id.button3:
                onClickBack();
                break;
            case R.id.button4:

                break;
            case R.id.button5:
                onClickBack();
                break;
        }
    }

    private void onClickBack() {
        Intent getMapsPage = new Intent(this,MapsActivity.class);

        startActivity(getMapsPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help_page, menu);
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

    @Override
    public void onBackPressed() {
        // Write your code here
        onClickBack();
        super.onBackPressed();
    }


}
