package com.insightdev.both;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.ping.PingManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Tools {

    private static String config = "";


    public static ArrayList<Bitmap> bitmaps = new ArrayList<>();

    public static String checkEndPointZero(double d) {

        if (d == Math.floor(d))
            return String.valueOf((int) d);

        return String.valueOf(d);

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connect.getActiveNetworkInfo();
        if (networkInfo != null)
            return networkInfo.isConnected();
        return false;
    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * Smoothly scroll to specified position allowing for interval specification. <br>
     * Note crude deceleration towards end of scroll
     *
     * @param rv       Your RecyclerView
     * @param toPos    Position to scroll to
     * @param duration Approximate desired duration of scroll (ms)
     * @throws IllegalArgumentException
     */
    public static void smoothScroll(RecyclerView rv, int toPos, int duration) throws IllegalArgumentException {
        int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;     // See androidx.recyclerview.widget.LinearSmoothScroller
        int itemHeight = rv.getChildAt(0).getHeight();  // Height of first visible view! NB: ViewGroup method!
        itemHeight = itemHeight + 33;                   // Example pixel Adjustment for decoration?
        int fvPos = ((LinearLayoutManager) rv.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        int i = Math.abs((fvPos - toPos) * itemHeight);
        if (i == 0) {
            i = (int) Math.abs(rv.getChildAt(0).getY());
        }
        final int totalPix = i;                         // Best guess: Total number of pixels to scroll
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rv.getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected int calculateTimeForScrolling(int dx) {
                int ms = (int) (duration * dx / (float) totalPix);
                // Now double the interval for the last fling.
                if (dx < TARGET_SEEK_SCROLL_DISTANCE_PX) {
                    ms = ms * 2;
                } // Crude deceleration!
                //lg(format("For dx=%d we allot %dms", dx, ms));
                return ms;
            }
        };
        //lg(format("Total pixels from = %d to %d = %d [ itemHeight=%dpix ]", fvPos, toPos, totalPix, itemHeight));
        smoothScroller.setTargetPosition(toPos);
        rv.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    public static int getPosOfInStringArray(String[] array, String item) {

        for (int i = 0; i < array.length; i++)
            if (array[i].contentEquals(item))
                return i;
        return -1;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    private String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static float getRatioFromUri(Uri uri, Context context) {

        try {
            ExifInterface ei;

            if (Build.VERSION.SDK_INT >= 24) {
                ei = new ExifInterface(context.getContentResolver().openInputStream(uri));
            } else {
                ei = new ExifInterface(uri.getPath());
            }

            int width = ei.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_UNDEFINED);
            int height = ei.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_UNDEFINED);

            return (float) width / height;

        } catch (IOException e) {
            Log.w("asd", "Failed to get image orientation from file.", e);

            return 1F;
        }
/*
        Bitmap bitmap = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null)
            return (float) bitmap.getWidth() / bitmap.getHeight();

        return 1F;*/

    }


    public static String processDocTime(Document document) {
        String[] tags = new String[]{
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements = document.select(tags[0]);
        for (String tag : tags) {
            elements = elements.select(tag);
        }
        return getDate(Long.parseLong(elements.text()));

    }

    public static String getAbsoluteTimestamp() throws Exception {
        Log.d("asoopdjkañkd", "nice");
        String url = "https://time.is/Unix_time_now";
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);

        Log.d("asoopdjkañkd", doc.title() + "");
        String[] tags = new String[]{
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements = doc.select(tags[0]);
        for (String tag : tags) {
            elements = elements.select(tag);
        }
        return getDate(Long.parseLong(elements.text()));
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);

        return dateFormat.format(cal.getTime());
    }

    public static int icebreakerSendedToday(Context context, String today) {

        String auxSendedIceb = Tools.getSettings("iceb_cant", context);

        if (auxSendedIceb.isEmpty())
            return 0;

        String lastTimestamp = Tools.getSettings("iceb_timestamp", context);

        if (lastTimestamp.isEmpty())
            return 0;

        lastTimestamp = lastTimestamp.substring(0, 10);


        today = today.substring(0, 10);


        if (!today.equals(lastTimestamp))
            return 0;

        return Integer.parseInt(auxSendedIceb);


    }

    public static String getStringImg(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static void write(String fileName, Bitmap bitmap, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static Bitmap scaleImage(Uri uri, int minDim, Context context) {
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            double ratio = height / (width * 1.0);
            if (ratio > 1) {
                width = minDim;
                height = (int) (width * ratio);
            } else {
                height = minDim;
                width = (int) (height / ratio);
            }
            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int minDim) {
        Bitmap bmp = bitmap;

        int height = bmp.getHeight();
        int width = bmp.getWidth();
        double ratio = height / (width * 1.0);
        if (ratio > 1) {
            width = minDim;
            height = (int) (width * ratio);
        } else {
            height = minDim;
            width = (int) (height / ratio);
        }
        bmp = Bitmap.createScaledBitmap(bmp, width, height, true);


        return bmp;
    }


    static public Contact profileToContact(Person person, String type) {
        String myProfile = new Gson().toJson(person);
        myProfile = putValue(myProfile, "status", "");
        myProfile = putValue(myProfile, "type", type);

        return new Gson().fromJson(myProfile, Contact.class);
    }

    static public String profileToContactJson(Person person, String type) {
        String myProfile = new Gson().toJson(person);
        myProfile = Tools.putValue(myProfile, "status", "");
        myProfile = Tools.putValue(myProfile, "type", type);

        if (new Gson().fromJson(myProfile, Contact.class) != null)
            return myProfile;
        return null;
    }

    public static String getValue(String str, String key) {
        JSONObject json;
        try {
            json = new JSONObject(str);
            return json.getString(key);
        } catch (JSONException e) {
            Log.d("Login_", "log" + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public static String putValue(String str, String key, String value) {
        JSONObject json;
        try {
            json = new JSONObject(str);
            json.remove(key);
            json.put(key, value);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("aslkndasda", "error " + e.getMessage());
        }
        return str;
    }

    public static String getJson(ArrayList<Pair<String, String>> data) {
        JSONObject object = new JSONObject();
        for (Pair<String, String> pair : data) {
            try {
                object.put(pair.first, pair.second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object.toString();
    }

    public static String nameCapitalize(String name) {
        String pattern = "\\s+";
        String[] array = name.toLowerCase().split(pattern);
        StringBuilder new_name = new StringBuilder();

        if (!name.isEmpty())
            for (String s : array)
                new_name.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");

        return new_name.toString().trim();
    }

    public static String getFirstWords(String str, int cant) {
        String pattern = "\\s+";
        String[] array = str.split(pattern);
        StringBuilder first_words = new StringBuilder();
        if (array.length < cant)
            cant = array.length;
        for (int i = 0; i < cant; i++)
            first_words.append(array[i]).append(" ");

        return first_words.toString().trim();
    }

    public static String getStringWithDotsAt(String str, int size) {
        String word = str;

        int wordLength = word.length();

        if (wordLength > size) {
            Log.d("ansñdlkañd", wordLength + " /" + word.substring(size - 5, size).contains(" "));
            if (!word.substring(size - 5, size).contains(" "))
                word = word.substring(0, size - 1) + ".";
            else
                return stringWithNoSpaceAtEnd(word);
        }

        return word;


    }

    private static String stringWithNoSpaceAtEnd(String str) {
        int length = str.length();

        for (int i = length - 1; i >= 0; i--)

            if (Objects.equals(str.charAt(i), ' '))

                return str.substring(0, i);


        return str;
    }

    public static boolean validateName(EditText name, boolean isOptional) {
        String str = name.getText().toString().trim();
        String pattern = "[a-zA-Z\\sáéíóúÁÉÍÓÚñÑ]+";
        if (str.isEmpty()) {
            if (!isOptional) {
                name.setError("Rellene el campo");
                return false;
            }
            return true;
        } else if (!str.matches(pattern)) {
            name.setError("Nombre inválido");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }


    public static boolean validateCity(EditText name) {
        String str = name.getText().toString().trim();
        String pattern = "[a-zA-Z\\sáéíóúüÜÁÉÍÓÚñÑ]+";
        if (str.isEmpty()) {
            name.setError("Rellene el campo");
            return false;
        } else if (!str.matches(pattern)) {
            name.setError("Nombre inválido");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    public static boolean validateOccupation(EditText occupation) {
        String str = occupation.getText().toString().trim();
        String pattern = "[a-zA-Z\\sáéíóúÁÉÍÓÚ]+[,]*[.a-zA-Z\\sáéíóúÁÉÍÓÚ]*";
        if (str.isEmpty()) {
            occupation.setError("Rellene el campo");
            return false;
        } else if (!str.matches(pattern)) {
            occupation.setError("Formato inválido");
            return false;
        } else {
            occupation.setError(null);
            return true;
        }
    }


    public static boolean validatePhone(EditText phone, boolean isOptional) {
        String str = phone.getText().toString().trim();
        if (str.isEmpty()) {
            if (!isOptional) {
                phone.setError("Rellene el campo");
                return false;
            }
            return true;
        } else if (str.length() != 8) {
            phone.setError("Número incorrecto");
            return false;
        } else if (!str.substring(0, 1).contentEquals("5")) {
            phone.setError("Número incorrecto");
            return false;
        } else {
            phone.setError(null);
            return true;
        }
    }


    public static boolean validateEmail(EditText email, boolean isOptional) {
        String str = email.getText().toString().trim();
        if (str.isEmpty()) {
            if (!isOptional) {
                email.setError("Rellene el campo");
                return false;
            }
            return true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            email.setError("Correo inválido");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    public static boolean validateUser(EditText user) {
        String str = user.getText().toString().trim();
        String pattern = "[\\p{Alnum}[@#$%^&+=._/*:!-]]+";
        if (str.isEmpty()) {
            user.setError("Rellene el campo");
            return false;
        } else if (!str.matches(pattern)) {
            user.setError("No se permiten espacios en blanco");
            return false;
        } else {
            user.setError(null);
            return true;
        }
    }

    public static boolean validatePass(EditText pass) {
        String str = pass.getText().toString().trim();
        String pattern = "^" +
                //"(?=.*[0-9])" +       //al menos 1 digito
                //"(?=.*[a-z])" +       //al menos 1 minuscula
                //"(?=.*[A-Z])" +       //al menos  1 mayuscula
                "(?=.*[a-zA-Z])" +      //al menos 1 letra
                //"(?=.*[@#$%^&+=])" +    //al menos 1 caracter especial
                //"(?=\\S+$)" +           //sin espacios en blanco
                ".{6,}" +                //al menos 6 caracteres
                "$";
        if (str.isEmpty()) {
            pass.setError("Rellene el campo");
            return false;
        } else if (!str.matches(pattern)) {
            pass.setError("La contraseña debe tener al menos 6 caracteres, entre ellos una letra");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    public static String getUUiD() {
        UUID COMMON_PSSH_UUID = new UUID(0x1077EFECC0B24D02L, -0x531cc3e1ad1d04b5L);
        UUID CLEARKEY_UUID = new UUID(-0x1d8e62a7567a4c37L, 0x781AB030AF78D30EL);
        UUID WIDEVINE_UUID = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
        UUID PLAYREADY_UUID = new UUID(-0x65fb0f8667bfbd7aL, -0x546d19a41f77a06bL);
        UUID[] commonsUUID = {WIDEVINE_UUID, COMMON_PSSH_UUID, CLEARKEY_UUID, PLAYREADY_UUID};

        for (UUID uuid : commonsUUID) {
            try {
                byte[] id = new MediaDrm(uuid)
                        .getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
                return Arrays.toString(id);
            } catch (UnsupportedSchemeException e) {
                e.printStackTrace();
            }
        }
        return "Indisponible";

    }

    public static int generateRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static String getSha256(String str) {
        return Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }

    public static String getJsonMsg(String msg, String id, String key) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", msg);
            object.put("x", id);
            object.put("key", key);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJsonMsg(String msg, String id) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", msg);
            object.put("x", id);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJsonMsgWithContact(String msg, String id, String key, String contact) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", msg);
            object.put("x", id);
            object.put("key", key);
            object.put("contact", contact);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJsonMsgWithContact(String msg, String id, String contact) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", msg);
            object.put("x", id);
            object.put("contact", contact);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMsgWithReply(String body, String textReply, String id) {
        JSONObject object = new JSONObject();
        try {
            object.put("body", body);
            String reply = "{}";
            reply = putValue(reply, "text", textReply);
            reply = putValue(reply, "id", id);
            object.put("reply", reply);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getJsonAudio(String strAudio, int duration) {
        JSONObject object = new JSONObject();
        try {
            object.put("audio", strAudio);
            object.put("duration", duration);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void saveRsaKeys(PublicKey publicKey, PrivateKey privateKey, Context context) {
        if (context == null)
            return;
        byte[] pubKey = publicKey.getEncoded();
        byte[] priKey = privateKey.getEncoded();

        String pubKeyString = Base64.encodeToString(pubKey, Base64.DEFAULT);
        String priKeyString = Base64.encodeToString(priKey, Base64.DEFAULT);
        try {
            putKeyInPreferences("public", pubKeyString, context);
            putKeyInPreferences("private", priKeyString, context);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Pair<PublicKey, PrivateKey> getRSAKeys(Context context) {

        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        Pair<String, String> rsaKeys = null;
        try {
            rsaKeys = getKeyStrings(context);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (rsaKeys.first != null) {
            publicKey = getPublicKeyFromString(rsaKeys.first);
            privateKey = getPrivateKeyFromString(rsaKeys.second);
        }

        return new Pair<>(publicKey, privateKey);
    }

    private static void putKeyInPreferences(String keyName, String keyString, Context context) throws GeneralSecurityException, IOException {
        SharedPreferences.Editor editor = getEncryptedSharedPreferences(context, "keys").edit();
        editor.putString(keyName, keyString);
        editor.apply();
    }

    private static Pair<String, String> getKeyStrings(Context context) throws GeneralSecurityException, IOException {
        SharedPreferences pref = getEncryptedSharedPreferences(context, "keys");
        String publicKey = pref.getString("public", null);
        String privateKey = pref.getString("private", null);
        return new Pair<>(publicKey, privateKey);

    }

    public static SharedPreferences getEncryptedSharedPreferences(Context context, String filename) throws GeneralSecurityException, IOException {

        MasterKey masterKeyAlias = null;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            masterKeyAlias = new MasterKey.Builder(context)
                    .setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } else {
            masterKeyAlias = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        }*/
        masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        return EncryptedSharedPreferences.create(
                context,
                filename,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static PublicKey getPublicKeyFromString(String str) {

        Log.d("MyAes", "tools " + str);
        byte[] publicBytes = Base64.decode(str, Base64.DEFAULT);
        PublicKey publicKey = null;
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKeyFromString(String str) {
        byte[] privateBytes = Base64.decode(str, Base64.DEFAULT);
        PrivateKey privateKey = null;
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static String getFullDate(long date) {
        DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setMonths(new String[]{"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"});
        symbols.setWeekdays(new String[]{"domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"});
        String pattern = "EEEEEEEE dd MMMM yyyy hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, symbols);
        return sdf.format(new Date(date));
    }

    public static String convertMillisToMinutes(int timeMillis) {
        int timeSeconds = timeMillis / 1000;
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String strMinutes = minutes + "";
        String strSeconds = seconds + "";

        if (seconds < 10)
            strSeconds = "0" + strSeconds;

        return strMinutes + ":" + strSeconds;
    }

    public static int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }
        return array;
    }

    public static String getString(int id, Context context) {
        return context.getResources().getString(id);
    }

    public static int getAge(int year, int month, int day) {
        Date now = new Date();

        int year_difference = now.getYear() + 1900 - year;
        if (now.getMonth() < month) {
            year_difference--;
        } else if (now.getMonth() == month && now.getDate() < day) {
            year_difference--;
        }

        return year_difference;
    }

    public static Date addDaysToDate(Date date, int calendarField, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static void saveSettings(String key, String value, Context context) {
        if (context == null)
            return;
        SharedPreferences preferences = null;
        try {
            preferences = getEncryptedSharedPreferences(context, "settings");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static String getSettings(String key, Context context) {

        if (context == null)
            return"";
        SharedPreferences preferences = null;
        try {
            preferences = getEncryptedSharedPreferences(context, "settings");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (preferences != null) {
            Log.d("anslkdadafa", "tools " + preferences.getString(key, ""));
            return preferences.getString(key, "");
        }


        return "";
    }

    public static String getConfig(Context context) {
        if (!config.isEmpty())
            return config;
        SharedPreferences preferences = null;
        try {
            preferences = getEncryptedSharedPreferences(context, "config");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (preferences != null)
            return preferences.getString("config", "");

        return "";
    }

    public static void setConfig(String config, Context context) {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Tools.config = config;
                SharedPreferences preferences = null;
                try {
                    preferences = getEncryptedSharedPreferences(context, "config");
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("config", config);
                editor.apply();
            }
        });

    }

    public static void initConfig(Context context) {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = null;
                try {
                    preferences = getEncryptedSharedPreferences(context, "config");
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
                if (preferences != null)
                    config = preferences.getString("config", "");
            }
        });

    }

    public static boolean isServiceAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return true;
        String str = getValue(getConfig(context), "service");
        return getValue(str, "available").contentEquals("1");
    }

    static public void closeMainService(Context context) {

        if (isMyServiceRunning(MainService.class, context) && !AppHandler.isMainActivityOpen() && !AppHandler.isChatActivityOpen() && !AppHandler.isOpeningActivityOpen()) {
            Log.d("aklsd", "CLOSED");
            context.stopService(new Intent(context, MainService.class));
        }


    }

    static public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDaysForDeployment(Context context) {
        String str = getValue(getConfig(context), "service");
        return getValue(str, "days");
    }


    public static boolean isNativeAdsAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return false;
        String str = getValue(getConfig(context), "ads");
        str = getValue(str, "natives");
        return getValue(str, "available").contentEquals("1");
    }

    public static int getStepNativeAds(Context context) {
        if (getConfig(context).isEmpty())
            return 0;
        String str = getValue(getConfig(context), "ads");
        Log.d("NativeAds", "config" + getConfig(context));
        str = getValue(str, "natives");
        Log.d("NativeAds", str);
        Log.d("NativeAds", getValue(str, "step"));
        return Integer.parseInt(getValue(str, "step"));
    }

    public static boolean isBannerAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return false;
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "banner").contentEquals("1");
    }

    public static boolean isAdsPremiumAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return false;
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "ads_premium").contentEquals("1");
    }

    public static Pair<Integer, Boolean> getAppVersionAvailable(Context context) {
        String strApp = getValue(getConfig(context), "app");
        String strArray = getValue(strApp, "versions");
        int apiVersion = Build.VERSION.SDK_INT;
        JSONArray json = null;
        try {
            json = new JSONArray(strArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str;
        int maxApiVersion, minApiVersion, version_code;
        boolean isRequired;
        for (int i = 0; i < (json != null ? json.length() : 0); i++) {
            try {
                str = json.get(i).toString();
                version_code = Integer.parseInt(getValue(str, "version_code"));
                isRequired = getValue(str, "is_required").contentEquals("1");
                minApiVersion = Integer.parseInt(getValue(str, "min_api"));
                maxApiVersion = Integer.parseInt(getValue(str, "max_api"));
                if (minApiVersion <= apiVersion && apiVersion <= maxApiVersion)
                    return new Pair<>(version_code, isRequired);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return new Pair<>(1, false);
    }

    public static boolean isAppVersionHigher(int version) {
        return version > BuildConfig.VERSION_CODE;
    }

    public static String getAppUrl(Context context) {
        String strApp = getValue(getConfig(context), "app");
        return getValue(getValue(strApp, "url"), getValue(strApp, "url_priority"));
    }

    public static String getPrivacyUrl(Context context) {
        return getValue(getConfig(context), "privacy_url");
    }

    public static String getTermsUrl(Context context) {
        return getValue(getConfig(context), "terms_url");
    }

    public static String getPushNotification(Context context) {
        return getValue(getConfig(context), "push_notification");
    }

    public static String getCantTopLikes(Context context) {
        return getValue(getConfig(context), "top_likes");
    }

    public static String getApkLisUrl(Context context) {
        String strApp = getValue(getConfig(context), "app");
        return getValue(getValue(strApp, "url"), "apklis");
    }

    public static String getGooglePlayUrl(Context context) {
        String strApp = getValue(getConfig(context), "app");
        return getValue(getValue(strApp, "url"), "google_play");
    }

    public static String getWebsite(Context context) {
        return getValue(getConfig(context), "website");
    }

    public static String getDirectLink(Context context) {
        String strApp = getValue(getConfig(context), "app");
        return getValue(getValue(strApp, "url"), "direct_link");
    }

    public static String getSocial(Context context) {
        return getValue(getConfig(context), "social");
    }

    public static String getSharedPhotoUrl(Context context) {
        String strApp = getValue(getConfig(context), "app");
        return getValue(strApp, "shared_photo");
    }

    public static String getPlacementPosts(Context context) {
        String strAds = getValue(getConfig(context), "ads");
        return getValue(getValue(strAds, "placement_ids"), "posts");
    }

    public static String getPlacementInfoProfiles(Context context) {
        String strAds = getValue(getConfig(context), "ads");
        Log.d("Placement", "s" + getValue(getValue(strAds, "placement_ids"), "info_profile"));
        return getValue(getValue(strAds, "placement_ids"), "info_profile");
    }

    public static String getAddress(String name, Context context) {
        String strArray = getValue(getConfig(context), "servers");
        JSONArray json = null;
        try {
            json = new JSONArray(strArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (json != null ? json.length() : 0); i++) {
            try {
                String str = json.get(i).toString();
                if (getValue(str, getString(R.string.name, context)).contentEquals(name))
                    return getValue(str, getString(R.string.address, context));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    public static boolean isPaymentAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return false;
        String payment = getValue(getConfig(context), "payment");
        String available = getValue(payment, "available");
        return available.contentEquals("1");
    }

    public static int[] getPremiumPrices(Context context) {
        String payment = getValue(getConfig(context), "payment");
        String prices = getValue(payment, "prices");
        String[] strArray = prices.split(",");
        int[] intArray = new int[4];
        int cont = 0;
        for (String str : strArray) {
            intArray[cont++] = Integer.parseInt(str);
        }
        return intArray;
    }

    public static String getCardNumber(Context context) {
        String payment = getValue(getConfig(context), "payment");
        return getValue(payment, "card_number");
    }

    public static boolean isFreePremiumAvailable(Context context) {
        if (getConfig(context).isEmpty())
            return true;
        String free_premium = getValue(getConfig(context), "free_premium");
        String available = getValue(free_premium, "available");
        return available.contentEquals("1");
    }

    public static int getFreePremiumDays(Context context) {
        if (getConfig(context).isEmpty())
            return 7;
        String free_premium = getValue(getConfig(context), "free_premium");
        int days = Integer.parseInt(getValue(free_premium, "days"));
        return days;
    }

    public static boolean isAvailableReload(Context context) {
        int lastDate = PersonsToday.getLastDate(context);
        if (lastDate == -1)
            return true;
        int now = new Date().getDate();
        int dif = now - lastDate;

        return dif != 0;
    }

    public static boolean isNotifiedReload(Context context) {
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, "reload");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        int last_notified = preferences.getInt("isNotified", 0);
        int now = new Date().getDate();
        int dif = now - last_notified;
        return !(dif > 1 || dif < 0);
    }


    public static void saveNotifiedReload(Context context) {
        if (context == null)
            return;
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, "reload");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("isNotified", new Date().getDate());
        editor.apply();
    }


    public static boolean isBlacklist(Context context) {
        SharedPreferences preferences = null;
        try {
            preferences = getEncryptedSharedPreferences(context, "blacklist");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return preferences.getBoolean("isBlacklist", false);
    }

    public static void saveBlacklist(Context context) {
        if (context == null)
            return;
        SharedPreferences preferences = null;
        try {
            preferences = getEncryptedSharedPreferences(context, "blacklist");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isBlacklist", true);
        editor.apply();
    }

    public static String getBackAds(Context context) {
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "back_ads");
    }

    public static boolean isAllFreePremium(Context context) {
        if (context == null)
            return false;
        Log.d("AllPremium_Config", "Str" + getConfig(context));
        String str = getValue(getConfig(context), "all_free_premium");
        Log.d("AllPremium_", "Str" + str);
        return str.contentEquals("1");
    }

    public static int getCantAds(Context context) {
        String str = getValue(getConfig(context), "ads");
        int cantAds;
        try {
            cantAds = Integer.parseInt(getValue(str, "cant_ads"));
        } catch (Exception e) {
            return 0;
        }
        return cantAds;
    }

    public static String getDefaultAds(Context context) {
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "default_ads");
    }

    public static String getCustomAds(Context context) {
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "custom_ads");
    }

    public static String getEroticAds(Context context) {
        String str = getValue(getConfig(context), "ads");
        return getValue(str, "erotic_ads");
    }


    public static boolean isBackAdsAvailable(Context context) {
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, "back_ads");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        int last_notified = preferences.getInt("date", 0);
        int now = new Date().getDate();
        int dif = now - last_notified;
        return dif != 0;
    }

    public static void saveBackAds(Context context) {
        if (context == null)
            return;
        SharedPreferences preferences = null;
        try {
            preferences = Tools.getEncryptedSharedPreferences(context, "back_ads");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("date", new Date().getDate());
        editor.apply();
    }


    public static String getZodiac(int day, int month, Context context) {
        switch (month) {
            case 1:
                // if the day int is higher or equal to 20, return Aquarius,
                // otherwise Capricorn
                return day >= 20 ? getString(R.string.acuario_, context) + convertEmoji("u+2652") : getString(R.string.capricornio_, context) + convertEmoji("u+2651");
            case 2:
                return day >= 19 ? getString(R.string.piscis_, context) + convertEmoji("u+2653") : getString(R.string.acuario_, context) + convertEmoji("u+2652");
            case 3:
                return day >= 21 ? getString(R.string.aries_, context) + convertEmoji("u+2648") : getString(R.string.piscis_, context) + convertEmoji("u+2653");
            case 4:
                return day >= 20 ? getString(R.string.tauro_, context) + convertEmoji("u+2649") : getString(R.string.aries_, context) + convertEmoji("u+2648");
            case 5:
                return day >= 21 ? getString(R.string.geminis_, context) + convertEmoji("u+264a") : getString(R.string.tauro_, context) + convertEmoji("u+2649");
            case 6:
                return day >= 21 ? getString(R.string.cancer_, context) + convertEmoji("u+264b") : getString(R.string.geminis_, context) + convertEmoji("u+264a");
            case 7:
                return day >= 23 ? getString(R.string.leo_, context) + convertEmoji("u+264c") : getString(R.string.cancer_, context) + convertEmoji("u+264b");
            case 8:
                return day >= 23 ? getString(R.string.virgo_, context) + convertEmoji("u+264d") : getString(R.string.leo_, context) + convertEmoji("u+264c");
            case 9:
                return day >= 23 ? getString(R.string.libra_, context) + convertEmoji("u+264e") : getString(R.string.virgo_, context) + convertEmoji("u+264d");
            case 10:
                return day >= 23 ? getString(R.string.escorpio_, context) + convertEmoji("u+264f") : getString(R.string.libra_, context) + convertEmoji("u+264e");
            case 11:
                return day >= 22 ? getString(R.string.sagitario_, context) + convertEmoji("u+2650") : getString(R.string.escorpio_, context) + convertEmoji("u+264f");
            case 12:
                return day >= 22 ? getString(R.string.capricornio_, context) + convertEmoji("u+2651") : getString(R.string.sagitario_, context) + convertEmoji("u+2650");
            // A default option because the function needs a return value
            default:
                return "Error";
        }
    }

    static public int dpIntoPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) Math.ceil(dp * displayMetrics.density);


    }

    static public int pxIntoDp(int px, Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) Math.ceil(px / displayMetrics.density);


    }

    static public String getMonth(int M) {
        switch (M) {
            case 1:
                return "ene.";
            case 2:
                return "feb.";
            case 3:
                return "mar.";
            case 4:
                return "abr.";
            case 5:
                return "may.";
            case 6:
                return "jun.";
            case 7:
                return "jul.";
            case 8:
                return "ago.";
            case 9:
                return "sep.";
            case 10:
                return "oct.";
            case 11:
                return "nov.";
            case 12:
                return "dic.";
            default:
                return null;

        }
    }

    public static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }

        return returnedEmoji;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getStringFromInt(int cant) {
        String str;
        DecimalFormat d = new DecimalFormat("'$'0.0");
        if (cant > 1000 && cant < 1000000) {
            str = d.format(cant / 1000.0) + "K";
        } else if (cant > 1000000)
            str = d.format(cant / 1000000.0) + "M";
        else
            str = cant + "";

        return str;
    }

    public static Date getDateFromTIMESTAMP(String timestamp) {
        if (timestamp.isEmpty())
            return null;
        String date = StringUtils.substringBetween("$" + timestamp, "$", " ");
        String[] dateArray = date.split("-");
        Log.d("Log_", "dateArray" + dateArray.toString());
        int year = Integer.parseInt(dateArray[0]) - 1900;
        int month = Integer.parseInt(dateArray[1]);
        int day = Integer.parseInt(dateArray[2]);

        return new Date(year, month, day);
    }

    public static Date getDateFromDb(String time) {
        int year = Integer.parseInt(getValue(time, "year")) - 1900;
        int month = Integer.parseInt(getValue(time, "month")) - 1;
        int day = Integer.parseInt(getValue(time, "day"));
        int hrs = Integer.parseInt(getValue(time, "hours"));
        int min = Integer.parseInt(getValue(time, "minutes"));
        int sec = Integer.parseInt(getValue(time, "seconds"));
        return new Date(year, month, day, hrs, min, sec);
    }


    public static void clearStorage(Context context) {
        //clearCache();
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .clearApplicationUserData();
        /*String packageName = activity.getPackageName();
          Runtime runtime = Runtime.getRuntime();
          try {
              runtime.exec("pm clear "+packageName);
          } catch (IOException e) {
              e.printStackTrace();
          }*/

    }

    public static boolean isUserOnline(String contact) {
        if (XMPPMessageServer.getConnection() != null) {
            XMPPMessageServer.getChatHandler().addRosterEntry(contact, contact);
            Roster roster = XMPPMessageServer.getRoster();
            //  String with = contact.getChatUsername(context);
            BareJid jid = null;
            try {
                jid = JidCreate.bareFrom(contact);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
            if (roster != null)
                return roster.getPresence(jid).getType().equals(Presence.Type.available);

        }
        return false;
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public static byte[] getRandomNonce() {
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }


    public static int getDurationByProduct(String product) {

        String intVal = product.replace("both_pay_", "");

        return Integer.valueOf(intVal);


    }

}
