package com.example.tmikulsk1.watsontranslateforme;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmikulsk1 on 23/03/2018.
 */

public class AvaliableLanguagesAdapter extends ArrayAdapter<AvailableLanguages> {


    public AvaliableLanguagesAdapter(Context context, ArrayList<AvailableLanguages> availableLanguages) {

        super(context, 0, availableLanguages);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return createItemView(position, convertView, parent);
    }

    public View createItemView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_adapter, parent, false);
        }

        TextView name = view.findViewById(R.id.name);

        AvailableLanguages currentLanguage = getItem(position);

        name.setText(currentLanguage.getName());

        return view;

    }
}
