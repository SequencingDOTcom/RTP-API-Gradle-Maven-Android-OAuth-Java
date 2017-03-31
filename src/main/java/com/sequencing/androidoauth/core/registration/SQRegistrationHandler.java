package com.sequencing.androidoauth.core.registration;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sequencing.androidoauth.R;
import com.sequencing.androidoauth.helper.HttpHelper;
import com.sequencing.androidoauth.helper.ValidationHelper;
import com.sequencing.androidoauth.model.RegistrationEntity;
import com.sequencing.androidoauth.model.ResetEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class determines basic action in relation to Sequencing.com registration reset
 */
public class SQRegistrationHandler {

    /**
     * Activity
     */
    Activity context;

    /**
     * User callback of registration reset account
     */
    private static SQRegistrationCallback registrationCallback;

    private Dialog dialogRegistrationReset;
    private EditText etEmail;
    private ProgressBar spinner;
    private TextView invalidTextEmail;
    private Button btCreateAccount;
    private Button btReset;
    private Button btCancel;
    private String clientSecret;


    public SQRegistrationHandler(Activity context) {
        this.context = context;
    }

    /**
     * Registration reset user and execute user callback
     *
     * @param btnRegisterReset     registration reset button
     * @param clientSecret         client secret
     * @param registrationCallback user callback of registration new account reset password
     */
    public void registerResetAccount(Button btnRegisterReset, String clientSecret, SQRegistrationCallback registrationCallback) {
        this.clientSecret = clientSecret;
        this.registrationCallback = registrationCallback;
        btnRegisterReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRegistrationDialog();
            }
        });
    }

    /**
     * Show registration reset dialog
     */
    private void initRegistrationDialog() {
        dialogRegistrationReset = new Dialog(context);
        dialogRegistrationReset.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRegistrationReset.setCancelable(false);
        dialogRegistrationReset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogRegistrationReset.setContentView(R.layout.registration_reset_dialog);
        dialogRegistrationReset.show();

        etEmail = (EditText) dialogRegistrationReset.findViewById(R.id.etEnterEmail);
        spinner = (ProgressBar) dialogRegistrationReset.findViewById(R.id.spinner);
        invalidTextEmail = (TextView) dialogRegistrationReset.findViewById(R.id.tvInvalidEmail);
        btCreateAccount = (Button) dialogRegistrationReset.findViewById(R.id.btnRegister);
        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterAccount();
            }
        });
        btReset = (Button) dialogRegistrationReset.findViewById(R.id.btnReset);
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetPassword();
            }
        });
        btCancel = (Button) dialogRegistrationReset.findViewById(R.id.btnCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancel();
            }
        });
    }

    /**
     * Validation email, start registration
     */
    private void onRegisterAccount() {
        String email = etEmail.getText().toString().trim();
        if (!ValidationHelper.isValidEmail(email)) {
            invalidTextEmail.setVisibility(View.VISIBLE);
        } else {
            invalidTextEmail.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            registerPost();
        }
    }

    protected void registerPost() {
        String email = etEmail.getText().toString().trim();
        final Map<String, String> params = new HashMap<>(2);
        params.put("email", email);
        params.put("client_id", clientSecret);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    String response = HttpHelper.doHttpPost("https://sequencing.com/indexApi.php?q=sequencing/public/webservice/user/seq_register.json", null, params);
                    RegistrationEntity responseEntity = new Gson().fromJson(response, RegistrationEntity.class);
                    if (responseEntity != null) {
                        checkRegistrationStatus(responseEntity);
                    }
                } catch (IllegalArgumentException e) {
                    checkRegistrationStatus(null);
                } catch (Exception e) {
                    showResultDialog("Error", "Error processing registration request on sequencing.com. This is temporary error. Please re-try later", 3);
                }
            }
        };

        service.submit(run);
    }

    /**
     * Validation email, start reset password
     */
    private void onResetPassword() {
        String email = etEmail.getText().toString().trim();
        if (!ValidationHelper.isValidEmail(email)) {
            invalidTextEmail.setVisibility(View.VISIBLE);
        } else {
            invalidTextEmail.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            resetPost();
        }
    }

    /**
     * Reset password
     */
    protected void resetPost() {
        String email = etEmail.getText().toString().trim();

        final Map<String, String> params = new HashMap<>(2);
        params.put("email", email);
        params.put("client_id", clientSecret);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    String response = HttpHelper.doHttpPost("https://sequencing.com/indexApi.php?q=sequencing/public/webservice/user/seq_new_pass.json", null, params);
                    ResetEntity responseEntity = new Gson().fromJson(response, ResetEntity.class);
                    if (responseEntity != null) {
                        checkResetStatus(responseEntity);
                    }
                } catch (Exception e) {
                    showResultDialog("Error", "Error processing registration request on sequencing.com. This is temporary error. Please re-try later", 3);
                }
            }
        };

        service.submit(run);
    }

    private void onCancel() {
        if (dialogRegistrationReset != null) {
            dialogRegistrationReset.dismiss();
        }
    }

    /**
     * Check registration response
     *
     * @param registrationEntity registration entity
     */
    protected void checkRegistrationStatus(final RegistrationEntity registrationEntity) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                String status = null;
                if (registrationEntity != null) {
                    status = registrationEntity.status;
                    int statusCode = Integer.parseInt(status);
                    switch (statusCode) {
                        case 0:
                            showResultDialog("Registration", "Please check your mail box and follow instruction to activate your account.", statusCode);
                            break;
                        case 1:
                            int errorCode = Integer.parseInt(registrationEntity.errorCode);
                            if (errorCode == 1 || errorCode == 2) {
                                if (registrationEntity.errorRegisterMessage.errorMail != null) {
                                    showResultDialog("Registration error", registrationEntity.errorRegisterMessage.errorMail, statusCode);
                                }
                            } else if (errorCode == 3) {
                                showResultDialog("Registration error", "Error processing registration request on sequencing.com. Client secret not found.", statusCode);
                            }
                            break;
                    }
                } else {
                    showResultDialog("Registration error", "Error processing registration request on sequencing.com. Client secret not found.", 1);
                }
            }
        });

    }

    /**
     * Check reset password response
     *
     * @param resetEntity reset entity
     */
    protected void checkResetStatus(final ResetEntity resetEntity) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                String status = null;
                if (resetEntity != null) {
                    status = resetEntity.status;
                }
                int statusCode = Integer.parseInt(status);
                switch (statusCode) {
                    case 0:
                        showResultDialog("Reset password", "Please check your mail box and follow instruction to reset your password.", statusCode);
                        break;
                    case 1:
                        int errorCode = Integer.parseInt(resetEntity.errorCode);
                        if (errorCode == 1 || errorCode == 2) {
                            if (resetEntity.errorMessage != null) {
                                showResultDialog("Reset password error", resetEntity.errorMessage, statusCode);
                            } else {
                                showResultDialog("Reset password error", "Error processing reset password on sequencing.com. This is temporary error. Please re-try later", statusCode);
                            }
                        } else if (errorCode == 3) {
                            showResultDialog("Reset password error", "Error processing reset password on sequencing.com. This is temporary error. Please re-try later", statusCode);
                        }
                        break;
                }
            }
        });

    }

    /**
     * Show result registration reset dialog
     *
     * @param title      title dialog
     * @param message    message
     * @param statusCode statusCode of response
     */
    protected void showResultDialog(String title, final String message, final int statusCode) {
        spinner.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (statusCode == 0) {
                    if(dialogRegistrationReset != null){
                        dialogRegistrationReset.dismiss();
                    }
                    registrationCallback.onSuccessRegistration(message);
                } else {
                    registrationCallback.onFailedRegistration(message);
                }
                dialog.dismiss();
            }
        });
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
