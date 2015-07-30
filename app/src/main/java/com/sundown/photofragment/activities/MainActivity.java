package com.sundown.photofragment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.sundown.photofragment.R;
import com.sundown.photofragment.fragments.ContainerDialogFragment;
import com.sundown.photofragment.logging.Log;
import com.sundown.photofragment.pojo.ActivityResult;

/*
*   NOTE: This code is excised from a larger project I'm building right now, so some of
*   the variable names (i.e. what is a PhotoField?!) might seem a little odd without knowing
*   that the bigger project contains multiple Field types.. anyway I'll try to clear up
*   variable names where I see them but just keep that in mind.. Also removed from this is
*   any permanent storage for photos, so if you use this in your project you should incorporate
*   hard storage of the photos into whatever you're using as a DB.. I'll mark the hooks
*   with TODO tags..
*/
public class MainActivity extends AppCompatActivity {

    private static final String CONTAINER_FRAGMENT = "container_fragment";
    private FragmentManager fm;

    //nothing special about this, this fragment is simply a container for our nested fragments.
    private ContainerDialogFragment containerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        if (savedInstanceState == null) { //activity first created, create the container
            containerFragment = ContainerDialogFragment.newInstance();
            containerFragment.show(fm, CONTAINER_FRAGMENT);

        } else { //returning from background, grab container from fm
            containerFragment = (ContainerDialogFragment) fm.findFragmentByTag(CONTAINER_FRAGMENT);
        }
    }

    //when the intent returns results to our activity, this is where they come in..
    //since our nested fragments still need to be created, lets send
    //these results to the container fragment holding our nested fragments..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (containerFragment != null){
                containerFragment.handleActivityResults(new ActivityResult(requestCode, resultCode, data));
            }
        } catch (Exception e){ Log.e(e);}
    }
}
