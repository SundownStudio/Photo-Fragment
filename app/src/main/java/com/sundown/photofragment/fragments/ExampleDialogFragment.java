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
import com.sundown.photofragment.pojo.Field;
import com.sundown.photofragment.pojo.PhotoField;
import com.sundown.photofragment.views.ExampleView;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Sundown on 7/27/2015.
 */
public class ExampleDialogFragment extends DialogFragment implements ExampleView.ExampleViewListener, PhotoFragment.PhotoFragmentListener {

    private static final double PROP_HEIGHT = .41;
    private static final double PROP_WIDTH = .85;
    private FragmentManager fm;
    private ExampleView view;
    private Stack<ActivityResult> activityResults;

    //this will keep track of our PhotoFragments
    private HashMap<Integer, PhotoFragment> photoFragments = new HashMap<>();

    //and this will keep track of our PhotoFields.. normally this would contain other field types as well
    //but limited (and renamed) for this demo..
    private Photos model;


    private LinearLayout layout;
    private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static boolean specsSet;
    private int width, height;

    //i usually instantiate this way, so i can pass the model and other vars (not needed in this circumstance but used in larger project)..
    public static ExampleDialogFragment newInstance(){
        ExampleDialogFragment fragment = new ExampleDialogFragment();
        return fragment;
    }


    public ExampleDialogFragment(){}; //leave empty pls


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        activityResults = new Stack<>();
    }


    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup viewGroup, Bundle savedInstanceState) {
        view = (ExampleView) inflater.inflate(R.layout.fragment_example, null);
        view.setListener(this);
        fm = getChildFragmentManager();
        specsSet = false;

        getDialog().setTitle(getString(R.string.example));
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                //called AFTER onResume...
                height = (int) (getDialog().getWindow().getDecorView().getHeight() * PROP_HEIGHT);
                width = (int) (getDialog().getWindow().getDecorView().getWidth() * PROP_WIDTH);
                specsSet = true;

                if (model == null){
                    model = new Photos();
                }

                initLayout();
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisibleHint(true);
        if (specsSet){
            initLayout();
            handleActivityResults();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserVisibleHint(false);
    }

    //todo: see if we still need to do this.. why do we? why not just redo the interface each rotation? easier... and dont have disappearing fragments onscreen in some events..
    @Override //cant do transactions after this method is called.. leads to that wonderful crash.. but must remove fragments for them to display on reload and cant do it when putting together layout
    public void onSaveInstanceState(Bundle outState) {
        Set<Integer> keys = photoFragments.keySet();
        for (Integer key: keys){
            fm.beginTransaction().remove(photoFragments.get(key)).commit();
        }
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

    public void addActivityResult(ActivityResult result){
        activityResults.add(result);
    }

    private void handleActivityResults(){
        if (activityResults.size() > 0){
            Set<Integer> keys = photoFragments.keySet();

            for (ActivityResult result: activityResults){
                for (Integer key: keys){
                    photoFragments.get(key).onActivityResult(result.requestCode, result.resultCode, result.data);
                }
            }
            activityResults.clear();
        }
    }

    private void initLayout(){

        if (layout != null)
            layout.removeAllViews();

        layout = new LinearLayout(getActivity());
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        Integer[] keys = model.getKeys();

        for (Integer k: keys){
            addToLayout(k, model.get(k));
        }
        view.updateView(layout);
    }

    private void addToLayout(int id, Field field){
        field.setId(id);
        addPhotoFragment(id, field);
    }


    private void addPhotoFragment(int containerViewId, Field field) {
        PhotoFragment photoFragment = photoFragments.get(containerViewId);
        if (photoFragment == null) {
            photoFragment = PhotoFragment.newInstance();
        }

        photoFragment.setListenerAndImageData(containerViewId, this, (PhotoField) field, width, height);
        photoFragments.put(containerViewId, photoFragment);

        RelativeLayout rl = new RelativeLayout(getActivity());
        rl.setId(containerViewId);
        layout.addView(rl);

        fm.beginTransaction().replace(rl.getId(), photoFragment).commit();


    }

    @Override
    public void cancelPressed() {
        dismiss();
    }

    @Override
    public void addPhotoFragmentPressed() {
        PhotoField field = new PhotoField(false);
        int id = model.addField(field);
        addToLayout(id, field);
    }

    @Override
    public void enterPressed() {
        //here you could tell the main activity to save your pics..
        dismiss();
    }

    @Override
    public void deleteImage(String imageName, String thumbName) {
        //delete this image from your db..
    }

    @Override
    public void removePhotoFragment(int id) {
        //remove this photoFragment from the layout..
        PhotoFragment photoFragment = photoFragments.remove(id);
        fm.beginTransaction().remove(photoFragment).commit();
        model.remove(id);
    }
}
