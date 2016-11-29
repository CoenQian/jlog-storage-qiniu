/*
 * Copyright 2016 JiongBull
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

package com.jiongbull.jlog.storage.qiniu.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * HOME PAGE.
 */
public class MainActivity extends AppCompatActivity {

    private static final String URL_BLOG = "http://jiongbull.com";
    private static final String URL_GITHUB = "https://github.com/JiongBull";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_blog:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_BLOG));
                startActivity(intent);
                break;
            case R.id.action_github:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_GITHUB));
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void log(View view) {
        RootApp.getLogger().v("VERBOSE");
        RootApp.getLogger().d("DEBUG");
        RootApp.getLogger().i("INFO");
        RootApp.getLogger().w("WARN");
        RootApp.getLogger().e("ERROR");
        RootApp.getLogger().wtf("WTF");
    }
}