package com.example.mytags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {
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
    String currentTag ="";

    //Button
    Button btnCharger;
    Button btnPhoto;
    ImageView searchButton;

    //intent id
    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE = 101;


    Uri imageUri;
    List<Row> currentListe = new ArrayList<>();

    //EditText
    EditText editTextTag ;

    //DATABASE
    SQLiteDatabase mediaDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activity = this;

      


        //Load an image
        btnCharger = (Button)findViewById(R.id.buttonCharger);
        btnCharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerieIntent();
            }
        });

        //Take a photo
        btnPhoto = (Button)findViewById(R.id.buttonPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoIntent();
            }
        });

        //Search media by Tag
        searchButton = (ImageView)findViewById(R.id.imageViewSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllImage();
            }
        });

        popupDialog = new Dialog(this);
    }

    /**Intents **/

    //GALERY INTENTS
    private void galerieIntent(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    //Camera Intent and URI manager
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

    //Intent Call and Gallery Load
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
    //Load all images from database
    private void loadAllImage(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAll();
        //3 - Create Row

        List<Row> liste = new ArrayList<>();

        for (Media media : mediaAll) {
            System.out.println(media);
            Uri imageUri = Uri.parse(media.getUri());
            liste.add(new Row(imageUri));

        }
        Integer i = liste.size();
        showMessage(i.toString());
       //4- affichage
        staggeredRv = findViewById(R.id.staggered_rv);
        manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        adapter = new StaggeredRecyclerAdapter(MainActivity.this,liste);
        staggeredRv.setAdapter(adapter);

    }


    /********************/




    /**Return of intent management**/

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Back From GALLERY
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){

            imageUri = data.getData();



            Button okButton;
            Button cancelButton;
            //Dialog to write associate media and Tag
            popupDialog.setContentView(R.layout.add_popup);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.add_popup, null);
            cancelButton = (Button) popupDialog.findViewById(R.id.annuler_popup_button);
            okButton = (Button) popupDialog.findViewById(R.id.ajouter_image_popup_button);
            editTextTag = (EditText) popupDialog.findViewById(R.id.textTag);

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

                    currentTag = editTextTag.getText().toString();
                    Media mediaModel;

                    if (!currentTag.isEmpty()) {
                        //uri
                        mediaModel = new  Media(-1,imageUri.toString(), currentTag);


                        currentListe.add(new Row(imageUri));
                        staggeredRv = findViewById(R.id.staggered_rv);
                        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        staggeredRv.setLayoutManager(manager);
                        adapter = new StaggeredRecyclerAdapter(MainActivity.this,currentListe);
                        staggeredRv.setAdapter(adapter);
                        popupDialog.dismiss();
                        showMessage("Tag : "+currentTag+" Ajouté");
                        //DataBase call
                        DataBaseArch dataBaseArch = new DataBaseArch(MainActivity.this);
                        //insert
                        boolean success = dataBaseArch.addOne(mediaModel);
                        showMessage(imageUri.toString());


                    }



                }
            });
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();


        }

        //Back from photo
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            imageUri = Uri.parse(currentPhotoPath);

            Button okButton;
            Button cancelButton;
            //Dialog to write associate media and Tag
            popupDialog.setContentView(R.layout.add_popup);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.add_popup, null);
            cancelButton = (Button) popupDialog.findViewById(R.id.annuler_popup_button);
            okButton = (Button) popupDialog.findViewById(R.id.ajouter_image_popup_button);
            editTextTag = (EditText) popupDialog.findViewById(R.id.textTag);

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

                    currentTag = editTextTag.getText().toString();
                    Media mediaModel;

                    if (!currentTag.isEmpty()) {
                        //uri
                        mediaModel = new  Media(-1,imageUri.toString(), currentTag);

                        currentListe.add(new Row(imageUri));
                        staggeredRv = findViewById(R.id.staggered_rv);
                        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        staggeredRv.setLayoutManager(manager);
                        adapter = new StaggeredRecyclerAdapter(MainActivity.this, currentListe);
                        staggeredRv.setAdapter(adapter);
                        popupDialog.dismiss();
                        System.out.println(imageUri.toString());
                        System.out.println(Uri.parse(imageUri.toString()));
                        showMessage("Tag : "+currentTag+" Ajouté");
                    }



                }
            });
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();

        }
    }
        //Get the media type
        public String mediaType (String uri){
            String type = uri.substring(uri.toString().length() - 3);
            return type;
        }
        //Short message for successful task
        private void showMessage (String message){
            Toast toast = Toast.makeText(activity,message,Toast.LENGTH_SHORT);
            toast.show();
        }

}