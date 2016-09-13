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
import android.support.v4.app.AppLaunchChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "메인 액티비티";
    private DbOpenner mOpenner;
    private Cursor mCursor;
    private Entity mEntity;
    private ClipboardManager manager = null;
    private ClipData clipData = null;
    private ClipData.Item item = null;
    private ImageView deleteBtn = null;

    private TextView display = null;
    private RecyclerView mRecyclerView = null;

    private MyAdapter adapter = null;
    private ArrayList<Entity> list = null;

    private Intent intent = null;
    private boolean isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent("hanbat.encho.com.clipboardmake.service");
        intent.setPackage("hanbat.encho.com.clipboardmake");

        if (isServiceRunning) {
            stopService(intent); // 실행중인경우 STOP !
            isServiceRunning = false;
        }


        display = (TextView) findViewById(R.id.display_text); // 표시해줄 텍스트뷰
        deleteBtn = (ImageView) findViewById(R.id.delete); // 딜리트 버튼 가장 최신꺼
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_clipdata); // 리사이클러뷰

        mOpenner = DbOpenner.getInstance(this);
        mOpenner.open();

        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipData = manager.getPrimaryClip();

        list = new ArrayList<>();
        doWhileCursorToArray(); // DB 데이터를 어레이리스트로 옮김

        adapter = new MyAdapter(MainActivity.this, list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        if (clipData != null) { /* 가장 최신의 클립보드 내용을 맨 윗줄에 표시해줌 */
            item = clipData.getItemAt(0);
            display.setText(new StringBuilder().append("최근 클립보드 내용 : ").append(item.getText()).toString());
        } else {
            Toast.makeText(getApplicationContext(), "클립보드 내용 없음", Toast.LENGTH_SHORT).show();
            display.setText("최근 클립보드 내용 : 없음");
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mOpenner.deleteColumn()
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            display.setText("클립보드 내용 없음");
        }

        /* ----- 클립보드 내용 변경시 ----- */
        manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
//                mOpenner.insertColumn(clipData.getItemAt(0).getText().toString());
                list = new ArrayList<Entity>();
                doWhileCursorToArray();
                display.setText(clipData.getItemAt(0).getText() + " - " + list.size());
                adapter = new MyAdapter(MainActivity.this, list);
            }
        });
        /* -------------------------------------------------------- */

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(Application.getMyContext(), "메모는 계속 기록됩니다. 정말로요!", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PendingIntent mPendingIntent = PendingIntent.getActivity(Application.getMyContext(),
                    0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("클립보드 내용을 기록중입니다")
                    .setContentText("글자를 복사해보세요!")
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
        mCursor.close();
    }
}
