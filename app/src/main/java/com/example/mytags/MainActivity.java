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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

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
    String currentTag ="";//another change to commit

    //Button
    private FloatingActionButton btnAddPhoto;
    private FloatingActionButton btnAddFromGallery;
    private FloatingActionButton btnAddVideo;
    private FloatingActionButton btnAddVideoFromGallery;
    private FloatingActionButton btnAddAudio;
    private FloatingActionButton btnAddFile;
    private FloatingActionButton btnPlus;
    private Boolean bBtnPlusClicked = false;
    private View btnMenuTag;


    // Intent Select code
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE = 101;
    private static final int REQUEST_AUDIO = 102;
    private static final int REQUEST_VIDEO = 103;
    private static final int REQUEST_DOCUMENT = 104;

    //EditText
    EditText editTextTag ;

    //Uri
    Uri imageUri;
    Uri audioUri;
    Uri documentUri;
    Uri videoUri;
    List<Media> currentList = new ArrayList<>();

    // Search
    AutoCompleteTextView searchView;

    TextView no_tag_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;

        //Check all Permissions
        if(!Util.checkPermission(MainActivity.this))
        {
            Util.requestPermission(MainActivity.this);
        }else {
            Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show();
        }

        //Load all Media on create()
        loadAllMedia();

        // Remove shadow for menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        // Set Animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Set Button
        btnAddPhoto = (FloatingActionButton) findViewById(R.id.BtnAddPhoto);
        btnAddFromGallery = (FloatingActionButton) findViewById(R.id.BtnAddFromGallery);
        btnAddVideo = (FloatingActionButton) findViewById(R.id.BtnAddVideo);
        btnAddVideoFromGallery = (FloatingActionButton) findViewById(R.id.BtnAddVideoFromGallery);
        btnAddAudio = (FloatingActionButton) findViewById(R.id.BtnAddAudio);
        btnAddFile = (FloatingActionButton) findViewById(R.id.BtnAddFile);
        btnPlus = (FloatingActionButton) findViewById(R.id.fab);

        btnMenuTag = findViewById(R.id.menu_tag);
        btnMenuTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenuTag.setActivated(true);
                Intent intent = new Intent(MainActivity.this, SearchTagsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               photoIntent();
           }
        });

        btnAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galerieIntent();
            }
        });

        btnAddAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioIntent();
            }
        });

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentIntent();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClicked();
            }
        });

        String[] list = new String[] {"vacance", "vache", "passport", "chat"};

        searchView = (AutoCompleteTextView) findViewById(R.id.autocomplete_text_view);
        ArrayAdapter<String> adapter_string = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        searchView.setAdapter(adapter_string);


        popupDialog = new Dialog(this);
    }


    /*******MANAGE THE DATABASE REQUEST*******/

    //Load all media
    private void loadAllMedia(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAll();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load all images from database
    private void loadAllImage(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAllImages();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load all audio
    private void loadAllaudio(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAllAudio();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load all video
    private void loadAllVideo(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAllVideo();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load all text type
    private void loadAllText(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectDocumentTxt();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load all application type
    private void loadAllPdfApk(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectDocumentPdfApk();
        //3 - Create List of media to shows

        List<Media> liste = new ArrayList<>(mediaAll);

        Integer i = liste.size();
        showMessage(i.toString() + " fichiers");
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);

    }

    //Load Files With Tag
    private void searchWithtag(String tagSearch){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectFromTag(tagSearch);
        //3 - Create List of media to shows
        List<Media> liste = new ArrayList<>(mediaAll);

        Integer numberElements = liste.size();
        if(numberElements == 0){
            showMessage("Aucun Fichier Trouvé");
        }else{
            showMessage(numberElements.toString() + " fichiers");
        }

        //4- affichage
        updateActivity(R.id.staggered_rv,liste);
    }

    //Delete All entries
    private void deleteAll(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        dataBaseArch.deleteAll();
        //3 - Create Row
        updateActivity(R.id.staggered_rv, currentList);
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
        btnAddFromGallery.setVisibility(visibility);
        btnAddAudio.setVisibility(visibility);
        btnAddFile.setVisibility(visibility);
    }

    private void setAnimation(Boolean clicked){
        Animation animBtnPlus = (clicked ? rotateClose : rotateOpen);
        Animation animBtnOther = (clicked ? toBottom : fromBottom);
        // Other buttons
        btnAddPhoto.startAnimation(animBtnOther);
        btnAddFromGallery.startAnimation(animBtnOther);
        btnAddAudio.startAnimation(animBtnOther);
        btnAddFile.startAnimation(animBtnOther);

        // Plus button
        btnPlus.startAnimation(animBtnPlus);

        //Charge all dataBaseMedia

    }


    /**LES INTENTS **/

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

    //Choose a file Generic
    private void fileIntent(String fileType, int FILE_SELECT_CODE, String message){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType(fileType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, message), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showMessage("Installer un FileManager");
        }
    }

    //Choose audio file
    private void audioIntent(){
        fileIntent("audio/*",REQUEST_AUDIO,"Sélectionner le fichier audio !");
    }

    //Choose choose document
    private void documentIntent(){
        fileIntent("application/*|text/*",REQUEST_DOCUMENT,"Sélectionner le document !");
    }


    /********************/
    /**Return of intent management**/

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Back From GALLERY
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            String mediaType= getMimeType(MainActivity.this,imageUri);
            if(mediaType.contains("video")){
                imageUri = Uri.parse("android.resource://com.example.mytags/drawable/video");
                mediaType = "video";
            }
            else mediaType = "photo";

            fullRequest(R.id.staggered_rv,imageUri,imageUri, currentList,mediaType);
        }

        //Back from photo, show pop and update activity
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            imageUri = Uri.parse(currentPhotoPath);
            fullRequest(R.id.staggered_rv, imageUri, imageUri, currentList,"photo");
        }
        //Back from audio, show generic Audio image
        if (requestCode == REQUEST_AUDIO && resultCode == RESULT_OK) {
            audioUri = data.getData();
            imageUri = Uri.parse("android.resource://com.example.mytags/drawable/audio");
            fullRequest(R.id.staggered_rv, imageUri, audioUri, currentList,"audio");
            MainActivity.this.getContentResolver().takePersistableUriPermission(audioUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //Back from fileExplorer
        if (requestCode == REQUEST_DOCUMENT && resultCode == RESULT_OK) {
            documentUri = data.getData();
            imageUri = Uri.parse("android.resource://com.example.mytags/drawable/document");
            fullRequest(R.id.staggered_rv,imageUri,documentUri, currentList,"file");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MainActivity.this.getContentResolver().takePersistableUriPermission(documentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
    }

    /****HELPFULL FONCTIONS*****/
    //Get mediaType
    public static String getMimeType(String url)
    {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    //Short message for successful task
    public void showMessage (String message){
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

    //Update the activity with available Rows
    public void updateActivity(int activityId, List<Media> listeRow){
        no_tag_text = (TextView) findViewById(R.id.no_tag_error);
        if(currentList.isEmpty()) no_tag_text.setVisibility(View.VISIBLE);
        else no_tag_text.setVisibility(View.INVISIBLE);

        staggeredRv = findViewById(activityId);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        adapter = new StaggeredRecyclerAdapter(MainActivity.this, listeRow);
        staggeredRv.setAdapter(adapter);
        if(listeRow.size() == 0){
            Util.showMessage(this,"No files");
        }else{
            Util.showMessage(this,listeRow.size()+" file saved");
        }

    }

    //Create a photo and Update activity on Request
    public void fullRequest(int activityId, Uri imageUri,Uri fileUri,List<Media> ListeRow,String mediaType){
        Button okButton;
        Button cancelButton;
        //Dialog to write associate media and Tag

        popupDialog.setContentView(R.layout.add_popup);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.add_popup, null);
        cancelButton = (Button) popupDialog.findViewById(R.id.cancel_popup_button);
        okButton = (Button) popupDialog.findViewById(R.id.add_image_popup_button);
        editTextTag = (EditText) popupDialog.findViewById(R.id.textTag);

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

                currentTag = editTextTag.getText().toString();
                Media mediaModel = new  Media(-1,imageUri.toString(),fileUri.toString(),mediaType, currentTag);
                // On click media event
                currentList.add(mediaModel);

                updateActivity(activityId,ListeRow);

                showMessage("Tag : "+currentTag+" Ajouté");


                //DataBase call
                DataBaseArch dataBaseArch = new DataBaseArch(MainActivity.this);
                //insert
                boolean success = dataBaseArch.addOne(mediaModel);
                showMessage(""+success);
                popupDialog.dismiss();
            }

        });
        popupDialog.show();
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


