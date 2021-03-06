package com.example.mytags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SearchTagsActivity extends AppCompatActivity {
    private EditText mSearchField;

    // RecycleView
    private RecyclerView mResultList;
    private TagRecycleViewAdapter adapter;
    private LinearLayoutManager manager;


    private List<TagElement> lstTag;

    // button
    private View btnHome;
    private View btnTag;
    private FloatingActionButton btnPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);

        lstTag = recapTags(uniqueTags());
       // uniqueTags();

        mResultList = findViewById(R.id.list_tag);
        manager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(manager);
        adapter = new TagRecycleViewAdapter(SearchTagsActivity.this, lstTag);
        mResultList.setAdapter(adapter);

        // Remove shadow for menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        btnHome = findViewById(R.id.menu_home);
        btnTag = findViewById(R.id.menu_tag);
        btnPlus = findViewById(R.id.fab);



        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });



    }

    //Load Unique tags
    private List<String> uniqueTags(){
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(this);
        List<String> tags;
        tags = dataBaseArch.selectTags();
        if(tags.size() ==0){
            Util.showMessage(this,this.getString(R.string.no_File));
        }else{
            Util.showMessage(this,tags.size()+" "+this.getString(R.string.saved_Tags));
        }

        return  tags;
    }

    private List<TagElement> recapTags(List<String> tags){

        List<TagElement> tagElementList = new ArrayList<>();
        //1- create database helper object
        DataBaseArch dataBaseArch = new DataBaseArch(this);
        for(String tag : tags){
            //2- CreateTagElement
            tagElementList.add(new TagElement(tag,dataBaseArch.selectCount(tag,"image"),dataBaseArch.selectCount(tag,"video"),dataBaseArch.selectCount(tag,"audio"),dataBaseArch.selectCount(tag,"file")));


        }
        List<Media> medias = dataBaseArch.selectAll();

       /* for (TagElement tag : tagElementList ){
            Util.showMessage(this,tag.getTag()+" "+tag.getCountAudio()+" "+tag.getCountFile()+" "+tag.getCountPhoto()+" "+tag.getCountVideo());
        }*/

        for(Media m : medias){
            Log.d("AllS",m.getMediaType()+" "+m.getFileUri()+" "+m.getTag()+" "+m.getImageUri()+" "+m.getId());
        }
        return tagElementList;
    }
}