package com.sequencing.androidoauth.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.sequencing.androidoauth.fragments.AncestryImportDialog;
import com.sequencing.androidoauth.fragments.Dialog23andMe;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by omazurova on 3/13/2017.
 */

@EActivity(resName = "activity_import_data")
public class ImportDataActivity extends AppCompatActivity {

    @ViewById(resName = "btnAncestry")
    ImageButton btnAncestry;

    @ViewById(resName = "btn23")
    ImageButton btn23;

    @Click(resName = "btnAncestry")
    protected void onAncestryImportClick(){
        AncestryImportDialog ancestryImportDialog = new AncestryImportDialog();
        ancestryImportDialog.show(getFragmentManager(), "AncestryDialog");

    }

    @Click(resName = "btn23")
    protected void on23AndMeImportClick(){
        Dialog23andMe dialog23andMe = new Dialog23andMe();
        dialog23andMe.show(getFragmentManager(), "Dialog23andMe");

    }
}
