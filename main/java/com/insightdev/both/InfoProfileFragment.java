package com.insightdev.both;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.InstagramAuthViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoProfileFragment extends Fragment {

    private ImageRequest request;
    ResizeOptions resizeOptions = new ResizeOptions(480, 480);
    ArrayList<SimpleDraweeView> roundedImageViews = new ArrayList<>();
    ArrayList<TextView> changeButtons = new ArrayList<>();
    ArrayList<TextView> editButtons = new ArrayList<>();
    ArrayList<TextView> deleteButtons = new ArrayList<>();
    ArrayList<ImageView> icons = new ArrayList<>();
    RelativeLayout tagsButton, infoPersonalButton, aboutMe, survey, orientation, occupation, zodiac,
            gender, instagram, email;

    InstagramAuthViewModel instagramAuthViewModel;

    ArrayList<MotionLayout> motions = new ArrayList<>();

    ArrayList<RelativeLayout> addButtons = new ArrayList<>();
    TakeImageDialog takeImageDialog;
    TextView textName, textCity, textGender, textOrientation, textOccupation, textZodiac,
            textAboutMe, textInsta, cantAnswers, textEmail;
    ImageView backButton, warningInfo, warningGender, warningEmail;
    ChipGroup chipGroup;
    Chip chipGuide;
    PersonalInfoBottDialog personalInfoBottDialog;
    TagBottomDialog tagBottomDialog;
    AboutMeDialog aboutMeDialog;
    OrientationDialog orientationDialog;
    OccupationDialog occupationDialog;
    BottomDialogSurvey bottomDialogSurvey;
    GenderDialog genderDialog;
    InstaDialog instaDialog;
    TextView title;
    ImageView titleIcInfo;


    public static native String deletePhoto();

    public static native String pass();

    public static native String pathPhoto();

    InstagramAuthFragmentDialog.ActionInterface actionInterface;

    static {
        System.loadLibrary("native-lib");
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoProfileFragment newInstance(String param1, String param2) {
        InfoProfileFragment fragment = new InfoProfileFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_profile, container, false);
        genderDialog = new GenderDialog();
        tagBottomDialog = new TagBottomDialog();
        personalInfoBottDialog = new PersonalInfoBottDialog();
        orientationDialog = new OrientationDialog();
        occupationDialog = new OccupationDialog();
        bottomDialogSurvey = new BottomDialogSurvey();
        aboutMeDialog = new AboutMeDialog();
        instaDialog = new InstaDialog();
        takeImageDialog = new TakeImageDialog();

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));
        motions.add((MotionLayout) view.findViewById(R.id.image1));
        motions.add((MotionLayout) view.findViewById(R.id.image2));
        motions.add((MotionLayout) view.findViewById(R.id.image3));
        motions.add((MotionLayout) view.findViewById(R.id.image4));
        motions.add((MotionLayout) view.findViewById(R.id.image5));

        for (int i = 0; i < 5; i++) {
            roundedImageViews.add((SimpleDraweeView) motions.get(i).findViewById(R.id.image));
            changeButtons.add((TextView) motions.get(i).findViewById(R.id.add));
            icons.add((ImageView) motions.get(i).findViewById(R.id.iconAdd));
            editButtons.add((TextView) motions.get(i).findViewById(R.id.edit));
            deleteButtons.add((TextView) motions.get(i).findViewById(R.id.delete));
            addButtons.add((RelativeLayout) motions.get(i).findViewById(R.id.addImage));
        }

        title = view.findViewById(R.id.title);
        titleIcInfo = view.findViewById(R.id.ic_info);

        tagsButton = view.findViewById(R.id.preferences);
        infoPersonalButton = view.findViewById(R.id.nameInfo);
        aboutMe = view.findViewById(R.id.aboutMe);
        occupation = view.findViewById(R.id.occupation);
        zodiac = view.findViewById(R.id.zodiac);
        survey = view.findViewById(R.id.survey);
        orientation = view.findViewById(R.id.orientation);
        gender = view.findViewById(R.id.gender);
        instagram = view.findViewById(R.id.instagram);
        email = view.findViewById(R.id.email);
        textName = view.findViewById(R.id.textName);
        textCity = view.findViewById(R.id.textCity);
        textGender = view.findViewById(R.id.textGender);
        textOrientation = view.findViewById(R.id.textOrientation);
        textOccupation = view.findViewById(R.id.textOccupation);
        textZodiac = view.findViewById(R.id.textZodiac);
        textAboutMe = view.findViewById(R.id.textAboutMe);
        textInsta = view.findViewById(R.id.textInsta);
        textEmail = view.findViewById(R.id.textEmail);
        chipGroup = view.findViewById(R.id.tags);
        chipGuide = view.findViewById(R.id.chipGuide);
        cantAnswers = view.findViewById(R.id.cantAnswers);
        backButton = view.findViewById(R.id.backButton);
        warningInfo = view.findViewById(R.id.warningInfo);
        warningGender = view.findViewById(R.id.warningGender);
        warningEmail = view.findViewById(R.id.warningEmail);


        instagramAuthViewModel = ViewModelProviders.of(this).get(InstagramAuthViewModel.class);

        instagramAuthViewModel.getResponseLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textInsta.setTextColor(Color.parseColor("#4285F4"));
                textInsta.setText("@" + s);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String str = "{}";
                        str = Tools.putValue(str, Tools.getString(R.string.instagram, getContext()), s);
                        Profile.setData(Tools.getString(R.string.social_networks, getContext()), str, getActivity());
                    }
                }).start();
            }
        });

        deleteButtons.get(0).setVisibility(View.GONE);
        for (int i = 0; i < 5; i++) {
            int finalI = i;

            addButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Profile.getCantPublicPhotos() <= finalI) {

                        if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                            ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                            return;
                        }

                        passToPickImageActivity(Profile.getCantPublicPhotos() + 1, true, true);

                    } else {

                        MotionLayout motionLayout = motions.get(finalI);
                        if (motionLayout.getCurrentState() == R.id.start) {
                            motionLayout.transitionToEnd();
                            icons.get(finalI).setImageResource(R.drawable.ic_close);
                        } else {
                            motionLayout.transitionToStart();
                            icons.get(finalI).setImageResource(R.drawable.ic_edit_mini);
                        }
                    }
                }
            });

            changeButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                        return;
                    }
                    /*int from = finalI + 2;
                    if (Profile.getCantPublicPhotos() - 1 < finalI)
                        from = Profile.getCantPublicPhotos() + 2;*/
                    passToPickImageActivity(finalI + 1, false, false);

                }
            });


            editButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                        return;
                    }
                    EventBus.getDefault().post(new LoadingShowEvent());
                    Glide.with(getContext())
                            .asBitmap()
                            .load(Profile.getPublicPhotos(getContext()).get(finalI))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    String file = "public" + finalI + Tools.getString(R.string.jpeg, getContext());
                                    String path = getActivity().getFilesDir().getAbsolutePath() + "/" + file;
                                    Tools.write(file, resource, getContext());
                                    Intent intent = new Intent(getActivity(), EditImage.class);
                                    intent.putExtra("path", path);
                                    intent.putExtra("from", finalI + 2);
                                    startActivity(intent);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EventBus.getDefault().post(new LoadingDismissEvent());
                                        }
                                    });
                                }

                                @Override
                                public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    EventBus.getDefault().post(new LoadingDismissEvent());
                                    //Toast.makeText(getContext(), "Intento Fallido...", Toast.LENGTH_SHORT).show();
                                    ProToast.makeText(getContext(), R.drawable.ic_info, getString(R.string.intento_fallido), Toast.LENGTH_SHORT);
                                }
                            });
                }
            });

            deleteButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Tools.isConnected(getContext())) {
                        ProToast.makeText(getContext(), R.drawable.offline, getString(R.string.sin_conexion), Toast.LENGTH_SHORT);
                        return;
                    }
                    EventBus.getDefault().post(new LoadingShowEvent());
                    String myAddress = Tools.getAddress(Profile.getServer(), getContext());
                    CRequest request = new CRequest(getContext(), Tools.getString(R.string.http, getContext()) + myAddress + "/" + deletePhoto(),
                            20);
                    ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                    pairs.add(new Pair<>(Tools.getString(R.string.id, getContext()), Profile.getId()));
                    pairs.add(new Pair<>(Tools.getString(R.string.auth, getContext()), pass()));
                    pairs.add(new Pair<>("no", (finalI + 1) + ""));
                    pairs.add(new Pair<>("cant", Profile.getCantPublicPhotos() + ""));
                    request.makeRequest(pairs);
                    request.setOnResponseListener(new CRequest.OnResponseListener() {
                        @Override
                        public void onResponse(String response) {
                            EventBus.getDefault().post(new LoadingDismissEvent());
                            // Log.d("DeletePhoto_",response);
                            if (response.trim().isEmpty()) {
                                Profile.setCantPhotos(Profile.getCantPublicPhotos() - 1);
                                Profile.updateProfileData(getContext());
                                EventBus.getDefault().post(new UpdatePublicPhotoEvent(true));
                                EventBus.getDefault().post(new LoadingDismissEvent());
                            }
                        }

                        @Override
                        public void onError(String error) {
                            EventBus.getDefault().post(new LoadingDismissEvent());
                            ProToast.makeText(getContext(), R.drawable.ic_info, getString(R.string.intento_fallido), Toast.LENGTH_SHORT);
                        }
                    });
                }
            });

            int finalI1 = i;
            roundedImageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (finalI1 < Profile.getCantPublicPhotos())
                        openPersonDialog(Profile.person);
                    else {
                        if (!MainActivity.hasPermissions(getContext(), MainActivity.PERMISSIONS)) {
                            ActivityCompat.requestPermissions(getActivity(), MainActivity.PERMISSIONS, MainActivity.PERMISSION_ALL);
                            return;
                        }

                        passToPickImageActivity(Profile.getCantPublicPhotos() + 1, true, true);
                    }
                }
            });

        }


        actionInterface = new InstagramAuthFragmentDialog.ActionInterface() {
            @Override
            public void codeRecived(String code) {
                Log.d("allksdjañ2kdald", "code " + code);
                instagramAuthViewModel.getAccesToken(getContext(), code);
            }
        };


        infoPersonalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalInfoBottDialog.showDialog(getActivity());

            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderDialog.showDialog(getActivity(), textGender);
            }
        });

        orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationDialog.showDialog(getActivity(), textOrientation);

            }
        });


        tagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagBottomDialog.showDialog(getActivity());

            }
        });

        chipGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagBottomDialog.showDialog(getActivity());
            }
        });

        chipGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagBottomDialog.showDialog(getActivity());
            }
        });

        occupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                occupationDialog.showDialog(getActivity(), textOccupation);
            }
        });

        zodiac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalInfoBottDialog.showDialog(getActivity());
            }
        });

        aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutMeDialog.showDialog(getActivity(), textAboutMe);
            }
        });


        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialogSurvey.showDialog(getActivity());

            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // instaDialog.showDialog(getActivity(), textInsta);
                openInstagramAuth();
            }
        });

        textInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String social = Profile.getSocialNetworks();
                if (!social.isEmpty()) {
                    String username = Tools.getValue(social, Tools.getString(R.string.instagram, getContext()));
                    if (!username.isEmpty()) {
                        Uri uri;
                        if (Tools.isAppInstalled(getContext(), "com.instagram.android"))
                            uri = Uri.parse("http://instagram.com/_u/" + username);
                        else
                            uri = Uri.parse("http://instagram.com/" + username);
                        getActivity().startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), MainActivity.REQUEST_INSTAGRAM);
                    } else openInstagramAuth();

                } else openInstagramAuth();

            }

        });

        if (Profile.getUser(getContext()).contains("facebook") || Profile.getUser(getContext()).contains("gmail"))
            email.setVisibility(View.GONE);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EmailDialog().showDialog(getActivity());
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openProfileFragment();
            }
        });

        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new UpdatePublicPhotoEvent(false));
        loadData();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        title.setText(getTitleLabel());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadBasicInfo(BasicInfoEvent event) {

        String name = Profile.getName();
        String email = Profile.getEmail();
        String city = Profile.getCity();
        String country = Profile.getCountry();
        String birth = Profile.getBirth();
        String gender = Profile.getGender();

        textName.setText(Profile.getName());

        if (!city.isEmpty())
            textCity.setText(city + ", " + country);

        if (!email.isEmpty()) {
            textEmail.setText(email);
            textEmail.setTextColor(Color.BLACK);
            warningEmail.setVisibility(View.GONE);
        }
        if (!birth.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //  Log.d("kajshdkad", birth+" a("+birth.substring(0, 4)+" /"+birth.substring(4, 6)+"/"+birth.substring(6, 8));
                    int year = Integer.parseInt(birth.substring(0, 4));
                    int month = Integer.parseInt(birth.substring(4, 6));
                    int day = Integer.parseInt(birth.substring(6, 8));

                    int age = Tools.getAge(year, month, day);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null)
                                textZodiac.setTextColor(getActivity().getResources().getColor(R.color.black));
                            textZodiac.setText(Tools.getZodiac(day, month, getContext()));
                            if (age != 0)
                                textName.setText(name + ", " + age);
                            if (!city.isEmpty())
                                warningInfo.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

        }


        if (!gender.isEmpty()) {
            textGender.setText(getGender(Integer.parseInt(gender)));
            if (!Profile.getSearchBy().isEmpty()) {
                warningGender.setVisibility(View.GONE);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadSurvey(SurveyEvent event) {
        String survey = Profile.getSurvey();

        if (!survey.isEmpty()) {
            final short[] total_survey = {4};
            final String[] questions = {"question1", "question2", "question3", "question4"};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String str : questions) {
                        if (!Tools.getValue(survey, str).isEmpty())
                            total_survey[0]--;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (total_survey[0] != 0) {
                                cantAnswers.setVisibility(View.VISIBLE);
                                cantAnswers.setText(total_survey[0] + getString(R.string._sin_responder));
                            } else
                                cantAnswers.setVisibility(View.INVISIBLE);

                        }
                    });

                }
            }).start();


        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadPreferences(PreferencesEvent event) {
        String preferences = Profile.getPreferences();
        if (!preferences.isEmpty()) {
            String[] tags = preferences.split("///");
            chipGuide.setVisibility(View.GONE);
            chipGroup.removeAllViews();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String tag : tags) {
                        Chip chip = new Chip(getActivity());
                        chip.setText(tag);
                        chip.setChipBackgroundColorResource(R.color.dBackground);
                        chip.setCloseIconVisible(false);
                        chip.setTextColor(Color.BLACK);
                        TextViewCompat.setTextAppearance(chip, R.style.chipFontF);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tagBottomDialog.showDialog(getActivity());
                                    }
                                });
                                chipGroup.addView(chip);
                            }
                        });
                    }
                }
            }).start();


        }
    }

    public void loadData() {
        String orientation = Profile.getOrientation();
        String occupation = Profile.getOccupation();
        String aboutMe = Profile.getAboutMe();
        String social = Profile.getSocialNetworks();

        EventBus.getDefault().post(new BasicInfoEvent());

        if (!orientation.isEmpty())
            textOrientation.setText(getOrientation(Integer.parseInt(orientation)));


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!occupation.isEmpty()) {
                    String str = Tools.getValue(occupation, Tools.getString(R.string.occupation, getContext()));
                    textOccupation.setTextColor(getResources().getColor(R.color.black));
                    if (str.contentEquals(Tools.getString(R.string.work, getContext()))) {
                        Log.d("Log_", "work");
                        String work_center = Tools.getValue(occupation, Tools.getString(R.string.work_center, getContext()));
                        String job = Tools.getValue(occupation, Tools.getString(R.string.job, getContext()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!work_center.isEmpty())
                                    textOccupation.setText(getString(R.string.trabajo_en_) + work_center);
                                else
                                    textOccupation.setText(getString(R.string.trabajo));

                                if (!job.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + getString(R.string._como_) + job);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "\uD83D\uDCBC");
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        String study_center = Tools.getValue(occupation, Tools.getString(R.string.study_center, getContext()));
                        String course = Tools.getValue(occupation, Tools.getString(R.string.course, getContext()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!course.isEmpty())
                                    textOccupation.setText(getString(R.string.estudio_) + course);
                                else
                                    textOccupation.setText(getString(R.string.estudio));
                                if (!study_center.isEmpty())
                                    textOccupation.setText(textOccupation.getText().toString() + getString(R.string._en_) + study_center);

                                textOccupation.setText(textOccupation.getText().toString() + " " + "📚");//"\uD83C\uDF93";
                                textOccupation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        }).start();


        if (!aboutMe.isEmpty()) {
            if (getActivity() != null)
                textAboutMe.setTextColor(getActivity().getResources().getColor(R.color.black));
            textAboutMe.setText(aboutMe);
        }


        if (!social.isEmpty()) {
            String username = Tools.getValue(social, Tools.getString(R.string.instagram, getContext()));
            if (!username.isEmpty()) {
                textInsta.setTextColor(Color.parseColor("#4285F4"));
                textInsta.setText("@" + username);
            }
        }


        EventBus.getDefault().post(new PreferencesEvent());
        EventBus.getDefault().post(new SurveyEvent());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadPhotos(UpdatePublicPhotoEvent event) {
        Log.d("LoadPublicPhoto", "cant" + Profile.getCantPublicPhotos());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = Tools.getString(R.string.http, getContext()) + Tools.getAddress(Profile.getServer(), getContext()) + "/" + pathPhoto() + "/" + Profile.getId() + "/";

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < Profile.getCantPublicPhotos(); i++) {
                            if (motions.get(i).getCurrentState() == R.id.end)
                                motions.get(i).transitionToStart();
                            String url = str + "public" + (i + 1) + ".jpeg";
                            Log.d("PublicURl_", url);
                            if (!event.isReload()) {
                                request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                                        .setProgressiveRenderingEnabled(true)
                                        .setResizeOptions(resizeOptions)
                                        .disableMemoryCache()
                                        .build();
                                roundedImageViews.get(i).setImageRequest(request);
                            } else {
                                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                                //imagePipeline.clearCaches();
                                imagePipeline.evictFromCache(Uri.parse(url));
                                roundedImageViews.get(i).setImageURI(Uri.parse(url));

                            }


                            icons.get(i).setImageResource(R.drawable.ic_edit_mini);

                        }

                        for (int i = Profile.getCantPublicPhotos(); i < 5; i++) {
                            if (motions.get(i).getCurrentState() == R.id.end)
                                motions.get(i).transitionToStart();

                            roundedImageViews.get(i).setActualImageResource(R.drawable.info_camera);
                            icons.get(i).setImageResource(R.drawable.ic_round_add_24);
                            if (getActivity() != null)
                                icons.get(i).setColorFilter(getActivity().getResources().getColor(R.color.redSecundary));


                        }
                    }
                });

            }
        }).start();

    }

    private void openInstagramAuth() {
        InstagramAuthFragmentDialog instagramAuthFragmentDialog = new InstagramAuthFragmentDialog(actionInterface);

        instagramAuthFragmentDialog.show((getActivity()).getSupportFragmentManager(), "InstaAuth");

    }

    private String getGender(int index) {
        return getResources().getStringArray(R.array.genders_list)[index];
    }

    private String getOrientation(int index) {
        return getResources().getStringArray(R.array.sexs_list)[index];
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


    private String getTitleLabel() {

        if (!Profile.isSetupComplete()) {
            titleIcInfo.setVisibility(View.VISIBLE);
            return getString(R.string.completa_tu_perfil);

        }

        titleIcInfo.setVisibility(View.GONE);
        return getString(R.string.mi_profile);


    }

}