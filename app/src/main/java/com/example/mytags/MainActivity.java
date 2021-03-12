package com.example.mytags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity {
    //Layout
    private RecyclerView staggeredRv;
    private StaggeredRecyclerAdapter adapter;
    private StaggeredGridLayoutManager manager;

    //Activity
    public static MainActivity activity;


    //popUp
    private Button cancelButton;
    private Dialog popupDialog;

    //Tag
    String currentTag ="";

    //Button
    Button btnCharger;
    Button btnPhoto;
    Button btnDelete;
    Button btnImage;
    ImageView searchButton;

    //intent id
    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE = 101;
    public static final int PERMISSION_REQUEST_CODE = 200;


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

        //Permissions
        if(!Util.checkPermission(MainActivity.this))
        {
            Util.requestPermission(MainActivity.this);
        }else {
            Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show();
        }


      


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
                EditText tagSearch = (EditText)findViewById(R.id.tag_search_field);
                showMessage(tagSearch.getText().toString());
                searchWithtag(tagSearch.getText().toString());
            }
        });

        //Show all images
        btnImage = (Button)findViewById(R.id.buttonImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllImage();
            }
        });

        //Empty DataBase Test
        btnDelete = (Button)findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
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
        List<Media> mediaAll = dataBaseArch.selectAllImages();
        //3 - Create Row

        List<Row> liste = new ArrayList<>();

        for (Media media : mediaAll) {
            Uri imageUri = Uri.parse(media.getUri());
            liste.add(new Row(imageUri));
        }
        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
       //4- affichage
        staggeredRv = findViewById(R.id.staggered_rv);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        adapter = new StaggeredRecyclerAdapter(MainActivity.this,liste);
        staggeredRv.setAdapter(adapter);

    }
    //Load Image With Tag
    private void searchWithtag(String tagSearch){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectFromTag(tagSearch);
        //3 - Create Row
        List<Row> liste = new ArrayList<>();

        for (Media media : mediaAll) {
            Uri imageUri = Uri.parse(media.getUri());
            liste.add(new Row(imageUri));
        }
        Integer numberElements = liste.size();
        if(numberElements == 0){
            showMessage("Aucun Fichier Trouvé");
        }else{
            showMessage(numberElements.toString() + " fichiers");
        }

        //4- affichage
        staggeredRv = findViewById(R.id.staggered_rv);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        adapter = new StaggeredRecyclerAdapter(MainActivity.this,liste);
        staggeredRv.setAdapter(adapter);
    }

    //Delete All entries
    private void deleteAll(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        dataBaseArch.deleteAll();
        //3 - Create Row
        //TODO REFRESH SCREEN

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
                        String mediaType = getMimeType(MainActivity.this, imageUri);
                        mediaModel = new  Media(-1,imageUri.toString(),mediaType, currentTag);


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
                        String mediaType = getMimeType(imageUri.toString());
                        mediaModel = new  Media(-1,imageUri.toString(),mediaType, currentTag);

                        currentListe.add(new Row(imageUri));

                            staggeredRv = findViewById(R.id.staggered_rv);
                            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                            staggeredRv.setLayoutManager(manager);
                            adapter = new StaggeredRecyclerAdapter(MainActivity.this, currentListe);


                        popupDialog.dismiss();
                        showMessage("Tag : "+currentTag+" Ajouté");
                        //DataBase call
                        DataBaseArch dataBaseArch = new DataBaseArch(MainActivity.this);
                        //insert
                        boolean success = dataBaseArch.addOne(mediaModel);
                    }
                }
            });
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupDialog.show();

        }
    }
                //Get mediaType
                public static String getMimeType(String url)
                {
                    String extension = url.substring(url.lastIndexOf("."));
                    String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
                    return mimeType;
                }

                //Short message for successful task
                private void showMessage (String message){
                    Toast toast = Toast.makeText(activity,message,Toast.LENGTH_SHORT);
                    toast.show();
                }

                //get MimeType for galery
                public String getMimeType(Context context, Uri uri) {
                    String mimeType = null;
                    if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                        ContentResolver cr = context.getContentResolver();
                        mimeType = cr.getType(uri);
                    } else {
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                                .toString());
                        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                fileExtension.toLowerCase());
                    }
                    return mimeType;
                }

                //Permission Request FUNCTIONS
                @Override
                public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                    switch (requestCode) {
                        case Util.PERMISSION_REQUEST_CODE:
                            if (grantResults.length > 0) {

                                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                                boolean readAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                                boolean writeAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                                if (locationAccepted && cameraAccepted && readAccepted && writeAccepted) {

                                    // All Permissions Granted
                                } else {

                                    //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                            showMessageOKCancel("You need to allow access to all the permissions",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                        Util.PERMISSION_REQUEST_CODE);
                                                            }
                                                        }
                                                    });
                                            return;
                                        }
                                    }

                                }
                            }


                            break;
                    }
                }

                private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(message)
                            .setPositiveButton("OK", okListener)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
}


