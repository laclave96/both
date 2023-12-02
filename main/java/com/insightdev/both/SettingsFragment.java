package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.jem.rubberpicker.RubberRangePicker;
import com.jem.rubberpicker.RubberSeekBar;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {


    private RelativeLayout notifications, faqs, closeSession, deleteAccount, searchLayout, autoPaylayout;
    private ImageView backButton;
    private String min_age, max_age, distance;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        notifications = view.findViewById(R.id.notifications);
        searchLayout = view.findViewById(R.id.ajustesBusqueda);
        autoPaylayout = view.findViewById(R.id.autoBill);
        faqs = view.findViewById(R.id.faqs);
        closeSession = view.findViewById(R.id.closeSession);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        backButton = view.findViewById(R.id.backButton);

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openProfileFragment();
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterDialog().showDialog(getActivity(), 0);
            }
        });
        autoPaylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoBillingDialogFragment autoBillingDialogFragment = new AutoBillingDialogFragment();

                autoBillingDialogFragment.show((getActivity()).getSupportFragmentManager(), "autoBillingDialogFragment");
            }
        });


        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NotificationsDialog().showDialog(getActivity());

            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FaqDialog().showDialog(getActivity());
            }
        });

        closeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CloseSessionDialog().showDialog(getActivity());
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteAccDialog().showDialog(getActivity());
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("On_", "OnStartSettings");

    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void saveGender(int pos) {
        Tools.saveSettings("gender", pos + "", getContext());
    }





}