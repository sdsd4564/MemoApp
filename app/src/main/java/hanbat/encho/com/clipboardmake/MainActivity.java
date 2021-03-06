package hanbat.encho.com.clipboardmake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;

import hanbat.encho.com.clipboardmake.Adapter.MyAdapter;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "메인 액티비티";
    private DbOpener mOpener = null;
    private RecyclerView mRecyclerView = null;
    private MyAdapter adapter = null;
    private ArrayList<Entity> list = null;
    private ArrayList<Entity> filtered = null;
    private Intent intent = null;
    private AdView mAdView = null;
    private ToggleButton mToggle = null;

    private Notification.Builder mBuilder = null;
    private NotificationManager notificationManager = null;
    public static final int NOTIFICATION_ID = 151;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ----- 서비스 선언 -----*/
        intent = new Intent(this, MemoService.class);

        /* ----- DB 오픈 ----- */
        mOpener = DbOpener.getInstance(this);

        /* ----- 광고 배너 ----- */
        MobileAds.initialize(Application.getMyContext(), "ca-app-pub-2392186899206299~1173995164");
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);
        //////////////////////////

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_clipdata); // 리사이클러뷰
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.search);

        list = new ArrayList<>();
        filtered = new ArrayList<>();
        doWhileCursorToArray(); // DB 데이터를 어레이리스트로 옮김


        adapter = new MyAdapter(MainActivity.this, list, filtered);
        adapter.setMode(MyAdapter.MODE_SINGLE);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);


        PendingIntent mPendingIntent = PendingIntent.getActivity(Application.getMyContext(),
                0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_head))
                .setContentText(getString(R.string.notification_body))
                .setAutoCancel(false)
                .setOngoing(true);

        mBuilder.setContentIntent(mPendingIntent);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mToggle = (ToggleButton) findViewById(R.id.toggle);

        /* ----- 알림 켜져있으면 토글 상태 ON ------ */
        if (PropertyManager.getInstance().getNotificationSetting()) {
            mToggle.setToggleOn(true);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        /* ----- 토글 ON & OFF ----- */
        mToggle.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = PropertyManager.getInstance().getNotificationSetting();

            @Override
            public void onClick(View view) {
                if (!isChecked) {
                    mToggle.toggleOn();
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    Toast.makeText(MainActivity.this, "상태바 알람을 켰습니다", Toast.LENGTH_SHORT).show();
                } else {
                    mToggle.toggleOff();
                    notificationManager.cancel(NOTIFICATION_ID);
                    Toast.makeText(MainActivity.this, "상태바 알람을 껐습니다", Toast.LENGTH_SHORT).show();
                }
                isChecked = !isChecked;
                PropertyManager.getInstance().setNotificationSetting(isChecked);
            }
        });
        ////////////////////////////

        MemoContent.setCallback(new MemoContent.DeleteCallback() {
            @Override
            public void onFragmentDestroy() {
                onResume();
            }
        });
        MyAdapter.setCallback(new MyAdapter.BookmarkedCallback() {
            @Override
            public void onDialogDestroied() {
                onResume();
                Toast.makeText(MainActivity.this, getString(R.string.message_when_bookmarked), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }

        stopService(intent); // TODO: 2016-09-28  ISSUE

        /* ----- 리사이클러뷰 업데이트 ----- */
        doWhileCursorToArray();
        adapter = new MyAdapter(MainActivity.this, list, filtered);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.hint_search) + "</font>"));
        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        boolean isItemChecked = false;

        switch (id) {
            case R.id.delete_all: {
                if (adapter.getMode() == MyAdapter.MODE_SINGLE) {
                    item.setIcon(R.drawable.ic_check_white_36dp);
                    adapter.setMode(MyAdapter.MODE_MULTI);
                } else if (adapter.getMode() == MyAdapter.MODE_MULTI) {
                    item.setIcon(R.drawable.ic_delete_white_36dp);
                    mOpener.open();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (filtered.get(i).checked) {
                            mOpener.deleteColumn(filtered.get(i)._id);
                            isItemChecked = true;
                        }
                    }
                    mOpener.close();
                    onResume();
                    if (isItemChecked)
                        Toast.makeText(this, R.string.message_when_delete, Toast.LENGTH_SHORT).show();
                    adapter.setMode(MyAdapter.MODE_SINGLE);
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(Application.getMyContext(), getString(R.string.message_when_pause), Toast.LENGTH_SHORT).show();
        startService(intent); // 클립보드 메모 서비스
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    /* ----- 데이터베이스 내용을 어레이로 옮김 ----- */
    private void doWhileCursorToArray() {
        Cursor mCursor;
        list = new ArrayList<>();
        filtered = new ArrayList<>();
        mOpener.open();

        mCursor = mOpener.getAllColumn();
        while (mCursor.moveToNext()) {
            Entity mEntity = new Entity(
                    mCursor.getInt(mCursor.getColumnIndex(MemoDB._ID)),
                    mCursor.getString(mCursor.getColumnIndex(MemoDB.MEMO)),
                    false,
                    mCursor.getInt(mCursor.getColumnIndex(MemoDB.MARKED)));

            list.add(mEntity);
        }


        filtered.addAll(list);
        mCursor.close();
        mOpener.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<Entity> filteredModelList = filter(list, newText);
        adapter.setFilter(filteredModelList, newText);
        return true;
    }

    private ArrayList<Entity> filter(ArrayList<Entity> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Entity> filteredModelList = new ArrayList<>();
        for (Entity model : models) {
            final String text = model.memo.toLowerCase();
            if (SoundSearcher.matchString(text, query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
