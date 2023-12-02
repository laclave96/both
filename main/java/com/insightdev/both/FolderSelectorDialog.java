package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FolderSelectorDialog {


    public interface ActionInterface {
         void methodOk(int pos);
    }

    ActionInterface actionInterface;

    // List<String> folders = new ArrayList<>();
    int sizeListFolders = 0;

    ArrayList<ImageFolder> folders = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showDialog(Activity activity, ArrayList<ImageFolder> imageFolders, ActionInterface actionInterface) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        dialog.setContentView(R.layout.folder_selector);

        this.actionInterface = actionInterface;


        for (ImageFolder imageFolder : imageFolders)
            folders.add(0, imageFolder);

        RecyclerView listView = dialog.findViewById(R.id.list);

        AlbumAdapter albumAdapter = new AlbumAdapter(folders, activity);

        listView.setLayoutManager(new GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false));

        listView.setAdapter(albumAdapter);

        EditText searchView = dialog.findViewById(R.id.searchView);

        ImageView icon = dialog.findViewById(R.id.iconSearch);

        ImageView close = dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        sizeListFolders = imageFolders.size() - 1;


        /*listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });*/

        listView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of
                // child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                actionInterface.methodOk(position);

                dialog.dismiss();

            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_off));
                    icon.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            icon.setImageResource(R.drawable.ic_round_close_24);
                        }
                    }, 300);
                }
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  albumAdapter.getFilter().filter(s);
                if (s.toString().length() > 0 & icon.getVisibility() == View.INVISIBLE) {

                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_in));
                    icon.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & icon.getVisibility() == View.VISIBLE) {

                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_off));
                    icon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = PickImage.screenHeight;
        bottomSheet.setLayoutParams(layoutParams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }
}

