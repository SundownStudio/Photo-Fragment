package com.sundown.photofragment.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sundown.photofragment.R;

/**
 * Created by Sundown on 7/27/2015.
 */
public class ExampleView extends LinearLayout implements View.OnClickListener {

    public interface ExampleViewListener{
        void cancelPressed();
        void addPhotoFragmentPressed();
        void enterPressed();
    }


    private ExampleViewListener listener;
    public void setListener(ExampleViewListener listener){ this.listener = listener;}

    private LinearLayout container;
    public LinearLayout getContainer(){return container;}

    private Button cancel, enter, add;

    public ExampleView(Context context, AttributeSet attrs) { super(context, attrs);}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        container = (LinearLayout) findViewById(R.id.container);

        cancel = (Button) findViewById(R.id.cancel);
        enter = (Button) findViewById(R.id.enter);
        add = (Button) findViewById(R.id.add);
        cancel.setOnClickListener(this);
        enter.setOnClickListener(this);
        add.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                listener.cancelPressed();
                break;
            case R.id.add:
                listener.addPhotoFragmentPressed();
                break;
            case R.id.enter:
                listener.enterPressed();
                break;
        }
    }

    public void updateView(LinearLayout layout){
        try { ((ViewGroup) layout.getParent()).removeView(layout); } catch (NullPointerException e){}
        container.addView(layout);
    }
}
