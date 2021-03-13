package com.example.mytags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Animation
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    //Layout
    private RecyclerView staggeredRv;
    private StaggeredRecyclerAdapter adapter;
    private StaggeredGridLayoutManager manager;

    //Activity
    private MainActivity activity;


    //popUp
    private Button cancelButton;
    private Dialog popupDialog;

    //Tag
    String tag ="issues";

    //Button
    private FloatingActionButton btnAddPhoto;
    private FloatingActionButton btnAddPhotoFromGallery;
    private FloatingActionButton btnAddVideo;
    private FloatingActionButton btnAddVideoFromGallery;
    private FloatingActionButton btnAddAudio;
    private FloatingActionButton btnAddFile;
    private FloatingActionButton btnPlus;
    private Boolean bBtnPlusClicked = false;


    // intent pour la gallery
    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE = 101;

    Uri imageUri;
    List<Row> lst = new ArrayList<>();

    //EditText
    EditText editTextTag ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow for menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        //bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        this.activity = this;

        // Set Animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Set Button
        btnAddPhoto = (FloatingActionButton) findViewById(R.id.BtnAddPhoto);
        btnAddPhotoFromGallery = (FloatingActionButton) findViewById(R.id.BtnAddPhotoFromGallery);
        btnAddVideo = (FloatingActionButton) findViewById(R.id.BtnAddVideo);
        btnAddVideoFromGallery = (FloatingActionButton) findViewById(R.id.BtnAddVideoFromGallery);
        btnAddAudio = (FloatingActionButton) findViewById(R.id.BtnAddAudio);
        btnAddFile = (FloatingActionButton) findViewById(R.id.BtnAddFile);
        btnPlus = (FloatingActionButton) findViewById(R.id.fab);

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               photoIntent();
           }
        });

        btnAddPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerieIntent();
            }
        });

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add video", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddVideoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerieIntent();
            }
        });

        btnAddAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add audio", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add file", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClicked();
            }
        });

        /**
        btnCharger = (FloatingActionButton) findViewById(R.id.BtnTakePhoto);
        btnCharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerieIntent();
            }
        });

        btnPhoto = (Button)findViewById(R.id.buttonPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoIntent();
            }
        });
        **/
        popupDialog = new Dialog(this);
    }

    /** Animations **/

    private void onAddButtonClicked() {
        setVisibility(bBtnPlusClicked);
        setAnimation(bBtnPlusClicked);
        bBtnPlusClicked = !bBtnPlusClicked;
    }

    private void setVisibility(Boolean clicked){
        int visibility = (clicked ? View.INVISIBLE : View.VISIBLE);
        btnAddPhoto.setVisibility(visibility);
        btnAddPhotoFromGallery.setVisibility(visibility);
        btnAddVideo.setVisibility(visibility);
        btnAddVideoFromGallery.setVisibility(visibility);
        btnAddAudio.setVisibility(visibility);
        btnAddFile.setVisibility(visibility);
    }

    private void setAnimation(Boolean clicked){
        Animation animBtnPlus = (clicked ? rotateClose : rotateOpen);
        Animation animBtnOther = (clicked ? toBottom : fromBottom);
        // Other buttons
        btnAddPhoto.startAnimation(animBtnOther);
        btnAddPhotoFromGallery.startAnimation(animBtnOther);
        btnAddVideo.startAnimation(animBtnOther);
        btnAddVideoFromGallery.startAnimation(animBtnOther);
        btnAddAudio.startAnimation(animBtnOther);
        btnAddFile.startAnimation(animBtnOther);

        // Plus button
        btnPlus.startAnimation(animBtnPlus);
    }




    /**LES INTENTS **/

    //GALERIE INTENT
    private void galerieIntent(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    //INTENT CAMERA ET GESTION DES URIS DE CAMERAS
    /********************/
    String currentPhotoPath;

    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Appel intent chargement de la galerie
    private void photoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }

    }
    /********************/




    /**GESTION DES RETOURS D'INTENT DANS MAIN**/

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Depuis intent Galerie
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            /*
            imageUri = data.getData();
            lst.add(new Row(imageUri));
            staggeredRv = findViewById(R.id.staggered_rv);
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            staggeredRv.setLayoutManager(manager);
            adapter = new StaggeredRecyclerAdapter(MainActivity.this, lst);
            staggeredRv.setAdapter(adapter);
            */
            Button okButton;
            Button cancelButton;
            /*  popupDialog.setContentView(R.layout.add_popup);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.add_popup, null);
            cancelButton = (Button) popupDialog.findViewById(R.id.annuler_popup_button);
            okButton = (Button) popupDialog.findViewById(R.id.ajouter_image_popup_button);
            */
            AlertDialog.Builder tagPopup = new AlertDialog.Builder(activity);
            tagPopup.setTitle("Tags");
            tagPopup.setMessage("Ajouter un tag au média");
            tagPopup.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getApplicationContext(), "valeur du tag", Toast.LENGTH_SHORT ).show;

                }
            });


            /*
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupDialog.dismiss();
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("set1");
                    editTextTag = (EditText) v.findViewById(R.id.textTag);
                    String tag2 = editTextTag.getText().toString();

                    System.out.println(tag2);
                    System.out.println("set2");
                        //uri
                        lst.add(new Row(imageUri));
                        staggeredRv = findViewById(R.id.staggered_rv);
                        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        staggeredRv.setLayoutManager(manager);
                        adapter = new StaggeredRecyclerAdapter(MainActivity.this, lst);
                        staggeredRv.setAdapter(adapter);
                    System.out.println(tag);
                    popupDialog.dismiss();
                }
            });
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();
            */

        }

        //Depuis intent appareil photo
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            imageUri = Uri.parse(currentPhotoPath);
            /*
            lst.add(new Row(imageUri));
            staggeredRv = findViewById(R.id.staggered_rv);
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            staggeredRv.setLayoutManager(manager);
            adapter = new StaggeredRecyclerAdapter(MainActivity.this, lst);
            staggeredRv.setAdapter(adapter);
            */
            Button okButton;
            Button cancelButton;
            popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupDialog.setContentView(R.layout.add_popup);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.add_popup, null);
            cancelButton = (Button) popupDialog.findViewById(R.id.cancel_popup_button);
            okButton = (Button) popupDialog.findViewById(R.id.add_image_popup_button);
            editTextTag = (EditText) view.findViewById(R.id.textTag);

            ImageView image_preview = (ImageView) popupDialog.findViewById(R.id.imagePreview);
            image_preview.setImageURI(imageUri);

            //if user clicks on cancel
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupDialog.dismiss();
                }
            });
            //if user click ok => save
            // picture Uri and Tags string
            okButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    tag = editTextTag.getText().toString();
                    System.out.println(tag);
                    if (tag.isEmpty()) {
                        System.out.println("it works");
                    }else{
                        System.out.println("it doesn't works");
                    }


                    //uri
                    lst.add(new Row(imageUri));
                    staggeredRv = findViewById(R.id.staggered_rv);
                    manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    staggeredRv.setLayoutManager(manager);
                    adapter = new StaggeredRecyclerAdapter(MainActivity.this, lst);
                    staggeredRv.setAdapter(adapter);
                    popupDialog.dismiss();
                }
            });
            //popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();

        }
    }


}