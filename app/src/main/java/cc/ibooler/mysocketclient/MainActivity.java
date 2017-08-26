package cc.ibooler.mysocketclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView ipTv, contentTv;
    private Button linkBtn, sendBtn;
    private EditText sendEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    // 初始化控件
    private void initView() {
        ipTv = (TextView) findViewById(R.id.tv_ip);
        contentTv = (TextView) findViewById(R.id.tv_content);
        linkBtn = (Button) findViewById(R.id.btn_link);
        linkBtn.setOnClickListener(this);
        sendBtn = (Button) findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(this);
        sendEd = (EditText) findViewById(R.id.ed_send);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_link:// 链接
                connect();
                break;
            case R.id.btn_send:// 发送
                send();
                break;
        }
    }

    //------------------------------
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    // 连接Socket服务端
    private void connect() {
        final String ip = ipTv.getText().toString().trim();
        final int port = 12345;
        // 异步执行
        AsyncTask<Void, String, Void> asyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // 实例化Socket
                    socket = new Socket(ip, port);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    publishProgress("success");

                    // 读取传递过来的数据
                    String line;
                    while ((line = reader.readLine()) != null) {
                        publishProgress(line);
                    }
                } catch (IOException e) {
                    publishProgress("failed");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if ("success".equals(values[0]))
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                else if ("failed".equals(values[0]))
                    Toast.makeText(MainActivity.this, "无法建立连接", Toast.LENGTH_LONG).show();
                else
                    contentTv.append("他说：" + values[0] + "\n");
                super.onProgressUpdate(values);
            }
        };
        asyncTask.execute();
    }

    // 向Socket服务端发送
    private void send() {
        String out = sendEd.getText().toString().trim();
        // 发送数据
        try {
            writer.write(out + "\n");
            writer.flush();
            sendEd.setText("");
            contentTv.append("我说：" + out + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
