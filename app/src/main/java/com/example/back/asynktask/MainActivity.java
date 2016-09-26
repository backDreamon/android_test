package com.example.back.asynktask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private TextView txtResponse;
    private EditText edtTextAddress, edtTextPort, contentText;
    private Button btnConnect, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTextAddress = (EditText) findViewById(R.id.address);
        edtTextPort = (EditText) findViewById(R.id.port);
        btnConnect = (Button) findViewById(R.id.connect);
        btnClear = (Button) findViewById(R.id.clear);
        txtResponse = (TextView) findViewById(R.id.response);
        contentText = (EditText) findViewById(R.id.content);

        btnConnect.setOnClickListener(buttonConnectOnClickListener);
        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtResponse.setText("전송");
            }
        });
    }

    // 클릭이벤트 리스너
    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
        public void onClick(View arg0) {
            NetworkTask myClientTask = new NetworkTask(
                    edtTextAddress.getText().toString(),
                    Integer.parseInt(edtTextPort.getText().toString()),
                    contentText.getText().toString()
            );
            Log.d("IP", edtTextAddress.getText().toString());
            Log.d("PORT", edtTextPort.getText().toString());
            contentText.setText("");
            myClientTask.execute();
        }
    };

    public class NetworkTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response;
        String dstMsg;

        NetworkTask(String addr, int port, String msg) {
            dstAddress = addr;
            dstPort = port;
            dstMsg = msg;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Socket socket = new Socket(dstAddress, dstPort);
                InputStream inputStream = socket.getInputStream();

                Log.d("host port", socket.getLocalPort()+"");

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(dstMsg);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                        1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                Log.d("status", "성공");
                socket.close();
                response = byteArrayOutputStream.toString("UTF-8");

            } catch (UnknownHostException e) {
                Log.d("status", "실패");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("status", "실패");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            txtResponse.setText(response);
            Log.d("Receive", response);
            super.onPostExecute(result);
        }

    }

}
