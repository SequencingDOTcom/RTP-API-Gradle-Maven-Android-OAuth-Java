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

import com.sequencing.androidoauth.R;
import com.sequencing.androidoauth.core.OAuth2Parameters;
import com.sequencing.androidoauth.core.importdata.AncestryImportHandler;
import com.sequencing.androidoauth.core.importdata.Import23AndMeHandler;
import com.sequencing.androidoauth.core.importdata.RestInterface;
import com.sequencing.androidoauth.core.importdata.entities.AuthorizationBody;
import com.sequencing.androidoauth.core.importdata.entities.ImportAncestry;
import com.sequencing.androidoauth.core.importdata.entities.ResponseAncestry;
import com.sequencing.androidoauth.helper.ValidationHelper;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;

@EFragment(resName = "dialog_ancestry_import")
public class AncestryImportDialog extends DialogFragment {

    @RestService
    RestInterface restInterface;

    @ViewById(resName = "rlContentAncestry")
    RelativeLayout rlContentAncestry;

    @ViewById(resName = "etEnterEmail")
    EditText etEmail;

    @ViewById(resName = "etEnterPassword")
    EditText etPassword;

    @ViewById(resName = "spinner")
    ProgressBar spinner;

    @ViewById(resName = "tvInvalidEmail")
    TextView tvInvalidEmail;

    @ViewById(resName = "btnSignIn")
    Button btnSignIn;

    @ViewById(resName = "rlUrlForm")
    RelativeLayout rlUrlForm;

    @ViewById(resName = "tvConfirmationTitle")
    TextView tvConfirmationTitle;

    @ViewById(resName = "etConfirmationUrl")
    EditText etConfirmationUrl;

    @ViewById(resName = "btnSendLink")
    Button btnSendLink;

    @ViewById(resName = "btnCancel")
    Button btnCancel;

    private static String sessionId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    @AfterViews
    public void initViews(){
        if(sessionId != null){
            rlContentAncestry.setVisibility(View.GONE);
            rlUrlForm.setVisibility(View.VISIBLE);
        } else {
            rlContentAncestry.setVisibility(View.VISIBLE);
            rlUrlForm.setVisibility(View.GONE);
        }
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
    protected void signIn(){
        String token = AncestryImportHandler.getToken();// OAuth2Parameters.getInstance().getOauth().getToken().getAccessToken();
        token = String.format("Bearer %s", token);
        AuthorizationBody andMeBody = new AuthorizationBody();
        andMeBody.Login = etEmail.getText().toString().trim();
        andMeBody.Password = etPassword.getText().toString().trim();

        setConnectionTimeOut();
        if (Build.VERSION.SDK_INT > 13) {
            restInterface.setHeader("Connection", "close");
        }
        restInterface.setHeader("Authorization", token);
        restInterface.setHeader("Content-type", "application/json");
        try {
            ResponseAncestry responseAncestry = restInterface.ancestryAuthorization(andMeBody);

            if(responseAncestry.statusCode == 0){
                sessionId = responseAncestry.sessionId;
                showConfirmationBlock();
            } else if(responseAncestry.statusCode == 1) {
                hideProgressBar("Invalid credentials.");
                AncestryImportHandler.getImportCallback().onFailedImport("Invalid credentials.");
            } else if(responseAncestry.statusCode == 2){
                hideProgressBar("Server error. Please try again.");
                AncestryImportHandler.getImportCallback().onFailedImport("Invalid credentials.");
            }
        }catch (Exception e){
            hideProgressBar(e.toString());
            AncestryImportHandler.getImportCallback().onFailedImport(e.toString());
        }
    }

    private void setConnectionTimeOut() {
        ClientHttpRequestFactory httpFactory = restInterface.getRestTemplate().getRequestFactory();
        if (httpFactory != null) {
            if (httpFactory instanceof SimpleClientHttpRequestFactory) {
                ((SimpleClientHttpRequestFactory) httpFactory).setConnectTimeout(600 * 1000);
                ((SimpleClientHttpRequestFactory) httpFactory).setReadTimeout(600 * 1000);
            } else if (httpFactory instanceof HttpComponentsClientHttpRequestFactory) {
                ((HttpComponentsClientHttpRequestFactory) httpFactory).setConnectTimeout(600 * 1000);
                ((HttpComponentsClientHttpRequestFactory) httpFactory).setReadTimeout(600 * 1000);
            }
        }
    }

    @UiThread
    protected void showConfirmationBlock(){
        spinner.setVisibility(View.GONE);
        String startTitle = getResources().getString(R.string.confirmation_text_title);
        tvConfirmationTitle.setText(startTitle + " " + etEmail.getText().toString());
        rlUrlForm.setVisibility(View.VISIBLE);
        rlContentAncestry.setVisibility(View.GONE);
    }

    @Click(resName = "btnSendLink")
    public void onConfirmationSend(){
        if(!etConfirmationUrl.getText().toString().equals("")){
            spinner.setVisibility(View.VISIBLE);
            sendConfirmLink();
        }
    }

    @Background
    protected void sendConfirmLink(){
        String token = AncestryImportHandler.getToken();
        restInterface.setHeader("Authorization", token);
        restInterface.setHeader("Content-type", "application/json");

        ImportAncestry importAncestry = new ImportAncestry();
        importAncestry.SessionId = sessionId;
        importAncestry.Url = etConfirmationUrl.getText().toString().trim();
        try {
            ResponseAncestry response23andMe = restInterface.startAncestryImport(importAncestry);
            if(response23andMe.statusCode == 0){
                hideProgressBar(getResources().getString(R.string.success_import));
                AncestryImportHandler.getImportCallback().onSuccessImportStarted(getResources().getString(R.string.success_import));
                sessionId = null;
                dismiss();
            } else if(response23andMe.statusCode == 1){
                hideProgressBar("Invalid data. Please, try again");
                AncestryImportHandler.getImportCallback().onFailedImport("Invalid data. Please, try again");
            } else if(response23andMe.statusCode == 2){
                hideProgressBar("Server error. Please, try again");
                AncestryImportHandler.getImportCallback().onFailedImport("Server error. Please, try again");
            }
        } catch (RestClientException e){
            hideProgressBar(e.toString());
            AncestryImportHandler.getImportCallback().onFailedImport(e.toString());
        }
    }

    @Click(resName = "btnCancel")
    public void onCancelClick(){
        spinner.setVisibility(View.GONE);
        sessionId = null;
        dismiss();
    }

    @UiThread
    protected void hideProgressBar(String message){
        if(spinner != null){
            spinner.setVisibility(View.GONE);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
