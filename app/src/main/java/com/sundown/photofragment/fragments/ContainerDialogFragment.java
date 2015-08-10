package com.sundown.photofragment.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sundown.photofragment.R;
import com.sundown.photofragment.models.Photos;
import com.sundown.photofragment.pojo.ActivityResult;
import com.sundown.photofragment.models.Field;
import com.sundown.photofragment.models.PhotoField;
import com.sundown.photofragment.utils.PreferenceManager;
import com.sundown.photofragment.views.ContainerView;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Sundown on 7/27/2015.
 */
public class ContainerDialogFragment extends DialogFragment implements ContainerView.ExampleViewListener, PhotoFragment.PhotoFragmentListener {

    private static final double PROP_HEIGHT = .41; //chosen for visual appeal, don't remember how
    private static final double PROP_WIDTH = .85; //specifically but use w/e you want..
    private FragmentManager fm;
    private ContainerView view;

    //this will map each of our PhotoFragments to an ID
    private HashMap<Integer, PhotoFragment> photoFragments = new HashMap<>();

    //and the model will keep track of our PhotoFields..
    private Photos model;

    private LinearLayout layout; //the layout we will add our nested photofragments to..
    private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    //we only want setup to be called one time per rotation
    private static boolean setupCalled;
    private int width, height; //the width and height of this dialogfragment

    //i usually instantiate this way, so i can pass the model and other vars..
    public static ContainerDialogFragment newInstance(){
        ContainerDialogFragment fragment = new ContainerDialogFragment();
        fragment.model = new Photos();
        return fragment;
    }


    public ContainerDialogFragment(){} //leave empty pls


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @NonNull
    @Override //so if user rotates in camera/gallery, upon returning to this app THIS method gets called BEFORE the main activity's onResult... however
    //if user DOESNT rotate in camera/gallery, then this doesn't get called AT ALL..
    public View onCreateView(final LayoutInflater inflater, final ViewGroup viewGroup, Bundle savedInstanceState) {
        view = (ContainerView) inflater.inflate(R.layout.fragment_example, null);
        view.setListener(this);
        fm = getChildFragmentManager();
        setupCalled = false;

        getDialog().setTitle(getString(R.string.example));
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setup();
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisibleHint(true);
        if (height > 0 && width > 0) { //we only want to setup photofrags if height/width is set, otherwise let the onShowListener
            setup();                   // set in onCreateView call it cuz height/width is never 0 then.. onCreateView isn't called unless height/width need to be reset
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserVisibleHint(false);
    }


    @Override //cant do transactions after this method is called.. leads to that wonderful crash.. cant retain nested frags so lets remove them and display new ones on reload
    public void onSaveInstanceState(Bundle outState) {
        Set<Integer> keys = photoFragments.keySet();
        for (Integer key: keys){
            fm.beginTransaction().remove(photoFragments.get(key)).commit();
        }
        setupCalled = false; //we need this to run again when container fragment resumes so our new photofragments will init and have proper width/height
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        //needed due to a bug with the compatibility library but only if you do onRetainInstance
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }


    @Override
    public void dismiss() {
        photoFragments.clear();
        super.dismiss();
    }

    //called by main activity, hand these results off to each photofragment for potential processing
    public void handleActivityResults(ActivityResult result){
        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        int callingId = preferenceManager.getInt(PhotoFragment.FRAGMENT_ID);
        photoFragments.get(callingId).onActivityResult(result.requestCode, result.resultCode, result.data);
    }


    private void setup(){
        if (!setupCalled) {
            setupCalled = true;
            if (height == 0) {height = (int) (getDialog().getWindow().getDecorView().getHeight() * PROP_HEIGHT);}
            if (width == 0) { width = (int) (getDialog().getWindow().getDecorView().getWidth() * PROP_WIDTH);}

            initLayout();
        }
    }

    private void initLayout(){

        if (layout != null)
            layout.removeAllViews();

        layout = new LinearLayout(getActivity());
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        Integer[] keys = model.getKeys();

        //add model to layout
        for (Integer k: keys){
            addToLayout(k, model.getField(k));
        }

        //and update view
        view.updateView(layout);
    }

    private void addToLayout(int id, Field field){
        field.setId(id);
        addPhotoFragment(id, field);
    }


    private void addPhotoFragment(int containerViewId, Field field) {
        PhotoFragment photoFragment = photoFragments.get(containerViewId);
        if (photoFragment == null) { photoFragment = PhotoFragment.newInstance();}

        photoFragment.setListenerAndImageData(containerViewId, this, (PhotoField) field, width, height);
        photoFragments.put(containerViewId, photoFragment);

        RelativeLayout rl = new RelativeLayout(getActivity()); //this is a placeholder for our photofragment
        rl.setId(containerViewId); //if we set a tag here, it is attributed to the photofragment when we replace it with the placeholder.. not sure if id transfers too but for our purposes it doesnt matter
        layout.addView(rl);

        fm.beginTransaction().replace(rl.getId(), photoFragment).commit();


    }

    @Override //user closed dialog
    public void cancelPressed() { dismiss();}

    @Override //add a new photofragment to the layout
    public void addPhotoFragmentPressed() {
        PhotoField field = new PhotoField();
        int id = model.addField(field);
        addToLayout(id, field);
    }

    @Override //user pressed enter
    public void enterPressed() {
        //todo here you could save the pics in your model to your db..
        dismiss();
    }

    @Override
    public void deleteImage(String imageName, String thumbName) {
        //todo delete both images from your db..
    }

    @Override //remove this photoFragment from the layout..
    public void removePhotoFragment(int id) {
        PhotoFragment photoFragment = photoFragments.remove(id);
        fm.beginTransaction().remove(photoFragment).commit();
        model.removeField(id);
    }
}
