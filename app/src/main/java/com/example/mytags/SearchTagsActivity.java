package com.example.mytags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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

        lstTag = new ArrayList<>();

        lstTag.add( new TagElement("balbla", 5,2,3,5));
        lstTag.add( new TagElement("test", 52,12,35,51));
        lstTag.add( new TagElement("vacance", 5,2,3,51));
        lstTag.add( new TagElement("passport", 5,2,3,15));
        lstTag.add( new TagElement("document", 5,2,3,55));
        lstTag.add( new TagElement("meme", 1,52,3,45));
        lstTag.add( new TagElement("test", 5,2,3,75));
        lstTag.add( new TagElement("chaton", 5,2,3,5));
        lstTag.add( new TagElement("balbla", 5,2,3,5));
        lstTag.add( new TagElement("test", 52,12,35,51));
        lstTag.add( new TagElement("vacance", 5,2,3,51));
        lstTag.add( new TagElement("passport", 5,2,3,15));
        lstTag.add( new TagElement("document", 5,2,3,55));
        lstTag.add( new TagElement("meme", 1,52,3,45));
        lstTag.add( new TagElement("test", 5,2,3,75));
        lstTag.add( new TagElement("chaton", 5,2,3,5));
        lstTag.add( new TagElement("balbla", 5,2,3,5));
        lstTag.add( new TagElement("test", 52,12,35,51));
        lstTag.add( new TagElement("vacance", 5,2,3,51));
        lstTag.add( new TagElement("passport", 5,2,3,15));
        lstTag.add( new TagElement("document", 5,2,3,55));
        lstTag.add( new TagElement("meme", 1,52,3,45));
        lstTag.add( new TagElement("test", 5,2,3,75));
        lstTag.add( new TagElement("chaton", 5,2,3,5));
        lstTag.add( new TagElement("balbla", 5,2,3,5));
        lstTag.add( new TagElement("test", 52,12,35,51));
        lstTag.add( new TagElement("vacance", 5,2,3,51));
        lstTag.add( new TagElement("passport", 5,2,3,15));
        lstTag.add( new TagElement("document", 5,2,3,55));
        lstTag.add( new TagElement("meme", 1,52,3,45));
        lstTag.add( new TagElement("test", 5,2,3,75));
        lstTag.add( new TagElement("chaton", 5,2,3,5));

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
}