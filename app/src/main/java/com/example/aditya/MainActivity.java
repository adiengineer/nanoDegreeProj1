package com.example.aditya.nanodegreeproj1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;

import retrofit.Response;
import retrofit.Retrofit;



public class MainActivity extends Activity {

    private static int number_of_posters = 20; //later put val in values
    //public static String[] relative_poster_paths=new String[number_of_posters]; //this will hold the rel poster paths
    // static private String base_url="http://image.tmdb.org/t/p/";
    // static private String imageSize="w185"; //guide has a slash after this as well w185/

    //retrofit has been added to the project files

    //Root URL of our web service
    public static final String ROOT_URL = "http://api.themoviedb.org/"; //a little confused as to where to break the given url, try out.

    //end point string to be set acc to user choice, by default pop movies, modify this in a method set endpoint url
    private String endpointUrl = ROOT_URL + "3/movie/popular?api_key"; //TODO:api key has been removed as instructed, please generate your own key to use this code
    private String endpointopt1 = ROOT_URL + "3/movie/popular?api_key";
    private String endpointopt2 = ROOT_URL + "3/movie/top_rated?api_key";


    //List of type books this list will store type Book which is our data model
    private List<Result> movies; //this is where the processed json data will be stored. we have called its class as result
    private ImageAdapter imageAdapter=null;
    private GridView gridview;
    static float density;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: add code to get the JSON input, parse that to get create an arraylist of Movie objects, pass that to imageAdapter.
        //TODO: while implement refresh of grid in the listener change the list of arrayAdapter and can nottifydatasetchange

        DisplayMetrics displayMetrics= this.getResources().getDisplayMetrics();
        density=displayMetrics.density;
        //function to use retrofi
        // t

        //try calling async task before this, so that info will be available
        //we will need to pass in the movie arraylist to the adapter.

        //call getmovies to initialize the list movies
        getmovies();


        //create an array adapter

        //
        //if (movies==null)
        // {
        //    Log.i("msg","damn its null!");
//
        //     }


        gridview = (GridView) findViewById(R.id.gridView);

        //attach the adapter to the gridcview


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
       else if (id == R.id.most_popular) //if user selects this action
        {
            //check what is being currenly displayed. check last value of endpoint url
            if (this.endpointUrl==this.endpointopt1)
                 return true; //alreadt displayed so no need to change anything
            else
            {
               this.setEndpointURL(this.endpointopt1); //changed to top rated, call get movies again
                this.getmovies();
                return true; //return after finishing
            }
        }

        else if (id == R.id.top_rated) //if user selects this action
        {
            //check what is being currenly displayed. check last value of endpoint url
            if (this.endpointUrl==this.endpointopt2)
                return true; //alreadt displayed so no need to change anything
            else
            {
                this.setEndpointURL(this.endpointopt2); //changed to top rated, call get movies again
                this.getmovies();
                return true; //return after finishing
            }
        }
        //add code to handle the action clicks
        return super.onOptionsItemSelected(item);
    }



    //will use retrofit
    private void getmovies() {
        //While the app fetched data we are displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        // i have used the retrofit library to simplify fetching json data.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        //TODO: remove after check test call to modifying the url, actually will have to be accroding to user selectio in the menu
       // this.setEndpointURL(this.endpointopt2);

        MovieAPI service = retrofit.create(MovieAPI.class);


        Call<Movie> call = service.getMovieResults(this.endpointUrl); //called by default using pop endpoint.

        //Executing Call
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Response<Movie> response, Retrofit retrofit) {

                //one doubt: why is response a list of results each containing movies list?
                try {

                    movies = response.body().getResults();
                    loading.dismiss();

                    //if not already made initialize and set.
                    if (imageAdapter==null) //will happen onCreate.
                    {
                        imageAdapter = new ImageAdapter(getApplicationContext(), movies);
                        gridview.setAdapter(imageAdapter);
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                               //when a particular poster is clicked, launch the info activity and bundle info for the other activity
                                Intent i=new Intent(MainActivity.this,MovieInfoAct.class);
                              Result currMov=(Result)parent.getAdapter().getItem(position); // i know it is of type movie so cast is safe.
                                i.putExtra("movtitle",currMov.getTitle());
                                i.putExtra("movdate",currMov.getReleaseDate());
                                i.putExtra("movavg",currMov.getVoteAverage());
                                i.putExtra("movplot", currMov.getOverview()); //info as extras
                                i.putExtra("posterpath",currMov.getPosterPath());
                                startActivity(i);
                            }
                        });
                    }
                    else //if we have alrady displayed some data, then change that data and notify
                    {
                      //remove all elemnets from the list as we have fresh data which has come in.
                        imageAdapter.clear();
                        imageAdapter.addAll(movies); //hopefully adds the list
                        imageAdapter.notifyDataSetChanged(); //tells gridview to refresh itself.
                    }
                    // placeAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    //to modify endpoint url if option item is selected accordingly.
    private void setEndpointURL(String endpointURL)
    {
        this.endpointUrl=endpointURL;
    }

}
