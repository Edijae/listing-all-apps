package com.example.gikarasojo.helpinguser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager = null;
    ListView listView;
    ListViewAdapter  listViewAdapter;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        packageManager = getPackageManager();

        ctx = this;

        listView = (ListView) findViewById(R.id.listView);

        LoadApplications loadApplications = new LoadApplications();
        loadApplications.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ResolveInfo> checkForLauncherIntent(List<ApplicationInfo> list){

        ArrayList<ResolveInfo> mItems = new ArrayList();

        for(ApplicationInfo info : list) {
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    Intent intent = packageManager.getLaunchIntentForPackage(info.packageName);
                    ResolveInfo app = packageManager.resolveActivity(intent,0);
                    mItems.add(app);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("MainActivity ", " size is: " + mItems.size());
        return mItems;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params){
           ArrayList<ResolveInfo> mItem = checkForLauncherIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

          listViewAdapter = new ListViewAdapter(ctx, mItem);


            return null;
        }


        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            listView.setAdapter(listViewAdapter);

        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //progressDialog = ProgressDialog.show(getActivity(), null, "Loading file info...");

        }
    }

    public class ListViewAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ResolveInfo> mItem;
        private PackageManager packageManager;

        public ListViewAdapter(Context context, ArrayList<ResolveInfo> items) {
            this.context = context;
            this.mItem = items;
            packageManager = context.getPackageManager();
        }


        public int getCount(){
            return mItem.size();
        }

        @Override
        public ResolveInfo getItem(int position) {
            return mItem.get(position);
        }
        public long getItemId(int position){
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView  == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.testa, null);
            }

            ResolveInfo app =  mItem.get(position);

            TextView appName = (TextView) convertView.findViewById(R.id.tv_app_name);
            TextView appPackage = (TextView) convertView.findViewById(R.id.tv_app_package);
            ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);

            ActivityInfo activity = app.activityInfo;

            appPackage.setText(activity.applicationInfo.packageName);
            appName.setText(app.loadLabel(packageManager));
            icon.setImageDrawable(app.loadIcon(packageManager));



            return convertView;
        }
    }
}
