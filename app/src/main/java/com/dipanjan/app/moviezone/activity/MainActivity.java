package com.dipanjan.app.moviezone.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dipanjan.app.moviezone.adapter.RecyclerViewDataAdapter;
import com.dipanjan.app.moviezone.bo.MovieDetailsBO;
import com.dipanjan.app.moviezone.helper.NetworkCheck;
import com.dipanjan.app.moviezone.model.Movie;
import com.dipanjan.app.moviezone.model.MovieSeries;
import com.dipanjan.app.moviezone.util.AnalyticsTAGs;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import info.dipanjan.app.BuildConfig;
import info.dipanjan.app.R;

import com.dipanjan.app.moviezone.app.AppController;
import com.dipanjan.app.moviezone.helper.Helper;
import com.dipanjan.app.moviezone.model.DataModel;
import com.dipanjan.app.moviezone.model.SectionDataModel;
import com.dipanjan.app.moviezone.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Integer URLIndexPosition=-1;


    ArrayList<DataModel> dataModelArrayList=null;

    private ProgressBar progressBar;
    private RelativeLayout relativeLayout,relativeLayoutForMessageText;
    private CoordinatorLayout coordinatorLayout;
    private TextView messageText;
    ArrayList<MovieSeries> movieSeries=null;
    public MainActivity() {
        dataModelArrayList = new ArrayList<DataModel>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        relativeLayout = (RelativeLayout) findViewById(R.id.spinKitLayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        relativeLayoutForMessageText = (RelativeLayout)findViewById(R.id.messageLayout);
        messageText = (TextView)findViewById(R.id.messageText);

        relativeLayoutForMessageText.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);

        ThreeBounce threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

        toolbar.setTitleTextAppearance(this,R.style.CodeFont_Movie_Details_Headers);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("G PlayStore");

        }

        startUpActivity();



    }

    public void displayNetworkInfoAlert(final CoordinatorLayout coordinatorLayout, String msg,int displayMode){
        final Snackbar snackBar = Snackbar.make(coordinatorLayout,msg , Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackBar.getView();
        if(displayMode== Constant.SNACKBAR_DISPALY_MODE_SUCCESS){
            layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }else if(displayMode== Constant.SNACKBAR_DISPALY_MODE_FAILURE){
            layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Color_Red));
        }


        TextView action = layout.findViewById(android.support.design.R.id.snackbar_action);
        action.setMaxLines(5);
        action.setTextColor(layout.getContext().getResources().getColor(android.R.color.black));
        snackBar.setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    getFirebaseRemoteConfigJSONDataForHost();
                    snackBar.dismiss();
                    relativeLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    displayNetworkInfoAlert(coordinatorLayout, Constant.MESSAGE_NETWORK_NOT_AVIALABLE, Constant.SNACKBAR_DISPALY_MODE_FAILURE);
                }
            }
        });

        snackBar.show();
    }


    private void startUpActivity() {

        if (isNetworkAvailable()) {
            getFirebaseRemoteConfigJSONDataForHost();
        } else {
            progressBar.setVisibility(View.GONE);
            displayNetworkInfoAlert(coordinatorLayout, Constant.MESSAGE_NETWORK_NOT_AVIALABLE, Constant.SNACKBAR_DISPALY_MODE_FAILURE);
        }
    }

    public void getFirebaseRemoteConfigJSONDataForHost(){

        final FirebaseRemoteConfig mfirFirebaseRemoteConfig = AppController.getInstance().getFirebaseRemoteConfigInstanse();
        if(mfirFirebaseRemoteConfig!=null){
            mfirFirebaseRemoteConfig.fetch(AppController.getInstance().getFirebasecacheExpirationDuration(mfirFirebaseRemoteConfig)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mfirFirebaseRemoteConfig.activateFetched();
                        Log.d("Fetched Url", "Fetched value: " + mfirFirebaseRemoteConfig.getString(Constant.FIREBASE_REMOTE_CONFIG_HOST_LIST));

                        MovieDetailsBO.getHostList(mfirFirebaseRemoteConfig.getString(Constant.FIREBASE_REMOTE_CONFIG_HOST_LIST), new com.dipanjan.app.moviezone.listener.DataFetchListener() {
                            @Override
                            public void onResultFetchedSuccessful(String[] hostArr) {
                                List<String> strArr = Arrays.asList(hostArr);
                                Gson gson = new Gson();
                                String json = gson.toJson(strArr);
                                AppController.getInstance().storeSharedPreferenceData(Constant.SharedPreferenceTag.HOST_LIST,json);
                                performNetworkActivity(hostArr);
                            }

                            @Override
                            public void onResultFetchError() {
                                performNetworkActivity(Constant.BASE_URL);
                            }
                        });


                    }

                }
            });
        }else{
            performNetworkActivity(Constant.BASE_URL);
        }



    }

    public void performNetworkActivity(final String[] hostArr){
        NetworkCheck networkCheck = (NetworkCheck) new NetworkCheck(hostArr,new NetworkCheck.AsyncResponse() {
            @Override
            public Integer processFinish(Integer URLIndexPos) {
                if(URLIndexPos!=-1){
                  //  Toast.makeText(getApplicationContext(),"come dddd "+hostArr[URLIndexPos],Toast.LENGTH_SHORT).show();
                    URLIndexPosition = URLIndexPos;
                    relativeLayoutForMessageText.setVisibility(View.GONE);
                    messageText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    populateMovieData(URLIndexPos,hostArr);
                }else{
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayoutForMessageText.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.VISIBLE);
                    messageText.setText(NetworkCheck.DISPLAY_MSG_IF_HOST_NOT_RESOLVE);
                    displayNetworkInfoAlert(coordinatorLayout, NetworkCheck.DISPLAY_SNACBAR_MSG_IF_HOST_NOT_RESOLVE, Constant.SNACKBAR_DISPALY_MODE_FAILURE);
                }
                return null;
            }
        }).execute();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //startUpActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.fav) {
            if(URLIndexPosition!=-1){
                Intent intent = new Intent(getApplicationContext(),ListLikedMovieItems.class);
                Log.d("@@@@@@@@ ",""+URLIndexPosition);
                intent.putExtra("URLIndexPosition", URLIndexPosition);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                return true;
            }

        }
        if (id == R.id.search_movies) {
            if(URLIndexPosition!=-1){
                AppController.getInstance().trackEvent(AnalyticsTAGs.Category.CATEGORY_SEARCH_MOVIE, AnalyticsTAGs.Events.EVENT_OPEN_SEARCH_MOVIE, AnalyticsTAGs.Events.EVENT_OPEN_SEARCH_MOVIE);
                openDialog();
                return true;
            }

        }

        if (id == R.id.disclaimer) {
            AppController.getInstance().trackEvent(AnalyticsTAGs.Category.CATEGORY_MENU_CLICK, AnalyticsTAGs.Events.EVENT_OPEN_DISCLAIMER, AnalyticsTAGs.Events.EVENT_OPEN_DISCLAIMER);
            Intent intent = new Intent(getApplicationContext(),Disclaimer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            return true;

        }

        if (id == R.id.rate) {
            AppController.getInstance().trackEvent(AnalyticsTAGs.Category.CATEGORY_MENU_CLICK, AnalyticsTAGs.Events.EVENT_RATE_THE_APP, AnalyticsTAGs.Events.EVENT_RATE_THE_APP);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.dipanjan.app.moviezone"));
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void displayFetchedMovieItemAsList() {

        progressBar.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
        RecyclerView my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);

        my_recycler_view.setHasFixedSize(false);

        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, dataModelArrayList,URLIndexPosition);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        my_recycler_view.setAdapter(adapter);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }


    private final Object lock = new Object();
    int flag = 0;

    public void makeCount() {

        synchronized (lock) {
            flag++;

            if (flag == Constant.IDENTIFIER_LIST.length+2) {

                displayFetchedMovieItemAsList();
            }
        }
    }

    private void populateMovieData(Integer URLIndexPos,String[] hostArr) {
        Log.d("CALL","Y");
        initialisedListItems(URLIndexPos,hostArr);
        for (DataModel dataModel : dataModelArrayList) {
            if (dataModel != null) {
                fetchMovieData(dataModel);
            }
        }
        getFirebaseRemoteConfigJSONDataForMovieSeries(hostArr);
        populateMovieGerne(URLIndexPos);

    }


    private void populateMovieGerne(Integer URLIndexPos) {
        DataModel dataModel = new DataModel();
        SectionDataModel sectionDataModel = new SectionDataModel();
        sectionDataModel.setHeaderTitle("Browse By Genre");
        sectionDataModel.setMovieIdentifier(Constant.MovieCategory.MOVIE_GERNE);
        dataModel.setSectionDataModel(sectionDataModel);
        List<String> movieGerneList = Arrays.asList(getResources().getStringArray(R.array.genre_array_recycle_view));
        ArrayList<Movie> movieGenreList = new ArrayList<Movie>();
        for (String movieGerne : movieGerneList) {
            if(movieGenreList.size()!= Constant.MAX_ITEM_EACH_ROW) {
                Movie movie = new Movie();
                movie.setTitle(movieGerne);
                movie.setId(movieGerne.toLowerCase());
                movie.setMediumCoverImage(null);
                movie.setCategoryDescriptorTab(true);
                movieGenreList.add(movie);
            }else{
                break;
            }
        }
        dataModel.getSectionDataModel().setAllItemsInSection(movieGenreList);
        dataModelArrayList.add(2,dataModel);
        makeCount();
    }

    private void populateMovieSeries(){
        DataModel  dataModel = new DataModel();
        SectionDataModel sectionDataModel = new SectionDataModel();
        sectionDataModel.setHeaderTitle(Constant.Header.MOVIE_SERIES);
        sectionDataModel.setMovieIdentifier(Constant.MovieCategory.MOVIE_SERIES);
        dataModel.setSectionDataModel(sectionDataModel);
        ArrayList<Movie> movieSeriesListForGridDisplay = new ArrayList<Movie>();
        for(MovieSeries movieSeries:movieSeries){
            if(movieSeries!=null){
                if(movieSeriesListForGridDisplay.size()!=Constant.MAX_ITEM_EACH_ROW){
                    Movie movie = new Movie();
                    movie.setTitle(movieSeries.getMovieSeriesTitle());
                    movie.setId(movieSeries.getMovieSeriesTitle());
                    movie.setMovieSeries(movieSeries);
                    movie.setMediumCoverImage(movieSeries.getMoviePoster());
                    movie.setCategoryDescriptorTab(true);
                    movie.setMovieSeries(true);
                    movieSeriesListForGridDisplay.add(movie);
                }else{
                    break;
                }
            }
        }
        dataModel.getSectionDataModel().setAllItemsInSection(movieSeriesListForGridDisplay);
        dataModel.setMovieSeriesArrayList(movieSeries);
        dataModelArrayList.add(0,dataModel);
    }

    public void getFirebaseRemoteConfigJSONDataForMovieSeries(final String[] hostArr){

        final FirebaseRemoteConfig firebaseRemoteConfig = AppController.getInstance().getFirebaseRemoteConfigInstanse();
        if(firebaseRemoteConfig!=null){
            firebaseRemoteConfig.fetch(AppController.getInstance().getFirebasecacheExpirationDuration(firebaseRemoteConfig)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseRemoteConfig.activateFetched();
                        Log.d("Fetched Url", "Fetched value: " + firebaseRemoteConfig.getString(Constant.FIREBASE_REMOTE_CONFIG_MOVIE_JSON));

                        loadMovieSeriesContents(URLIndexPosition,hostArr, new DataFetchListener() {
                            @Override
                            public void onDataFetchSuccessfull() {
                                Gson gson = new Gson();
                                String json= gson.toJson(movieSeries);
                                //AppController.getInstance().storeSharedPreferenceData(Constant.SharedPreferenceTag.MOVIE_SERIES_JSON,json);
                                populateMovieSeries();
                                makeCount();
                            }
                        },firebaseRemoteConfig);

                    }else
                        Toast.makeText(MainActivity.this,"Someting went wrong please try again",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private  void loadMovieSeriesContents(int URLIndexPosition,String[] hostArr,DataFetchListener dataFetchListener,FirebaseRemoteConfig mFirebaseRemoteConfig){
        movieSeries = MovieDetailsBO.loadMovieSeriesContents(mFirebaseRemoteConfig.getString(Constant.FIREBASE_REMOTE_CONFIG_MOVIE_JSON),URLIndexPosition,hostArr);
        if(movieSeries!=null && movieSeries.size()>0){
            dataFetchListener.onDataFetchSuccessfull();
        }
    }

    public ArrayList<Movie> populateMovieList(String stg, ArrayList<Movie> movies, DataFetchListener dataFetchListener) {
        try {
            // Toast.makeText(getApplicationContext(),stg,Toast.LENGTH_SHORT).show();
            JSONObject jObj = new JSONObject(stg);
            String city = jObj.getString("status_message");
            System.out.println(city);

            JSONObject jObj1 = jObj.getJSONObject("data");
            JSONArray jArr = jObj1.getJSONArray("movies");
            for (int i = 0; i < jArr.length(); i++) {
                Movie movie = new Movie();
                JSONObject obj = jArr.getJSONObject(i);
                movie.setMediumCoverImage(obj.getString("large_cover_image"));
                movie.setTitle(obj.getString("title"));
                movie.setRating(obj.getString("rating"));
                movie.setYear(obj.getString("year"));
                movie.setRuntime(obj.getString("runtime"));
                movie.setId(obj.getString("id"));
                movie.setCategoryDescriptorTab(false);

                movies.add(movie);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        dataFetchListener.onDataFetchSuccessfull();
        return movies;
    }


    private ArrayList<DataModel> initialisedListItems(Integer URLIndexPos,String[] hostArr) {

        for (int index = 0; index < Constant.IDENTIFIER_LIST.length; index++) {
            ArrayList<Movie> movieArr = new ArrayList<Movie>();
            DataModel dataModel = new DataModel(Helper.generateURL(URLIndexPos,Constant.URL_LINK[index],hostArr), Constant.IDENTIFIER_LIST[index], Constant.IDENTIFIER_LIST[index], Constant.QUERY_PARAMETER[index], Helper.getFullHeaderName(Constant.HEADER_LIST[index]));
            SectionDataModel sectionDataModel = new SectionDataModel();
            sectionDataModel.setHeaderTitle(Helper.getFullHeaderName(Constant.HEADER_LIST[index]));
            sectionDataModel.setMovieIdentifier(Constant.IDENTIFIER_LIST[index]);
            sectionDataModel.setAllItemsInSection(movieArr);
            dataModel.setSectionDataModel(sectionDataModel);
            dataModelArrayList.add(dataModel);

        }
        return dataModelArrayList;
    }


    private int fetchMovieData(final DataModel dataModel) {
        StringRequest strReq = new StringRequest(Request.Method.GET,
                dataModel.getUrlLink(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    Uri uri = Uri.parse(dataModel.getUrlLink());
                    String queryParameter = uri.getQueryParameter(dataModel.getQueryParameter());
                    if (queryParameter != null) {
                        if (queryParameter.equalsIgnoreCase(dataModel.getCategory())) {
                            populateMovieList(response.toString(), dataModel.getSectionDataModel().getAllItemsInSection(), new DataFetchListener() {
                                @Override
                                public void onDataFetchSuccessfull() {
                                    makeCount();
                                }
                            });


                        }
                    }

                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getApplicationContext(),"Could Not connect",Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }
        });

        if(dataModel.getSectionDataModel().getMovieIdentifier().equalsIgnoreCase(Constant.MovieCategory.LATEST_MOVIES)){
            strReq.setShouldCache(false);
        }

        AppController.getInstance().addToRequestQueue(strReq);
        return 1;
    }


    DialogFragment dialogFragment;

    private void openDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment = new SearchDialogFragment();
        dialogFragment.show(ft, "dialog");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}


interface DataFetchListener {
    void onDataFetchSuccessfull();

}