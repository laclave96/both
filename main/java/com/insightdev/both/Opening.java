package com.insightdev.both;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Opening extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private LottieAnimationView lottieAnimationView;
    private MotionLayout motionLayout, inputLayout;
    private EditText userEdit, passEdit, nameEdit, phoneEdit;
    private ImageView deleteName, deletePhone, deleteUser, togglePass, backButton;
    private TextView signUp, logIn, createBoth, forget, terms;
    private TextView signInGoogle, signInFacebook;
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private CircularProgressIndicator indicator;
    private boolean flag_hide_show = false, isKeyboardVisible = false;
    private int GOOGLE_SIGN_IN = 30;


    public static native String loginUrl();

    public static native String loginOrRegister();

    public static native String signUpUrl();

    public static native String pass();

    static {
        System.loadLibrary("native-lib");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        lottieAnimationView = findViewById(R.id.animation);
        //lottieAnimationView.setRenderMode(RenderMode.HARDWARE);
        motionLayout = findViewById(R.id.opening);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        userEdit = findViewById(R.id.userEdit);
        backButton = findViewById(R.id.backButton);
        passEdit = findViewById(R.id.passEdit);
        deleteName = findViewById(R.id.deleteName);
        deletePhone = findViewById(R.id.deletePhone);
        deleteUser = findViewById(R.id.deleteUser);
        togglePass = findViewById(R.id.togglePass);
        createBoth = findViewById(R.id.both);
        signInGoogle = findViewById(R.id.google);
        indicator = findViewById(R.id.cindicator);
        signInFacebook = findViewById(R.id.facebook);
        inputLayout = findViewById(R.id.inputEdit);
        signUp = findViewById(R.id.createAccButt);
        logIn = findViewById(R.id.loginButt);
        forget = findViewById(R.id.forget);
        terms = findViewById(R.id.terminos);
        indicator.hide();

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayout.transitionToState(R.id.create);
            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 & deleteName.getVisibility() == View.INVISIBLE) {

                    deleteName.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    deleteName.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & deleteName.getVisibility() == View.VISIBLE) {

                    deleteName.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off));
                    deleteName.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deleteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdit.setText("");
            }
        });

        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 & deletePhone.getVisibility() == View.INVISIBLE) {

                    deletePhone.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    deletePhone.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & deletePhone.getVisibility() == View.VISIBLE) {

                    deletePhone.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off));
                    deletePhone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deletePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEdit.setText("");
            }
        });

        userEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 & deleteUser.getVisibility() == View.INVISIBLE) {

                    deleteUser.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    deleteUser.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & deleteUser.getVisibility() == View.VISIBLE) {

                    deleteUser.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off));
                    deleteUser.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEdit.setText("");
            }
        });

        togglePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passEdit.requestFocus();
                if (flag_hide_show) {
                    passEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passEdit.setSelection(passEdit.length());
                    togglePass.setImageResource(R.drawable.ic_outline_visibility_24);
                    flag_hide_show = false;
                } else {
                    passEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passEdit.setSelection(passEdit.length());
                    togglePass.setImageResource(R.drawable.ic_outline_visibility_off_24);
                    flag_hide_show = true;
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {

                isKeyboardVisible = isVisible;
            }
        });

        customTextView(terms);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(!Tools.validateUser(userEdit) | !Tools.validatePass(passEdit)))
                    if (Tools.isConnected(Opening.this)) {
                        logIn.setEnabled(false);
                        signUp.setEnabled(false);
                        signUp.setClickable(false);
                        indicator.show();
                        login();
                    } else {
                        Toast.makeText(Opening.this, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    }

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logIn.getAlpha() == 1.0) {
                    inputLayout.transitionToEnd();
                    motionLayout.transitionToState(R.id.createTop);
                } else {
                    if (!(!Tools.validateName(nameEdit, false) | !Tools.validateUser(userEdit) | !Tools.validatePass(passEdit)))
                        if (Tools.isConnected(Opening.this)) {
                            new TermsDialog().showDialog(Opening.this, "both");
                        } else {
                            Toast.makeText(Opening.this, "Sin Conexión", Toast.LENGTH_SHORT).show();
                        }
                }

            }
        });


        signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TermsDialog().showDialog(Opening.this, "facebook");
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //loginOrRegister("jnievesimon@gmail.com", "Juan Daniel", Tools.getSha256("123asd"));
                new TermsDialog().showDialog(Opening.this, "google");
            }
        });

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isBlackList = Tools.isBlacklist(Opening.this);
                        if (isBlackList) {
                            Intent intent = new Intent(Opening.this, InfoActivity.class);
                            intent.putExtra("cause", "blacklist");
                            startActivity(intent);
                            AppHandler.cancelWorker(Opening.this);

                            if (Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
                                stopService(new Intent(Opening.this, MainService.class));
                            finish();
                            return;
                        }
                        if (Profile.person == null)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    motionLayout.transitionToEnd();
                                }
                            });
                        else {
                            Log.d("init_", "test");
                            AppHandler.runService(getApplicationContext());
                            Intent intent = new Intent(Opening.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recovery", "true");
                startActivity(new Intent(Opening.this, RecoverPass.class));
            }
        });
        AppHandler.setOpeningActivityOpen(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Tools.isConnected(getApplicationContext()))
                    AppHandler.loadConfig(Opening.this);
                Profile.Load(getApplicationContext());
                Profile.loadOrCreateRSAKeys(getApplicationContext());
                if (Profile.person != null) {
                    if (Tools.isConnected(getApplicationContext())) {
                        /*new Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!AppHandler.isStartingWorker()) {
                                    Log.d("Connection", "notStartingWorker");
                                    if (XMPPMessageServer.getConnection() == null) {
                                        EventBus.getDefault().post(new ReconnectWorkerEvent());
                                    } else if (!XMPPMessageServer.getConnection().isConnected()) {
                                        EventBus.getDefault().post(new ReconnectWorkerEvent());
                                    }
                                }

                            }
                        }, 10000);*/
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Tools.initConfig(Opening.this);
                            }
                        });
                        ContactsManager.loadContactDBDriver(getApplicationContext());
                        Conversations.loadDatabaseDrivers(getApplicationContext());
                        AppHandler.loadInternalContacts(getApplicationContext());
                        AppHandler.loadMessagesFromInternalDatabase();
                        if (Profile.isPremium(getApplicationContext()))
                            AppHandler.checkPremiumIsOver(getApplicationContext());
                    }
                } else {
                    setupFacebook();
                    setupGoogle();
                    AppHandler.createNotificationChannels(getApplicationContext());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lottieAnimationView.playAnimation();

                            }
                        }, 500);
                    }
                });


            }
        }).start();

        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        AppHandler.setOpeningActivityOpen(true);


    }

    @Override
    protected void onStop() {
        super.onStop();
        Tools.closeMainService(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppHandler.addWorkerAppNotification(getApplicationContext());
            }
        }).start();
        /*
        if (Tools.isMyServiceRunning(MainService.class, getApplicationContext())) {
            stopService(new Intent(Opening.this, MainService.class));
        }*/
    }

    @Override
    public void onBackPressed() {
        if (motionLayout.getCurrentState() == R.id.start)
            return;
        if (logIn.getAlpha() == 0) {
            inputLayout.transitionToStart();
            motionLayout.transitionToState(R.id.create);
        } else if (logIn.getAlpha() == 1 && motionLayout.getCurrentState() != R.id.end)
            motionLayout.transitionToState(R.id.end);
        else
            super.onBackPressed();
        if (isKeyboardVisible)
            KeyboardUtils.forceCloseKeyboard(getCurrentFocus());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHandler.setOpeningActivityOpen(false);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            loadGoogleProfile(result);
        }
    }


    void loadGoogleProfile(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            final String name = acct.getGivenName();
            final String email = acct.getEmail();
            final String id = acct.getId();
            loginOrRegister(email, name, Tools.getSha256(id));
        } else {
            indicator.hide();
            Toast.makeText(this, "Inicio Fallido ", Toast.LENGTH_LONG).show();


            Log.d("laSJdk", result.getStatus().toString());
        }
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken != null)
                loadFacebookProfile(currentAccessToken);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }

    void loadFacebookProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String id = object.getString("id");
                    String name = object.getString("first_name");
                    loginOrRegister(id + "@facebook.com", name, Tools.getSha256(id));
                } catch (JSONException e) {
                    indicator.hide();
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void setupGoogle() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

    }

    void setupFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                    }

                    @Override
                    public void onCancel() {
                        indicator.hide();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        indicator.hide();
                    }
                });
    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CRequest request = new CRequest(Opening.this, loginUrl(), 20);
                ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                pairs.add(new Pair<>(Tools.getString(R.string.username, getApplicationContext()), userEdit.getText().toString().trim() + "@both.com"));
                pairs.add(new Pair<>(Tools.getString(R.string.pass, getApplicationContext()), Tools.getSha256(passEdit.getText().toString().trim())));
                pairs.add(new Pair<>(Tools.getString(R.string.public_key, getApplicationContext()), Profile.getPublicKeyAsString()));
                pairs.add(new Pair<>(Tools.getString(R.string.uuid, getApplicationContext()), Tools.getUUiD()));
                pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), pass()));
                request.makeRequest(pairs);
                request.setOnResponseListener(new CRequest.OnResponseListener() {
                    @Override
                    public void onResponse(String response) {


                        response = response.trim();

                        String login = Tools.getValue(response, "login");

                        Tools.setConfig(Tools.getValue(response, Tools.getString(R.string.config, getApplicationContext())), getApplicationContext());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logIn.setEnabled(true);
                                signUp.setEnabled(true);
                            }
                        });
                        if (response.contentEquals(userEdit.getText().toString().trim() + "@both.com")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    passEdit.setText("");
                                    passEdit.setError("Contraseña incorrecta");
                                    indicator.hide();
                                }
                            });

                        } else if (response.contentEquals("blacklist")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indicator.hide();
                                    Toast.makeText(Opening.this, "Su cuenta ha sido cancelada", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (login.isEmpty()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userEdit.setText("");
                                    userEdit.setError("Nombre de usuario no existe");
                                    indicator.hide();
                                }
                            });

                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Profile.Load(login, Tools.getSha256(passEdit.getText().toString().trim()), getApplicationContext());
                                    String contacts = Tools.getValue(login, Tools.getString(R.string.contacts, getApplicationContext()));
                                    AppHandler.processAllContacts(contacts, Opening.this);

                                    int cant_new_likes = Integer.parseInt(Tools.getValue(login, "new_likes"));

                                    if (cant_new_likes > 0) {
                                        String title, body = "esperando por tí";

                                        if (cant_new_likes == 1)
                                            title = "Tienes un nuevo like...";
                                        else
                                            title = "Tienes " + cant_new_likes + " nuevos likes...";


                                        AppNotifications.newNotify(getApplicationContext(), null, title, body);
                                    }


                                    // AppHandler.addWorkerConnection(getApplicationContext());
                                    /*if (!Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
                                        startService(new Intent(Opening.this, MainService.class));*/
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(Opening.this, MainActivity.class));
                                            finishAfterTransition();
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
                                logIn.setEnabled(true);
                                signUp.setEnabled(true);
                                indicator.hide();
                            }
                        });
                        if (error.contains("TimeoutError"))
                            Toast.makeText(Opening.this, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Opening.this, "Por favor revise su conectividad a internet", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }).start();

    }

    private void signUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CRequest request = new CRequest(Opening.this, signUpUrl(), 15);

                String phone = phoneEdit.getText().toString().trim();

                ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                pairs.add(new Pair<>(Tools.getString(R.string.name, getApplicationContext()), nameEdit.getText().toString().trim()));
                pairs.add(new Pair<>(Tools.getString(R.string.phone, getApplicationContext()), phone.isEmpty() ? "-" : phone));
                pairs.add(new Pair<>(Tools.getString(R.string.username, getApplicationContext()), userEdit.getText().toString().trim() + "@both.com"));
                pairs.add(new Pair<>(Tools.getString(R.string.pass, getApplicationContext()), Tools.getSha256(passEdit.getText().toString().trim())));
                pairs.add(new Pair<>(Tools.getString(R.string.public_key, getApplicationContext()), Profile.getPublicKeyAsString()));
                pairs.add(new Pair<>(Tools.getString(R.string.uuid, getApplicationContext()), Tools.getUUiD()));
                pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), pass()));
                request.makeRequest(pairs);
                request.setOnResponseListener(new CRequest.OnResponseListener() {
                    @Override
                    public void onResponse(String response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signUp.setEnabled(true);
                            }
                        });
                        Log.d("SignUpResponse", "response" + response);
                        response = response.trim();
                        Tools.setConfig(Tools.getValue(response, Tools.getString(R.string.config, getApplicationContext())), getApplicationContext());
                        if (response.contentEquals("blacklist")) {
                            String finalResponse = response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indicator.hide();
                                    ProToast.makeText(getApplicationContext(), R.drawable.ic_info, finalResponse, Toast.LENGTH_SHORT);
                                }
                            });
                        } else if (response.contentEquals("Nombre de usuario ya existe")) {

                            ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Nombre de usuario ya existe", Toast.LENGTH_SHORT);
                            String finalResponse1 = response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indicator.hide();
                                    userEdit.setError(finalResponse1);
                                }
                            });
                        } else if (response.contentEquals("Error Inesperado")) {
                            String finalResponse2 = response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indicator.hide();
                                    //Toast.makeText(Opening.this, finalResponse2, Toast.LENGTH_SHORT).show();
                                    ProToast.makeText(getApplicationContext(), R.drawable.ic_info, finalResponse2, Toast.LENGTH_SHORT);
                                }
                            });
                        } else {
                            pairs.add(new Pair<>(Tools.getString(R.string.server, getApplicationContext()), Tools.getValue(response, Tools.getString(R.string.server, getApplicationContext()))));
                            pairs.add(new Pair<>(Tools.getString(R.string.id, getApplicationContext()), Tools.getValue(response, Tools.getString(R.string.id, getApplicationContext()))));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Profile.Load(Tools.getJson(pairs), Tools.getSha256(passEdit.getText().toString().trim()), getApplicationContext());
                                    //AppHandler.addWorkerConnection(getApplicationContext());
                                   /* if (!Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
                                        startService(new Intent(Opening.this, MainService.class));*/
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(Opening.this, MainActivity.class));
                                            finishAfterTransition();
                                        }
                                    });
                                }
                            }).start();

                        }


                    }

                    @Override
                    public void onError(String error) {

                        Log.d("blskdalsd", error);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                indicator.hide();
                                signUp.setEnabled(true);
                            }
                        });
                        Log.d("Error_", error);
                        if (error.contains("TimeoutError"))
                            Toast.makeText(Opening.this, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Opening.this, "Por favor revise su conectividad a internet", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();

    }

    private void loginOrRegister(String username, String name, String pass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Tools.isConnected(Opening.this)) {
                    CRequest request = new CRequest(Opening.this, loginOrRegister(), 20);
                    ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                    pairs.add(new Pair<>(Tools.getString(R.string.username, getApplicationContext()), username));
                    pairs.add(new Pair<>(Tools.getString(R.string.name, getApplicationContext()), name));
                    pairs.add(new Pair<>(Tools.getString(R.string.pass, getApplicationContext()), pass));
                    pairs.add(new Pair<>(Tools.getString(R.string.public_key, getApplicationContext()), Profile.getPublicKeyAsString()));
                    pairs.add(new Pair<>(Tools.getString(R.string.uuid, getApplicationContext()), Tools.getUUiD()));
                    pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), pass()));
                    request.makeRequest(pairs);
                    request.setOnResponseListener(new CRequest.OnResponseListener() {
                        @Override
                        public void onResponse(String response) {
                            response = response.trim();
                            String login = Tools.getValue(response, "login");
                            Tools.setConfig(Tools.getValue(response, Tools.getString(R.string.config, getApplicationContext())), getApplicationContext());

                            if (response.contentEquals("blacklist")) {
                                String finalResponse = response;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        indicator.hide();
                                        Toast.makeText(Opening.this, finalResponse, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (response.contentEquals("Error Inesperado")) {
                                String finalResponse1 = response;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        indicator.hide();
                                        Toast.makeText(Opening.this, finalResponse1, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (!login.isEmpty()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Profile.Load(login, pass, getApplicationContext());
                                        String contacts = Tools.getValue(login, Tools.getString(R.string.contacts, getApplicationContext()));

                                        int cant_new_likes = Integer.parseInt(Tools.getValue(login, "new_likes"));

                                        if (cant_new_likes > 0) {
                                            String title, body = "esperando por tí";

                                            if (cant_new_likes == 1)
                                                title = "Tienes un nuevo like...";
                                            else
                                                title = "Tienes " + cant_new_likes + " nuevos likes...";


                                            AppNotifications.newNotify(getApplicationContext(), null, title, body);
                                        }

                                        AppHandler.processAllContacts(contacts, Opening.this);
                                        //AppHandler.addWorkerConnection(getApplicationContext());
                                      /*  if (!Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
                                            startService(new Intent(Opening.this, MainService.class));*/
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(Opening.this, MainActivity.class));
                                                finishAfterTransition();
                                            }
                                        });
                                    }
                                }).start();

                            } else {
                                pairs.add(new Pair<>(Tools.getString(R.string.server, getApplicationContext()), Tools.getValue(response, Tools.getString(R.string.server, getApplicationContext()))));
                                pairs.add(new Pair<>(Tools.getString(R.string.id, getApplicationContext()), Tools.getValue(response, Tools.getString(R.string.id, getApplicationContext()))));
                                Profile.Load(Tools.getJson(pairs), pass, getApplicationContext());
                                startActivity(new Intent(Opening.this, MainActivity.class));
                                finishAfterTransition();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indicator.hide();
                                }
                            });
                            if (error.contains("TimeoutError"))
                                Toast.makeText(Opening.this, "Lo sentimos por la demora, intente nuevamente", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(Opening.this, "Por favor revise su conectividad a internet", Toast.LENGTH_LONG).show();

                        }
                    });
                } else {
                    indicator.hide();
                    Toast.makeText(Opening.this, "Sin Conexion", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceShutDown(ServiceShutDownEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void termsAccepted(TermsAcceptedEvent event) {
        indicator.show();
        if (event.sourceSignUp().contentEquals("both")) {
            signUp.setEnabled(false);
            signUp();
            return;
        }
        if (event.sourceSignUp().contentEquals("facebook")) {
            LoginManager.getInstance().logInWithReadPermissions(Opening.this, Arrays.asList("public_profile"));
            return;
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

    }

    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "");
        spanTxt.append(getString(R.string.politica_de_privacidad));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Tools.getPrivacyUrl(Opening.this).isEmpty()) {
                    if (!Tools.isConnected(Opening.this)) {
                        Toast.makeText(Opening.this, getString(R.string.sin_conexion), Toast.LENGTH_SHORT).show();
                    } else {
                        AppHandler.loadConfig(Opening.this);
                        Toast.makeText(Opening.this, getString(R.string.sincronizando_datos), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.getPrivacyUrl(Opening.this))));

            }
        }, spanTxt.length() - getString(R.string.politica_de_privacidad).length(), spanTxt.length(), 0);
        spanTxt.append(" · ");
        //spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 32, spanTxt.length(), 0);
        spanTxt.append(getString(R.string.terminos_de_uso));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Tools.getTermsUrl(Opening.this).isEmpty()) {
                    if (!Tools.isConnected(Opening.this)) {
                        Toast.makeText(Opening.this, getString(R.string.sin_conexion), Toast.LENGTH_SHORT).show();
                    } else {
                        AppHandler.loadConfig(Opening.this);
                        Toast.makeText(Opening.this, getString(R.string.sincronizando_datos), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.getTermsUrl(Opening.this))));

            }
        }, spanTxt.length() - getString(R.string.terminos_de_uso).length(), spanTxt.length(), 0);

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
        view.setTextColor(getResources().getColor(R.color.gray));
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