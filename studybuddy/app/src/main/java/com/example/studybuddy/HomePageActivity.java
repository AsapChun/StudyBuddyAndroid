package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.itmMessages:
                return true;
            case R.id.itmPaymentHistory:

                return true;
            case R.id.itmSettings:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                goToSettings();
                return true;
            case R.id.itmLogoff:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goBack() {
        this.finish();
    }

    public void goToSettings(){
        Intent newIntent = new Intent(this, SettingsActivity.class);
        this.startActivity(newIntent);
    }


}
