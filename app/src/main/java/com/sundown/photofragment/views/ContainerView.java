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
public class ContainerView extends LinearLayout implements View.OnClickListener {

    public interface ExampleViewListener{
        void closePressed();
        void addPhotoFragmentPressed();
    }

    private ExampleViewListener listener;
    public void setListener(ExampleViewListener listener){ this.listener = listener;}
    private LinearLayout container;
    private Button close, add;

    public ContainerView(Context context, AttributeSet attrs) { super(context, attrs);}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        container = (LinearLayout) findViewById(R.id.container);
        close = (Button) findViewById(R.id.close);
        add = (Button) findViewById(R.id.add);
        close.setOnClickListener(this);
        add.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                listener.closePressed();
                break;
            case R.id.add:
                listener.addPhotoFragmentPressed();
                break;
        }
    }

    public void updateView(LinearLayout layout){
        try { ((ViewGroup) layout.getParent()).removeView(layout); } catch (NullPointerException e){}
        container.addView(layout);
    }
}
