package com.example.tmikulsk1.watsontranslateforme;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by tmikulsk1 on 23/03/2018.
 */

public class AvailableLanguagesLoader extends AsyncTaskLoader<List<AvailableLanguages>> {

    private String mUrl;

    public AvailableLanguagesLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<AvailableLanguages> loadInBackground() {

        if (mUrl == null) {

            return null;

        }

        List<AvailableLanguages> availableLanguages = Utils.fetchLanguages(mUrl);
        return availableLanguages;
    }
}
