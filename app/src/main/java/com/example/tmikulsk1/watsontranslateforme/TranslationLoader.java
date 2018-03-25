package com.example.tmikulsk1.watsontranslateforme;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by tmikulsk1 on 24/03/2018.
 */

public class TranslationLoader extends AsyncTaskLoader<String> {

    private String mUrl;

    public TranslationLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (mUrl == null) {

            return null;
        }

        String result = Utils.fetchTranslation(mUrl);

        return result;
    }
}
