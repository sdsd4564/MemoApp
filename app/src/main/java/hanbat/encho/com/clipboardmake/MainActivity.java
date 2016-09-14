package hanbat.encho.com.clipboardmake;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.AppLaunchChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "메인 액티비티";
    private DbOpenner mOpenner;
    private Cursor mCursor;
    private Entity mEntity;
    private ClipboardManager manager = null;
    private ClipData clipData = null;
    private ClipData.Item item = null;

    private TextView display = null;
    private RecyclerView mRecyclerView = null;

    private MyAdapter adapter = null;
    private ArrayList<Entity> list = null;
    private ArrayList<Entity> filtered = null;

    private Intent intent = null;
    private boolean isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ----- 서비스 선언 -----*/
        intent = new Intent("hanbat.encho.com.clipboardmake.service");
        intent.setPackage("hanbat.encho.com.clipboardmake");

        /* ----- DB 오픈 ----- */
        mOpenner = DbOpenner.getInstance(this);
        mOpenner.open();

        if (isServiceRunning) {
            stopService(intent); // 실행중인경우 STOP !
            isServiceRunning = false;
        }

        display = (TextView) findViewById(R.id.display_text); // 표시해줄 텍스트뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_clipdata); // 리사이클러뷰
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar); // TODO: CHECK THIS LINE IF ERROR OR WEIRD
        mToolbar.inflateMenu(R.menu.search);

        SearchView mSearchView = (SearchView) mToolbar.getMenu().findItem(R.id.menu_search).getActionView();
        mSearchView.setOnQueryTextListener(this);

        /* ----- 시스템에서 클립보드 내용 가져옴 ----- */
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipData = manager.getPrimaryClip();

        list = new ArrayList<>();
        filtered = new ArrayList<>();
        doWhileCursorToArray(); // DB 데이터를 어레이리스트로 옮김


        adapter = new MyAdapter(MainActivity.this, list, filtered);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        if (clipData != null) { /* 가장 최신의 클립보드 내용을 맨 윗줄에 표시해줌 */
            item = clipData.getItemAt(0);
            display.setText(new StringBuilder().append(getString(R.string.recent_clipboard)).append(item.getText()).toString());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.has_no_clipboard), Toast.LENGTH_SHORT).show();
            display.setText(new StringBuilder().append(getString(R.string.recent_clipboard)).append("없음").toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            display.setText(getString(R.string.has_no_clipboard));
        }

        /* ----- 클립보드 내용 변경시 ----- */
        manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                clipData = manager.getPrimaryClip();
                list = new ArrayList<Entity>();
                doWhileCursorToArray();
                display.setText(clipData.getItemAt(0).getText() + " - " + list.size());
                adapter = new MyAdapter(MainActivity.this, list, filtered);
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(Application.getMyContext(), getString(R.string.message_when_pause), Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PendingIntent mPendingIntent = PendingIntent.getActivity(Application.getMyContext(),
                    0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.notification_head))
                    .setContentText(getString(R.string.notification_body))
                    .setAutoCancel(false);

            mBuilder.setContentIntent(mPendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, mBuilder.build());

        }
        if (!isServiceRunning) {
            startService(intent); // 클립보드 메모 서비스
            isServiceRunning = true;
        }
    }

    /* ----- 데이터베이스 내용을 어레이로 옮김 ----- */
    private void doWhileCursorToArray() {
        mCursor = null;
        mCursor = mOpenner.getAllColumn();
        while (mCursor.moveToNext()) {
            mEntity = new Entity(mCursor.getInt(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex("memo")));

            list.add(mEntity);
        }
        filtered.addAll(list);
        mCursor.close();
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
            if (SoundSearcher.matchString(text, query)){
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
