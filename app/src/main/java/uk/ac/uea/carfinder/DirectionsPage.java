package uk.ac.uea.carfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

public class DirectionsPage extends AppCompatActivity implements View.OnClickListener {
    Chronometer chrono;
    long chronoNum = 0;
    ImageButton saveCar;
    ImageButton simpleView;
    ImageButton advancedView;
    ImageButton help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_page);
        TextView textView = (TextView) findViewById(R.id.textView);
        chrono = (Chronometer) findViewById(R.id.chronometer1);

        saveCar = (ImageButton) findViewById(R.id.button);
        simpleView = (ImageButton) findViewById(R.id.button2);
        advancedView = (ImageButton) findViewById(R.id.button3);
        help = (ImageButton) findViewById(R.id.button4);

        saveCar.setOnClickListener(this);
        simpleView.setOnClickListener(this);
        advancedView.setOnClickListener(this);
        help.setOnClickListener(this);


        Intent calledActivity = getIntent();

        String directions = calledActivity.getExtras().getString("Direction data");
        chronoNum = calledActivity.getExtras().getLong("Chronometer");

        textView.setText(directions);
        chrono.setBase(chronoNum);
        chrono.start();

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

                break;
            case R.id.button4:
                onClickHelp(v);
                break;

        }
    }

    private void onClickBack() {
        Intent back = new Intent();
        chrono.stop();
        back.putExtra("Chronometer", chronoNum);

        setResult(RESULT_OK, back);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directions_page, menu);
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

    public void onClickHelp(View view) {
        Intent getHelpPage = new Intent(this,HelpPage.class);

        startActivity(getHelpPage);


    }

    @Override
    public void onBackPressed() {
        // Write your code here
        onClickBack();
        super.onBackPressed();
    }
}
