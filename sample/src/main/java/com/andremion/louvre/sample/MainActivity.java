/*
 * Copyright (c) 2017. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andremion.louvre.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewTreeObserver;

import com.andremion.louvre.Louvre;
import com.andremion.louvre.home.GalleryActivity;
import com.andremion.louvre.util.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOUVRE_REQUEST_CODE = 0;

    private MainAdapter mAdapter;
    private List<Uri> mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.gallery_item_offset);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter = new MainAdapter());
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int size = getResources().getDimensionPixelSize(R.dimen.gallery_item_size);
                int width = recyclerView.getMeasuredWidth();
                int columnCount = width / (size + spacing);
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, columnCount));
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NumberPickerDialog.show(getSupportFragmentManager(), LOUVRE_REQUEST_CODE, mSelection);
                SparseBooleanArray mSelectedTypes = new SparseBooleanArray();
                mSelectedTypes.append(0,true);
                mSelectedTypes.append(1,true);
                mSelectedTypes.append(2,true);

                Louvre.init(MainActivity.this)
                        .setRequestCode(LOUVRE_REQUEST_CODE)
                        .setMaxSelection(100)
                        .setSelection(mSelection)
                        .setMediaTypeFilter(parseToArray(mSelectedTypes))
                        .open();
            }
        });
    }

    @NonNull
    private String[] parseToArray(@NonNull SparseBooleanArray selectedTypes) {
        List<String> selectedTypeList = new ArrayList<>();
        for (int i = 0; i < selectedTypes.size(); i++) {
            int key = selectedTypes.keyAt(i);
            if (selectedTypes.get(key, false)) {
                selectedTypeList.add(Louvre.IMAGE_TYPES[key]);
            }
        }
        String[] array = new String[selectedTypeList.size()];
        selectedTypeList.toArray(array);
        return array;
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //noinspection unchecked
        mSelection = (List<Uri>) getLastCustomNonConfigurationInstance();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mSelection;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOUVRE_REQUEST_CODE && resultCode == RESULT_OK) {
            mAdapter.swapData(mSelection = GalleryActivity.getSelection(data));
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
