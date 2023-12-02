package com.insightdev.both;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.IntentCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class RecoverPass extends AppCompatActivity {

    OtpTextView otpTextView;
    EditText email, pass;
    MotionLayout motionLayout;
    ImageView backB, toggle;
    TextView details;
    Button button;
    int code;
    String username;
    boolean flag_hide_show = false, isKeyboardVisible = false, matchOtp = false;
    LoadingDialog loadingDialog;

    public static native String recover();

    public static native String setPass();

    public static native String pass();

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);

        loadingDialog = new LoadingDialog();
        otpTextView = findViewById(R.id.editPin);
        motionLayout = findViewById(R.id.infoLayout);
        details = findViewById(R.id.details);
        email = findViewById(R.id.emailEdit);
        pass = findViewById(R.id.passEdit);
        toggle = findViewById(R.id.togglePass);
        backB = findViewById(R.id.backButton);
        button = findViewById(R.id.actionButton);

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass.requestFocus();
                if (flag_hide_show) {
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(pass.length());
                    toggle.setImageResource(R.drawable.ic_outline_visibility_24);
                    flag_hide_show = false;
                } else {
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(pass.length());
                    toggle.setImageResource(R.drawable.ic_outline_visibility_off_24);
                    flag_hide_show = true;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isConnected(RecoverPass.this)) {
                    Toast.makeText(RecoverPass.this, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (matchOtp) {
                    if (Tools.validatePass(pass)) {
                        loadingDialog.showDialog(RecoverPass.this, "Cargando");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CRequest request = new CRequest(RecoverPass.this, setPass(), 20);
                                ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                                pairs.add(new Pair<>(Tools.getString(R.string.username, getApplicationContext()), username));
                                pairs.add(new Pair<>(Tools.getString(R.string.pass, getApplicationContext()), Tools.getSha256(pass.getText().toString().trim())));
                                pairs.add(new Pair<>(Tools.getString(R.string.public_key, getApplicationContext()), Profile.getPublicKeyAsString()));
                                pairs.add(new Pair<>(Tools.getString(R.string.uuid, getApplicationContext()), Tools.getUUiD()));
                                pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), pass()));
                                pairs.add(new Pair<>("email", email.getText().toString().trim()));
                                request.makeRequest(pairs);
                                request.setOnResponseListener(new CRequest.OnResponseListener() {
                                    @Override
                                    public void onResponse(String response) {

                                        response = response.trim();
                                        Log.d("Login_", "log" + response);
                                        String login = Tools.getValue(response, "login");
                                        if (login.isEmpty()) {
                                            String finalResponse = response;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadingDialog.dismiss();
                                                    Toast.makeText(RecoverPass.this, finalResponse, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        Tools.setConfig(Tools.getValue(response, Tools.getString(R.string.config, getApplicationContext())), getApplicationContext());

                                        if (response.contentEquals("blacklist")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadingDialog.dismiss();
                                                    Toast.makeText(RecoverPass.this, "Su cuenta ha sido cancelada", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Profile.Load(login, Tools.getSha256(pass.getText().toString().trim()), getApplicationContext());
                                                    String contacts = Tools.getValue(login, Tools.getString(R.string.contacts, getApplicationContext()));
                                                    AppHandler.processAllContacts(contacts, RecoverPass.this);
                                                    int cant_new_likes = Integer.parseInt(Tools.getValue(login, "new_likes"));

                                                    if (cant_new_likes > 0) {
                                                        String title, body = "esperando por tí";

                                                        if (cant_new_likes == 1)
                                                            title = "Tienes un nuevo like...";
                                                        else
                                                            title = "Tienes " + cant_new_likes + " nuevos likes...";


                                                        AppNotifications.newNotify(getApplicationContext(), null, title, body);
                                                    }


                                                    if (!Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
                                                        startService(new Intent(RecoverPass.this, MainService.class));
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(RecoverPass.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }).start();

                                        }

                                    }

                                    @Override
                                    public void onError(String error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingDialog.dismiss();
                                            }
                                        });
                                        if (error.contains("TimeoutError"))
                                            Toast.makeText(RecoverPass.this, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(RecoverPass.this, "Por favor revise su conectividad a internet", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }).start();
                    }
                } else {
                    if (Tools.validateEmail(email, false)) {
                        loadingDialog.showDialog(RecoverPass.this, "Cargando");
                        code = Tools.generateRandom(10000, 99999);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CRequest request = new CRequest(RecoverPass.this, recover(), 15);
                                ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                                pairs.add(new Pair<>(Tools.getString(R.string.auth, RecoverPass.this), pass()));
                                pairs.add(new Pair<>("email", email.getText().toString().trim()));
                                pairs.add(new Pair<>("code", code + ""));
                                request.makeRequest(pairs);
                                request.setOnResponseListener(new CRequest.OnResponseListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("ResponseReason", "res" + response);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingDialog.dismiss();
                                            }
                                        });
                                        response = response.trim();
                                        if (response.contains("username")) {
                                            username = Tools.getValue(response, "username");
                                            motionLayout.transitionToEnd();
                                            details.setText("Escriba el código que le acabamos de enviar a su correo.");
                                            Toast.makeText(RecoverPass.this, Tools.getValue(response, "message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RecoverPass.this, response, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingDialog.dismiss();
                                            }
                                        });
                                        EventBus.getDefault().post(new LoadingDismissEvent());
                                        if (error.contains("TimeoutError"))
                                            Toast.makeText(RecoverPass.this, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(RecoverPass.this, "Por favor revise su conexión a internet", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }).start();

                    }
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isKeyboardVisible = isVisible;
            }
        });


        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                Log.d("recovery", "otp");
                if (otp.contentEquals(code + "")) {
                    Log.d("recovery", "otpmatch");
                    if (!Tools.isConnected(RecoverPass.this)) {
                        Toast.makeText(RecoverPass.this, "Sin Conexión", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    matchOtp = true;
                    details.setText("Ingrese una nueva contraseña.");
                    motionLayout.transitionToState(R.id.putPass);
                    button.setText("Continuar");
                    pass.requestFocus();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (motionLayout.getCurrentState() == R.id.end) {
            motionLayout.transitionToStart();
            details.setText("Ingrese la dirección de correo que especificó en su perfil.");
        } else
            super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                if (isKeyboardVisible)
                    KeyboardUtils.forceCloseKeyboard(getCurrentFocus());
            }
        }
        return ret;
    }
}