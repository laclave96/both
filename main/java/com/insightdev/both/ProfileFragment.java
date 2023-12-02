package com.insightdev.both;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private SimpleDraweeView circularProfileImg;
    private RelativeLayout infoButton, settingButton, addImage;
    private TextView textName, username, likes, matches;
    private TakeImageDialog takeImageDialog;

    private ProfileDialogFragment profileDialogFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        infoButton = view.findViewById(R.id.infoPerfil);
        addImage = view.findViewById(R.id.relativeLayout);
        settingButton = view.findViewById(R.id.settingsPerfil);
        textName = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        circularProfileImg = view.findViewById(R.id.circularProfileImg);
        likes = view.findViewById(R.id.likes);
        matches = view.findViewById(R.id.matches);
        takeImageDialog = new TakeImageDialog();

        profileDialogFragment = new ProfileDialogFragment();

        if (Profile.isPhotoUpLoad())
            circularProfileImg.setImageURI(Uri.parse(Profile.getProfilePhotoMedium(getContext())));

        String user = Profile.getUser(getContext());
        String name = Profile.getName();

        Log.d("username_", user);
        if (user != null) {
            if (user.contains("@both.com"))
                username.setText(StringUtils.substringBetween("$%@$" + user, "$%@$", "@both.com"));
            else if (user.contains("@facebook.com")) {
                username.setText(Tools.getFirstWords(name, 1) + "@facebook");
            } else {
                username.setText(user);
                Log.d("username_", user);
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openInfoProfileFragment();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                    return;
                }
                passToPickImageActivity(0, false, !Profile.isPhotoUpLoad());
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openSettingsFragment();
            }
        });
        circularProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openPersonDialog(Profile.person);
            }
        });
        EventBus.getDefault().register(this);
        loadActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateName(BasicInfoEvent event) {
        String name = Profile.getName();
        String birth = Profile.getBirth();
        textName.setText(name);
        if (!birth.isEmpty()) {
            int year = Integer.parseInt(birth.substring(0, 4));
            int month = Integer.parseInt(birth.substring(4, 6));
            int day = Integer.parseInt(birth.substring(6, 8));
            textName.setText(name + ", " + Tools.getAge(year, month, day));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadPhoto(UpdateProfilePhotoEvent event) {
        String url = Profile.getProfilePhotoMedium(getContext());
        Log.d("ReloadPhoto", "reload " + url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //imagePipeline.clearCaches();
        imagePipeline.evictFromCache(Uri.parse(url));
        circularProfileImg.setImageURI(Uri.parse(url));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLikesAndMatches(ChangeContactsEvent event) {
        loadActivity();
    }

    private void loadActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String l = Tools.getStringFromInt(ContactsManager.getCantLikes(getContext()));
                String m = Tools.getStringFromInt(ContactsManager.getCantMatches(getContext()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        likes.setText(l);
                        matches.setText(m);
                    }
                });
            }
        }).start();


    }

    private void passToPickImageActivity(int from, boolean multi, boolean update) {
        Intent intent = new Intent(getActivity(), PickImage.class);
        intent.putExtra("from", from);
        intent.putExtra("multi", multi);
        intent.putExtra("update", update);
        startActivity(intent);
    }

    private void openPersonDialog(Person contact) {

        ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
        if (profileDialogFragment.isAdded())
            return;
        Bundle bundle = new Bundle();
        bundle.putString("person", new Gson().toJson(contact));
        profileDialogFragment.setArguments(bundle);

        profileDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "ProfileDialog");
    }


}