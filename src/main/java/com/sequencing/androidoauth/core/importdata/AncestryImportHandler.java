package com.sequencing.androidoauth.core.importdata;

import android.app.Activity;
import android.view.View;

import com.sequencing.androidoauth.fragments.AncestryImportDialog_;

/**
 * Created by omazurova on 3/27/2017.
 */

public class AncestryImportHandler {

    /**
     * App context
     */
    private Activity context;

    /**
     * User callback of authentication
     */
    private static ImportCallback importCallback;

    /**
     * User's token
     */
    private static String token;

    public AncestryImportHandler(Activity context){
        this.context = context;
    }

    /**
     * Start import data from Ancestry, execute user callback
     * @param viewImport import listener
     * @param importCallback user callback of import data
     */
    public void importData(View viewImport, String token, final ImportCallback importCallback){
        if (importCallback == null)
            throw new RuntimeException();
        this.importCallback = importCallback;
        this.token = token;

        viewImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AncestryImportDialog_.builder().build().show(context.getFragmentManager(), "dialog_ancestry_import");
            }
        });
    }

    public static String getToken(){
        return token;
    }

    public static ImportCallback getImportCallback(){
        return importCallback;
    }
}
