package com.sequencing.androidoauth.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sequencing.androidoauth.core.importdata.Import23AndMeHandler;
import com.sequencing.androidoauth.core.importdata.Response23andMe;
import com.sequencing.androidoauth.core.importdata.RestInterface;
import com.sequencing.androidoauth.core.importdata.entities.AuthorizationBody;
import com.sequencing.androidoauth.core.importdata.entities.Import23andMeBody;
import com.sequencing.androidoauth.helper.ValidationHelper;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

/**
 * Created by omazurova on 3/13/2017.
 */
@EFragment(resName = "dialog_23andme_import")
public class Dialog23andMe extends DialogFragment {

    @RestService
    RestInterface restInterface;

    @ViewById(resName = "rlContent")
    RelativeLayout rlContent;

    @ViewById(resName = "etEnterEmail")
    EditText etEmail;

    @ViewById(resName = "etEnterPassword")
    EditText etPassword;

    @ViewById(resName = "tvInvalidEmail")
    TextView tvInvalidEmail;

    @ViewById(resName = "btnSignIn")
    Button signIn;

    @ViewById(resName = "spinner")
    ProgressBar spinner;

    @ViewById(resName = "rlSeqQuestionBlock")
    RelativeLayout rlSeqQuestionBlock;

    @ViewById(resName = "etEnterAnswer")
    EditText etAnswer;

    @ViewById(resName = "tvSeqQuestion")
    TextView tvSeqQuestion;

    @ViewById(resName = "btnSubmitAnswer")
    Button btnAnswer;

    private String sessionId;
    private String userAnswer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    @AfterTextChange(resName = "etEnterEmail")
    public void onEmailChanged() {
        if (!ValidationHelper.isValidEmail(etEmail.getText().toString().trim())) {
            tvInvalidEmail.setVisibility(View.VISIBLE);
        } else {
            tvInvalidEmail.setVisibility(View.GONE);
        }
    }

    @Click(resName = "btnSignIn")
    public void onSignInClick() {
        String email = etEmail.getText().toString().trim();
        if (!ValidationHelper.isValidEmail(email)) {
            tvInvalidEmail.setVisibility(View.VISIBLE);
        } else {
            tvInvalidEmail.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            signIn();
        }
    }

    @Background
    protected void signIn() {
        String token = Import23AndMeHandler.getToken();
        token = String.format("Bearer %s", token);
        AuthorizationBody andMeBody = new AuthorizationBody();
        andMeBody.Login = etEmail.getText().toString().trim();
        andMeBody.Password = etPassword.getText().toString().trim();

        if (Build.VERSION.SDK_INT > 13) {
            restInterface.setHeader("Connection", "close");
        }
        restInterface.setHeader("Authorization", token);
        restInterface.setHeader("Content-type", "application/json");
        try {
            Response23andMe response23andMe = restInterface.authorization23andMe(andMeBody);
            if (response23andMe.statusCode == 0 || response23andMe.statusCode == 3) {
                if (response23andMe.securityQuestion != null) {
                    sessionId = response23andMe.sessionId;
                    showQuestion(response23andMe.securityQuestion);
                } else {
                    showMessage("Import files is started!");
                    Import23AndMeHandler.getImportCallback().onSuccessImportStarted("Import files is started!");
                    dismiss();
                }
            } else if (response23andMe.statusCode == 1) {
                showMessage("Invalid credentials.");
                Import23AndMeHandler.getImportCallback().onFailedImport("Invalid credentials.");
            } else if(response23andMe.statusCode == 2){
                if (response23andMe.securityQuestion != null) {
                    sessionId = response23andMe.sessionId;
                    showQuestion(response23andMe.securityQuestion);
                }
            } else {
                showMessage("Import files can't be started.");
                Import23AndMeHandler.getImportCallback().onFailedImport("Import files can't be started.");
            }
        } catch (Exception e) {
            showMessage(e.toString());
            Import23AndMeHandler.getImportCallback().onFailedImport(e.toString());
        }
    }

    @Click(resName = "btnSubmitAnswer")
    public void onAnswerClick() {
        String email = etEmail.getText().toString().trim();
        if (!etAnswer.getText().toString().trim().equals("")) {
            userAnswer = etAnswer.getText().toString().trim();
            spinner.setVisibility(View.VISIBLE);
            startImportData();
        }
    }

    @Background
    protected void startImportData() {
        String token = Import23AndMeHandler.getToken();
        Import23andMeBody import23andMeBody = new Import23andMeBody();
        import23andMeBody.SeqAnswer = userAnswer;
        import23andMeBody.SessionId = sessionId;
        restInterface.setHeader("Authorization", token);
        restInterface.setHeader("Content-type", "application/json");
        try{
            Response23andMe response23andMe = restInterface.startImport23andMe(import23andMeBody);
            if (response23andMe.statusCode == 0) {
                showMessage("Import files is started!");
                Import23AndMeHandler.getImportCallback().onSuccessImportStarted("Import files is started!");
                dismiss();
            } else if (response23andMe.statusCode == 1 || response23andMe.statusCode == 2 || response23andMe.statusCode == 3) {
                showMessage("Error during start import data. Please, try again.");
                Import23AndMeHandler.getImportCallback().onFailedImport("Error during start import data. Please, try again.");
                hideQuestionBlock();
            }
        }catch (Exception e){
            Import23AndMeHandler.getImportCallback().onFailedImport(e.toString());
            showMessage(e.toString());
        }
    }

    @UiThread
    protected void showQuestion(String question) {
        spinner.setVisibility(View.GONE);
        rlContent.setVisibility(View.GONE);
        rlSeqQuestionBlock.setVisibility(View.VISIBLE);
        tvSeqQuestion.setText(question);
    }

    @UiThread
    protected void showMessage(String message) {
        if(spinner != null) {
            spinner.setVisibility(View.GONE);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    @UiThread
    protected void hideQuestionBlock() {
        rlContent.setVisibility(View.VISIBLE);
        rlSeqQuestionBlock.setVisibility(View.GONE);
    }
}
