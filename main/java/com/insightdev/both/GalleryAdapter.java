package com.insightdev.both;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.SizeReadyCallback;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemHolder> {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    Camera mCamera;


    private OnItemCLickListener mListener;
    Context context;

    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }

    public GalleryAdapter(ArrayList<PictureFacer> galleryItems, Context context) {
        // this.galleryItems = new ArrayList<>(galleryItems);

        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public GalleryItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new GalleryItemHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull GalleryItemHolder holder, int position) {
        // Log.d("klasjdl", galleryItems.size() + "");

        if (position == 0) {
            holder.imageView.setVisibility(View.GONE);
            holder.textureView.setVisibility(View.VISIBLE);
            holder.cameraLayout.setVisibility(View.VISIBLE);
            holder.cameraIcon.setVisibility(View.VISIBLE);
            holder.galery.setVisibility(View.GONE);
            holder.more.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);

        } else {
            Glide.with(context).load(PickImage.galleryItems.get(position).getImageUri()).centerCrop().into(holder.imageView);

            holder.imageView.setVisibility(View.VISIBLE);
            holder.galery.setVisibility(View.GONE);
            holder.textureView.setVisibility(View.GONE);
            holder.cameraIcon.setVisibility(View.GONE);
            holder.cameraLayout.setVisibility(View.GONE);
            holder.more.setVisibility(View.GONE);


            if (PickImage.multipleSelection)
                holder.checkBox.setVisibility(View.VISIBLE);
            else
                holder.checkBox.setVisibility(View.GONE);

            if (PickImage.galleryItems.get(position).getSelected())
                holder.checkBox.setChecked(true, false);
            else
                holder.checkBox.setChecked(false, false);

        }

    }


    @Override
    public int getItemCount() {
        return PickImage.galleryItems.size();
    }


    public class GalleryItemHolder extends RecyclerView.ViewHolder implements TextureView.SurfaceTextureListener {


        AnimatedCheckBox checkBox;
        CardView cardView;
        ImageView cameraIcon;
        AutoFitTextureView textureView;
        RoundedImageView imageView;
        RoundedImageView cameraLayout, galery;
        TextView more;


        @SuppressLint("ClickableViewAccessibility")

        public GalleryItemHolder(@NonNull View itemView, final OnItemCLickListener itemCLickListener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            checkBox = itemView.findViewById(R.id.check);
            checkBox.setClickable(false);
            cardView.getLayoutParams().height = (int) (PickImage.galleryHeight / 3.9);
            imageView = itemView.findViewById(R.id.mainImage);
            textureView = itemView.findViewById(R.id.cameraV);
            textureView.getLayoutParams().height = (int) (PickImage.galleryHeight / 3.9);
            galery = itemView.findViewById(R.id.galleryLayout);
            cameraLayout = itemView.findViewById(R.id.cameraLayout);
            cameraIcon = itemView.findViewById(R.id.icon);
            more = itemView.findViewById(R.id.more);
            textureView.setAspectRatio(9, 16);
            textureView.setSurfaceTextureListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    itemCLickListener.OnItemClick(pos);

                    if (PickImage.multipleSelection) {
                        if (PickImage.galleryItems.get(pos).getSelected()) {
                            PickImage.galleryItems.get(pos).setSelected(true);
                            checkBox.setChecked(true, true);
                        } else {
                            PickImage.galleryItems.get(pos).setSelected(false);
                            checkBox.setChecked(false, true);
                        }

                    }

                }
            });

        }


        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            if (getAdapterPosition() == 0) {
                openCamera(surface);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            if (getAdapterPosition() == 0) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        mCamera.release();

                    }
                });

            }

            return false;

        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }

        public void openCamera(SurfaceTexture surface) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCamera = Camera.open(0);

                        mCamera.setPreviewTexture(textureView.getSurfaceTexture());
                        mCamera.setDisplayOrientation(ORIENTATIONS.get(0));
                        Camera.Parameters params = mCamera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        mCamera.setParameters(params);
                        mCamera.startPreview();
                    } catch (Exception e) {
                    }
                }
            });


        }
    }

}
