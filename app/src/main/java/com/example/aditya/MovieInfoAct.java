package com.example.aditya.nanodegreeproj1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieInfoAct extends Activity {


    private String base_url="http://image.tmdb.org/t/p/";
    private String imageSize="w185";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mov_info_activity); //our layout is inflated.

        setupLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_info, menu);
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

    private void setupLayout()
    {
        Intent launcher=getIntent();
        ImageView poster=(ImageView)findViewById(R.id.poster);
        String poster_path= launcher.getStringExtra("posterpath");
        String modified_poster_path=base_url+imageSize+poster_path;

        //call picasso again.
        Picasso.with(this).load(modified_poster_path).into(poster);

        setTitle(launcher.getStringExtra("movtitle"));



        //TextView releasedate=(TextView)findViewById(R.id.releasedate);
        TextView plot=(TextView)findViewById(R.id.plot);
        TextView rating=(TextView)findViewById(R.id.rating);


       // releasedate.setText(launcher.getStringExtra("movdate"));
        plot.setText(launcher.getStringExtra("movplot"));
        Double rat=launcher.getDoubleExtra("movavg", 0);
        rating.setText(rat.toString()+"/10"+"  "+launcher.getStringExtra("movdate"));
    }


}
