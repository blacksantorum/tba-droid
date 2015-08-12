package com.tba.theboxingapp.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.tba.theboxingapp.Model.Boxer;

/**
 * Created by jackmiddlebrook on 8/11/15.
 */
public class BoxerListAdapter extends ArrayAdapter<Boxer> {


    public BoxerListAdapter(Context context, int resource) {
        super(context, resource);
    }
}
