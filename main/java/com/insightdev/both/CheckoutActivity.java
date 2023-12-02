package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.facebook.common.internal.ImmutableList;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import com.insightdev.both.viewmodels.StripeCardViewModel;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardBrand;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardBrandView;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardNumberEditText;
import com.stripe.android.view.CvcEditText;
import com.stripe.android.view.ExpiryDateEditText;
import com.stripe.android.view.StripeEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;

@SuppressWarnings("KotlinInternalInJava")
public class CheckoutActivity extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    private static final String BACKEND_URL = "http://192.168.1.6:4242/"; //4242 is port mentioned in server i.e index.js

    private static final double CARD_SAVINGS = 0.07;

    EditText amountText;
    CardNumberEditText cardEditText;
    CardBrandView cardBrandIcon;

    Map<String, String> non_decimals_currency = createMap();

    String currency, currencySymbol;

    LoadingDialog loadingDialog;

    static final String PUBLIC_STRIPE_KEY = "pk_live_51KujHcHjbd308KiSyUwJof2CKWVwixE1B5MenKakG9NGhTdqTXnqiudSodAbzeHEOssC02YpDguq4jwdSme8bBBd003H6mDnDU";
    //static final String PUBLIC_STRIPE_KEY = "pk_test_51KujHcHjbd308KiSR7fzDD0ESDGMbbCRpfFWsq5CCEN6rVZjh793S3y84u1RDgKaEcrQCn3tXsRbB8qWphD6ShqC00g2F7M6ME";

    CvcEditText cvcEditText;

    boolean isKeyboardVisible = false;

    OkPaymentDialog.ActionInterface okPayInterface;

    // we need paymentIntentClientSecret to start transaction
    private String paymentIntentClientSecret;
    //declare stripe
    private Stripe stripe;

    Button selectPayButton, payWithCard;

    private OkHttpClient httpClient;

    ExpiryDateEditText dateEditText;

    StripeEditText nameEditText;

    boolean googlePayisAvailable = false;

    ImageView close, basicCard, backButton;

    double totalPrice, totalPriceLessSavings;

    StripeCardViewModel stripeCardViewModel;

    PaymentMethodDialog.ActionInterface actionInterface;

    ReviewOrderDialog.ActionInterface actionInterfaceOrder;

    PriceItem priceItem;

    TextView sucriptionTv, startDateTv, payMonthlyTv, toPayTv;

    ConstraintLayout infoLayout, paymentLayout;

    private PurchasesUpdatedListener purchasesUpdatedListener;

    MotionLayout baseMotion;

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private static final long SHIPPING_COST_CENTS = 90 * PaymentsUtil.CENTS_IN_A_UNIT.longValue();

    // A client for interacting with the Google Pay API.
    private PaymentsClient paymentsClient;

    CircularProgressIndicator indicator;

    String token;

    BillingFlowParams billingFlowParams;

    private BillingClient billingClient;

    QueryProductDetailsParams queryProductDetailsParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        cardEditText = findViewById(R.id.cardInputWidget);

        selectPayButton = findViewById(R.id.nextStep);

        cardBrandIcon = findViewById(R.id.brand);

        payWithCard = findViewById(R.id.payWithCard);

        nameEditText = findViewById(R.id.nameEt);

        infoLayout = findViewById(R.id.infoLayout);

        cvcEditText = findViewById(R.id.cvcEt);

        indicator = findViewById(R.id.indicator);

        indicator.hide();

        dateEditText = findViewById(R.id.validEt);

        baseMotion = findViewById(R.id.checkMotionBase);

        backButton = findViewById(R.id.backButton);

        paymentLayout = findViewById(R.id.insertPay);

        basicCard = findViewById(R.id.basicCard);

        sucriptionTv = findViewById(R.id.sucription);

        startDateTv = findViewById(R.id.startDate);

        payMonthlyTv = findViewById(R.id.payMonthly);

        close = findViewById(R.id.close);

        toPayTv = findViewById(R.id.toPay);

        loadingDialog = new LoadingDialog();

        httpClient = new OkHttpClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();


        //Initialize
        stripe = new Stripe(
                getApplicationContext(),
                PUBLIC_STRIPE_KEY
        );

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.
            }
        };

        okPayInterface = new OkPaymentDialog.ActionInterface() {
            @Override
            public void goPrem() {
                finish();
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                            }
                        }
                ).build();

        // establishConnection();

        //start the connection after initializing the billing client


        stripeCardViewModel = ViewModelProviders.of(this).get(StripeCardViewModel.class);

        actionInterface = new PaymentMethodDialog.ActionInterface() {
            @Override
            public void payWithCard() {
                baseMotion.transitionToState(R.id.addCard);

            }

            @Override
            public void payWithGooglePay() {
                indicator.show();

                establishConnection();
                // billingResult = billingClient.launchBillingFlow(CheckoutActivity.this, billingFlowParams);

                //requestPayment(new View(getApplicationContext()));
            }
        };

        actionInterfaceOrder = new ReviewOrderDialog.ActionInterface() {
            @Override
            public void pay() {

                loadingDialog.showDialog(CheckoutActivity.this, "Pagando");

                stripeCardViewModel.chargeUser(getApplicationContext(), token, 0);

            }
        };

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardEditText.setBrandChangeCallback$payments_core_release(new Function1<CardBrand, Unit>() {
            @Override
            public Unit invoke(CardBrand cardBrand) {

                if (!cardBrand.getDisplayName().equals("Unknown"))
                    basicCard.setVisibility(View.GONE);
                else
                    basicCard.setVisibility(View.VISIBLE);

                cardBrandIcon.setBrand(cardBrand);
                return null;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PaymentMethodDialog().showDialog(CheckoutActivity.this, actionInterface, true);
            }
        });

        payWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });


        stripeCardViewModel.getResponseLiveData().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject s) {


                loadingDialog.dismiss();

                if (s != null) {

                    try {

                        String clientSecret = s.getString("client_secret");

                        String status = s.getString("status");

                        if (status.equals("succeeded")) {

                            //ProToast.makeText(getApplicationContext(), R.drawable.ic_success, "Pago efectuado correctamente", Toast.LENGTH_LONG);
                            new OkPaymentDialog().showDialog(CheckoutActivity.this, okPayInterface);
                            AppHandler.changeToPremium(getApplicationContext(), priceItem.getDuration() * 31, false);

                        } else if (status.equals("payment_failed")) {

                            //ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Ha ocurrido un fallo en el pago", Toast.LENGTH_LONG);
                            new ErrorPaymentDialog().showDialog(CheckoutActivity.this);

                        } else {

                            stripe.handleNextActionForPayment(CheckoutActivity.this, clientSecret);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else
                    // ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Ha ocurrido un fallo en el pago", Toast.LENGTH_LONG);
                    new ErrorPaymentDialog().showDialog(CheckoutActivity.this);

            }
        });


        paymentsClient = PaymentsUtil.createPaymentsClient(this);

        possiblyShowGooglePayButton();

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isKeyboardVisible = isVisible;
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (baseMotion.getCurrentState() != R.id.start) {
            baseMotion.transitionToState(R.id.start);
            return;
        }
        super.onBackPressed();
        if (isKeyboardVisible)
            KeyboardUtils.forceCloseKeyboard(getCurrentFocus());
    }

    private void pay() {


        if (!Tools.isConnected(this)) {
            ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "No hay conexion a internet", Toast.LENGTH_LONG);
            return;
        }


        CardInputWidget cardInputWidget = new CardInputWidget(this);
        cardInputWidget.setCardNumber(cardEditText.getValidatedCardNumber$payments_core_release().getValue());
        cardInputWidget.setCvcCode(cvcEditText.getCvc$payments_core_release().getValue$payments_core_release());
        cardInputWidget.setExpiryDate(dateEditText.getValidatedDate().getMonth(), dateEditText.getValidatedDate().getYear());
        cardInputWidget.setPostalCodeRequired(false);


        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

        Log.d("amsñdlad", params + "");

        if (params == null) {
            return;
        }
        loadingDialog.showDialog(CheckoutActivity.this, "Pagando");

        stripe.createPaymentMethod(params, new ApiResultCallback<PaymentMethod>() {
            @Override
            public void onSuccess(@NonNull PaymentMethod result) {

                PaymentModel paymentModel = new PaymentModel(result.id, Tools.getSha256(Profile.getId() + "" + System.currentTimeMillis()),
                        getStripeAmount(totalPriceLessSavings, currency.toUpperCase()), nameEditText.getText().toString().trim(), currency, priceItem.getDuration());

                stripeCardViewModel.sendPaymentMethod(getApplicationContext(), paymentModel, 0);
            }

            @Override
            public void onError(@NonNull Exception e) {


                loadingDialog.dismiss();
                new CardErrorDialog().showDialog(CheckoutActivity.this, e.getMessage());

            }
        });
    }

    private void init() {

        Bundle bundle = getIntent().getExtras();

        String priceItemString = bundle.getString("price_item");

        currency = bundle.getString("currency");

        currencySymbol = bundle.getString("currencySymbol");

        Constants.CURRENCY_CODE = currency.toUpperCase();

        Constants.COUNTRY_CODE = Profile.getCountryCode();


        priceItem = new Gson().fromJson(priceItemString, PriceItem.class);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int cantMonth = priceItem.getDuration();

                if (cantMonth > 1)
                    sucriptionTv.setText(getString(R.string.plan_de_) + priceItem.getDuration() + getString(R.string._meses));
                else
                    sucriptionTv.setText(getString(R.string.plan_de_) + priceItem.getDuration() + getString(R.string._mes));


                Date endDate = Tools.addDaysToDate(new Date(), Calendar.DAY_OF_MONTH, priceItem.getDuration() * 31);

                startDateTv.setText(DateFormat.format("d MMMM yyyy", endDate));

                payMonthlyTv.setText(priceItem.getPrice() + currencySymbol + "/" + getString(R.string.mes));

                totalPrice = priceItem.getPrice() * priceItem.getDuration();

                totalPriceLessSavings = Tools.round(totalPrice - (totalPrice * CARD_SAVINGS), 2);

                //totalPlusTaxes = Tools.round(total + (total * 0.21), 2);

                toPayTv.setText(Tools.checkEndPointZero(totalPrice) + currencySymbol);

                //taxes.setText(totalPrice + "€ .incl 21% IVA");

                payWithCard.setText(getString(R.string.pagar_) + getToPayAmount(totalPriceLessSavings, currency.toUpperCase()) + currencySymbol);


            }
        });


    }

    private void validate() {

        if (!checkEmptyFields())
            return;


        if (!cardEditText.isCardNumberValid()) {
            cardEditText.setShouldShowError(true);
            cardEditText.setError(getString(R.string.numero_invalido));
            return;
        }

        if (!dateEditText.isDateValid()) {
            dateEditText.setShouldShowError(true);
            dateEditText.setError(getString(R.string.fecha_invalida));
            return;
        }
        if (cvcEditText.getCvc$payments_core_release() == null) {
            cvcEditText.setShouldShowError(true);
            cvcEditText.setError(getString(R.string.codigo_invalido));
            return;
        }


        pay();

    }

    private boolean checkEmptyFields() {

        boolean ok = true;


        if (!Tools.validateName(nameEditText, false))
            ok = false;

        if (cardEditText.getText().toString().isEmpty()) {
            cardEditText.setShouldShowError(true);
            cardEditText.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }

        if (dateEditText.getText().toString().isEmpty()) {
            dateEditText.setShouldShowError(true);
            dateEditText.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }

        if (cvcEditText.getText().toString().isEmpty()) {
            cvcEditText.setShouldShowError(true);
            cvcEditText.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        return ok;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                }


                break;
            default:
                stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
                break;
        }


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


    private void possiblyShowGooglePayButton() {

        JSONObject isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (isReadyToPayJson == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            //setGooglePayAvailable(task.getResult());
                            googlePayisAvailable = true;

                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }


    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {

        Log.d("aslkdjmañld", "succes0");


        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        Log.d("aslkdjmañld", "succes0 " + paymentInfo);


        if (paymentInfo == null) {
            return;
        }
        try {

            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");


            Log.d("anslkdadafa", "infoo " + tokenizationData.getString("token"));
            final JSONObject tokenData = new JSONObject(tokenizationData.getString("token"));
            final JSONObject payInfo = tokenData.getJSONObject("card");
            String cardInfo;
            if (payInfo != null) {
                String cardBrand = payInfo.getString("brand");
                String last4 = payInfo.getString("last4");

                cardInfo = cardBrand + "  **** " + last4;
            } else
                cardInfo = "Google Pay";

            new ReviewOrderDialog().showDialog(CheckoutActivity.this, String.valueOf(totalPrice), currencySymbol, cardInfo, actionInterfaceOrder);

            token = tokenData.getString("id");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("anslkdadafa", "error " + e.getMessage());
        }

    }


    private void handleError(int statusCode) {
        Log.d("aslkdjmañld", "hnadlerr ");
        Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    public void requestPayment(View view) {

        JSONObject paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(totalPrice);
        if (paymentDataRequestJson == null) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }


    }


    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("asñldmkasñdasd", "connection ok");

                    showProducts();
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("asñldmkasñdasd", "error cconection");
                indicator.hide();
                establishConnection();
            }
        });
    }


    void showProducts() {

        queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("both_pay_" + priceItem.getDuration())
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {

                        indicator.post(new Runnable() {
                            @Override
                            public void run() {
                                indicator.hide();
                            }
                        });
                        // check billingResult
                        // process returned productDetailsList
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                            // Process the result.


                            Log.d("asñldmkasñdasd", productDetailsList.size() + " ");
                            launchPurchaseFlow(productDetailsList.get(0));

                           /* for (ProductDetails productDetail : productDetailsList) {
                                Log.d("anksldasjdlma", productDetail.getName());
                                productDetailsParams.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                        .setProductDetails(productDetails)

                                        // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                        // for a list of offers that are available to the user
                                       // .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                        .build());

                                Log.d("asñldmkasñdasd", "zz " + productDetailsParams.size() + " ");
                            }*/
                            // Log.d("asñldmkasñdasd", "zz " + productDetailsParams.size() + " ");

                        }
                    }
                }
        );


    }


    void launchPurchaseFlow(ProductDetails productDetails) {
        Log.d("asñldmkasñdasd", "launch " + productDetails.getSubscriptionOfferDetails().get(0).getOfferToken());
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)

                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()))
                .build();

        Log.d("asñldmkasñdasd", "init");

        billingClient.launchBillingFlow(CheckoutActivity.this, billingFlowParams);
    }


    @Override
    protected void onResume() {
        super.onResume();

        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull @NotNull BillingResult billingResult, @NonNull @NotNull List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        });


    }


    void handlePurchase(Purchase purchase) {

        Log.d("asñlasmdddasd", Tools.getDurationByProduct(purchase.getProducts().get(0)) + "");

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {


            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull @NotNull BillingResult billingResult) {
                        AppHandler.changeToPremium(getApplicationContext(), priceItem.getDuration() * 31, false);
                        finish();
                    }
                });
            } else {
                AppHandler.changeToPremium(getApplicationContext(), priceItem.getDuration() * 31, false);
                finish();
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            ProToast.makeText(getApplicationContext(), R.drawable.ic_info, getString(R.string.procesando_pago), Toast.LENGTH_SHORT);
        else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            ProToast.makeText(getApplicationContext(), R.drawable.ic_alert, getString(R.string.error_inesperado), Toast.LENGTH_SHORT);

    }


    private String getStripeAmount(double cant, String currency) {

        Log.d("naslkdmañsda", non_decimals_currency.containsKey(currency) + "");
        if (!non_decimals_currency.containsKey(currency))
            cant *= 100;

        return String.valueOf((int) (cant));

    }


    private String getToPayAmount(double cant, String currency) {

        if (non_decimals_currency.containsKey(currency))
            return String.valueOf((int) (cant));

        return String.valueOf((cant));

    }


    private static Map<String, String> createMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("BIF", "BIF");
        myMap.put("CLP", "CLP");
        myMap.put("DJF", "DJF");
        myMap.put("GNF", "GNF");
        myMap.put("JPY", "JPY");
        myMap.put("KMF", "KMF");
        myMap.put("KRW", "KRW");
        myMap.put("MGA", "MGA");
        myMap.put("PYG", "PYG");
        myMap.put("RWF", "RWF");
        myMap.put("UGX", "UGX");
        myMap.put("VND", "VND");
        myMap.put("VUV", "VUV");
        myMap.put("XAF", "XAF");
        myMap.put("XOF", "XOF");
        myMap.put("XPF", "XPF");
        return myMap;
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        //If Payment is successful
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                //ProToast.makeText(getApplicationContext(), R.drawable.ic_success, "Pago efectuado correctamente", Toast.LENGTH_LONG);
                new OkPaymentDialog().showDialog(CheckoutActivity.this, okPayInterface);
                AppHandler.changeToPremium(getApplicationContext(), priceItem.getDuration() * 31, false);

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                //ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Ha ocurrido un fallo en el pago", Toast.LENGTH_LONG);
                new ErrorPaymentDialog().showDialog(CheckoutActivity.this);
            }
        }

        //If Payment is not successful
        @Override
        public void onError(@NonNull Exception e) {
            //ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Ha ocurrido un fallo en el pago", Toast.LENGTH_LONG);
            new ErrorPaymentDialog().showDialog(CheckoutActivity.this);

        }
    }
}