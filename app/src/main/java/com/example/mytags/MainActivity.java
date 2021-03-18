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
import android.util.Log;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
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

    //View
    SearchView searchBar;

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
    private static final int CAPTURE_VIDEO = 105;

    //EditText
    EditText editTextTag ;

    //Uri
    Uri imageUri;
    Uri audioUri;
    Uri documentUri;
    Uri videoUri;
    List<Media> currentList = new ArrayList<>();

    // Chip
    Chip chipHome;
    Chip chipFile;
    Chip chipAudio;
    Chip chipVideo;
    Chip chipImage;

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
           Util.showMessage(this,this.getString(R.string.all_Permission_ok));
        }

        //Take result from an intent coming from click on a chip, get the value and call dataBase
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            String value = extras.getString("value");
            String [] result = value.split(" ");
            //if code is Tag => use value to search Tag
            if(result[1].equals("SEARCH_TAG")){
                searchWithtag(result[0]);
                Log.d("res", result[0]);
            }
            //delete the file via Uri sent from adapter
            if(result[1].equals("DELETE_TAG")){
                deleteSelected(result[0]);
                //refresh
                Util.showMessage(this, getString(R.string.media_deleted));
                loadAllMedia();
            }
            //Update a tag
            if(result[1].equals("UPDATE_TAG")){
                updateTag(result[0],result[2]);
                //refresh
                Util.showMessage(this,getString(R.string.tag_updated));
                loadAllMedia();
            }
        }else{
            //Load all Media on create()
            loadAllMedia();
        }


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
        btnAddAudio = (FloatingActionButton) findViewById(R.id.BtnAddAudio);
        btnAddFile = (FloatingActionButton) findViewById(R.id.BtnAddFile);
        btnPlus = (FloatingActionButton) findViewById(R.id.fab);

        //Set chips
        chipHome = (Chip) findViewById(R.id.chip_home);
        chipFile = (Chip) findViewById(R.id.chip_file);
        chipAudio = (Chip) findViewById(R.id.chip_audio);
        chipVideo = (Chip) findViewById(R.id.chip_video);
        chipImage = (Chip) findViewById(R.id.chip_image);

        //Onclick Buttons
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
           public void onClick(View v) { photoIntent(); }
        });

        btnAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { galerieIntent(); }
        });

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {recordIntent();}
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

        //Onclick Chips
        chipHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseArch dataBaseArch = new DataBaseArch(activity);
                allChipDisabled();
                Chip chip = (Chip) v;
                chip.setChipBackgroundColorResource(R.color.green_pack);
                chipImage.setText(String.valueOf(dataBaseArch.selectCount("", "image")));
                chipVideo.setText(String.valueOf(dataBaseArch.selectCount("", "video")));
                chipAudio.setText(String.valueOf(dataBaseArch.selectCount("", "audio")));
                chipFile.setText(String.valueOf(dataBaseArch.selectCount("", "file")));

                loadAllMedia();
            }
        });
        chipAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChipDisabled();
                Chip chip = (Chip) v;
                chip.setChipBackgroundColorResource(R.color.green_pack);
                loadAllaudio();
            }
        });
        chipFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChipDisabled();
                Chip chip = (Chip) v;
                chip.setChipBackgroundColorResource(R.color.green_pack);
                loadAllDocuments();
            }
        });
        chipVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChipDisabled();
                Chip chip = (Chip) v;
                chip.setChipBackgroundColorResource(R.color.green_pack);
                loadAllVideo();
            }
        });
        chipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChipDisabled();
                Chip chip = (Chip) v;
                chip.setChipBackgroundColorResource(R.color.green_pack);
                loadAllImage();
            }
        });

       chipHome.callOnClick();


        //SearchBar implementation
       searchBar = (SearchView) findViewById(R.id.search);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                             @Override
                                             public boolean onQueryTextSubmit(String query) {
                                                 searchWithtag(query);
                                                 DataBaseArch dataBaseArch = new DataBaseArch(activity);
                                                 allChipDisabled();
                                                 chipHome.setChipBackgroundColorResource(R.color.green_pack);
                                                 chipImage.setText(String.valueOf(dataBaseArch.selectCount(query, "image")));
                                                 chipVideo.setText(String.valueOf(dataBaseArch.selectCount(query, "video")));
                                                 chipAudio.setText(String.valueOf(dataBaseArch.selectCount(query, "audio")));
                                                 chipFile.setText(String.valueOf(dataBaseArch.selectCount(query, "file")));
                                                 return false;
                                             }

                                             @Override
                                             public boolean onQueryTextChange(String query) {
                                                 DataBaseArch dataBaseArch = new DataBaseArch(activity);

                                                 if(query.isEmpty()){
                                                     loadAllMedia();
                                                     chipHome.setChipBackgroundColorResource(R.color.green_pack);
                                                     chipImage.setText(String.valueOf(dataBaseArch.selectCount("", "image")));
                                                     chipVideo.setText(String.valueOf(dataBaseArch.selectCount("", "video")));
                                                     chipAudio.setText(String.valueOf(dataBaseArch.selectCount("", "audio")));
                                                     chipFile.setText(String.valueOf(dataBaseArch.selectCount("", "file")));
                                                     return false;
                                                 }
                                                 searchWithtag(query);


                                                 allChipDisabled();
                                                 chipHome.setChipBackgroundColorResource(R.color.green_pack);
                                                 chipImage.setText(String.valueOf(dataBaseArch.selectCount(query, "image")));
                                                 chipVideo.setText(String.valueOf(dataBaseArch.selectCount(query, "video")));
                                                 chipAudio.setText(String.valueOf(dataBaseArch.selectCount(query, "audio")));
                                                 chipFile.setText(String.valueOf(dataBaseArch.selectCount(query, "file")));
                                                 return false;
                                             }
                                         });


        popupDialog = new Dialog(this);
    }

    void allChipDisabled() {
        chipHome.setChipBackgroundColorResource(R.color.grey);
        chipImage.setChipBackgroundColorResource(R.color.grey);
        chipVideo.setChipBackgroundColorResource(R.color.grey);
        chipAudio.setChipBackgroundColorResource(R.color.grey);
        chipFile.setChipBackgroundColorResource(R.color.grey);
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
        //4- affichage
        no_tag_text = (TextView) findViewById(R.id.no_tag_error);
        if(liste.isEmpty()) no_tag_text.setVisibility(View.VISIBLE);
        else no_tag_text.setVisibility(View.INVISIBLE);
        updateActivity(R.id.staggered_rv, liste);
    }

    //Load all images from database
    private void loadAllImage(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAllImages();
        //3 - Create List of media to shows
        List<Media> liste = new ArrayList<>(mediaAll);
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
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);
    }

    //Load all Documents type
    private void loadAllDocuments(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectAllDocument();
        //3 - Create List of media to shows
        List<Media> liste = new ArrayList<>(mediaAll);
        //4- affichage
        updateActivity(R.id.staggered_rv,liste);
    }


    //Load Files With Tag
    private void searchWithtag(String tagSearch){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        List<Media> mediaAll = dataBaseArch.selectExactTag(tagSearch);
        //3 - Create List of media to shows
        List<Media> liste = new ArrayList<>(mediaAll);
        Integer numberElements = liste.size();
        if(numberElements == 0){
            Util.showMessage(MainActivity.this,getString(R.string.no_files_found));
        }else{
            Util.showMessage(MainActivity.this,numberElements.toString() + " " + getString(R.string.file));
        }
        //4- Refresh
        updateActivity(R.id.staggered_rv,liste);

    }

    //Delete All entries
    private void deleteAll(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        dataBaseArch.deleteAll();
    }
    //Delete Selected Media
    private void deleteSelected(String fileUri){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        dataBaseArch.deleteMedia(fileUri);
    }

    //Update a tag
    private void updateTag(String tag ,String fileUri){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(activity);
        //2- get all images from database in a List of Media
        dataBaseArch.updateTagViaUri(tag ,fileUri);
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
        btnAddVideo.setVisibility(visibility);
        btnAddFromGallery.setVisibility(visibility);
        btnAddAudio.setVisibility(visibility);
        btnAddFile.setVisibility(visibility);
    }

    private void setAnimation(Boolean clicked){
        Animation animBtnPlus = (clicked ? rotateClose : rotateOpen);
        Animation animBtnOther = (clicked ? toBottom : fromBottom);
        // Other buttons
        btnAddPhoto.startAnimation(animBtnOther);
        btnAddVideo.startAnimation(animBtnOther);
        btnAddFromGallery.startAnimation(animBtnOther);
        btnAddAudio.startAnimation(animBtnOther);
        btnAddFile.startAnimation(animBtnOther);


        // Plus button
        btnPlus.startAnimation(animBtnPlus);

        //Charge all dataBaseMedia

    }


    /**INTENTS **/

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
            Util.showMessage(MainActivity.this,getString(R.string.install_file_manager));
        }
    }

    //Record video from camera
    private void recordIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO);
        }
    }

    //Choose audio file
    private void audioIntent(){
        fileIntent("audio/*",REQUEST_AUDIO,getString(R.string.select_audio_file));
    }

    //Choose choose document
    private void documentIntent(){
        fileIntent("application/*|text/*",REQUEST_DOCUMENT,getString(R.string.select_document));
    }



    /********************/
    /**Return of intent management**/

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Back From GALLERY
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri locationUri = data.getData();
            imageUri = data.getData();
            String mediaType= getMimeType(MainActivity.this,imageUri);
            if(mediaType.contains("video")){
                imageUri = Uri.parse("android.resource://com.example.mytags/drawable/video");
                mediaType = "video";
            }
            else mediaType = "image";

            fullRequest(R.id.staggered_rv,imageUri,locationUri, currentList,mediaType);
        }

        //Back from photo, show pop and update activity
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            imageUri = Uri.parse(currentPhotoPath);
            fullRequest(R.id.staggered_rv, imageUri, imageUri, currentList,"image");
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
        //Back from record Intent
        if (requestCode == CAPTURE_VIDEO && resultCode == RESULT_OK) {
            videoUri = data.getData();
            imageUri = Uri.parse("android.resource://com.example.mytags/drawable/video");
            fullRequest(R.id.staggered_rv,imageUri,videoUri,currentList,"video");
        }
    }

    /****HELPFULL FONCTIONS*****/
    //Get mediaType



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
                                showMessageOKCancel(getString(R.string.permission),
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
        staggeredRv = findViewById(activityId);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        adapter = new StaggeredRecyclerAdapter(MainActivity.this, listeRow);
        staggeredRv.setAdapter(adapter);
        if(listeRow.size() == 0){
            Util.showMessage(this,getString(R.string.no_files_found));
        }else{
            Util.showMessage(this,listeRow.size()+ getString(R.string.file_saved));
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
                Media mediaModel;

                mediaModel = new  Media(-1,imageUri.toString(),fileUri.toString(),mediaType, currentTag);
                //TODO ONclick media event
                currentList.add(mediaModel);
                //DataBase call
                DataBaseArch dataBaseArch = new DataBaseArch(MainActivity.this);
                //insert
                boolean success = dataBaseArch.addOne(mediaModel);
                if(success){
                   Util.showMessage(MainActivity.this,getString(R.string.tag) + " : "+currentTag + " " + getString(R.string.added));
                }
                popupDialog.dismiss();
                loadAllMedia();

            }

        });
        popupDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.accept), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }
}


