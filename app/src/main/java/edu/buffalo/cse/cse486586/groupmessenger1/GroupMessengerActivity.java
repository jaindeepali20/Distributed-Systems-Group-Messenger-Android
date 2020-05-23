package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final String[] ports = {REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};
    static final int SERVER_PORT = 10000;
    Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener fcontentvalues androidor "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */

        //button1 - ptest
        //button4 - send
        //textview1 - text screen
        //editText1 - type to compose editText



        //serversocket shifted to creatingserversocket
//        new CreatingServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try
        {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        }
        catch (IOException e)
        {
//            Log.e(TAG, e.getMessage());
//            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        findViewById(R.id.button1).setOnClickListener(new OnPTestClickListener(tv, getContentResolver()));


        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        Button button = (Button) findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                    Log.e("insert", "on click presed");
                        press_send(v);
                    }
                });
    }

    public void press_send(View View) {

//        Log.e("insert", "Press send clicked");

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        //PA1
        EditText et = (EditText) findViewById(R.id.editText1);
        TextView tv = (TextView) findViewById(R.id.textView1);
        String msg = et.getText().toString() + "\n";
        et.setText(""); // This is one way to reset the input box.
        tv.append(msg+"\n"); // This is one way to display a string.
        new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, myPort);
    }

//    private class CreatingServerTask extends AsyncTask<Void, Void, Void> {
//
//        protected Void doInBackground(Void... voids) {
//
//
//            publishProgress();
//            return null;
//        }
//
//        protected void onProgressUpdate(String...strings) {
//
//            try
//            {
//                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
//                new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
//            }
//            catch (IOException e)
//            {
//                Log.e(TAG, "Can't create a ServerSocket");
//                return;
//            }
//            return;
//        }
//    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        DataInputStream ds;
        String message;

        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            //*************** DOUBT -> sockets[0]? ******************
            ServerSocket serverSocket = sockets[0];
            Integer sno=0;


            //implement file storage wali cheez here
            while(true) {


                try {
                    //create new Socket
                    Socket soc = null;
//                    Log.d(TAG, "Socket created " + soc);

                    //accept thru serversocket
                    soc = serverSocket.accept();
//                    Log.d(TAG, "client socket accepted by server socket");

                    //new DataOutputStream
                    //ds = new DataInputStream(soc.getInputStream());
                    InputStreamReader sr = new InputStreamReader(soc.getInputStream());
                    BufferedReader br = new BufferedReader(sr);
//                    Log.d(TAG, "Data stream created ");

                    //writeUTF(message)
                    message = br.readLine();
//                    Log.d(TAG, "Got message from data stream ");
                    if (message != null) {

//                        Log.d("custom", "null message");
                        //Create stream to store message
                        PrintWriter op = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())));
//                        Log.d(TAG, "Got object of PrintWriter: ");

//
//                        //write message through stream
//                        op.write("Ack");
//                        Log.d(TAG, "Send acknowledgement: ");

//                        op.flush();
//                        Log.d(TAG, "Object flushed ");

                        //return null;
                        //send message to onProgressUpdate()
                        String[] str = new String[]{sno.toString(), message};
                        sno=sno+1;
                        publishProgress(str);
//                    Log.d(TAG, "passed message to publish progress ");
                    }


                    //close data obj
                    //DIS.close();

                    //close socket
                    //soc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

           // return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */

            //*************** DOUBT -> strings[0]? ******************
                String fname = strings[0].trim();
                String fdata = strings[1].trim();

                TextView tv = (TextView) findViewById(R.id.textView1);
                tv.append(fdata+"\n");

                Log.d(TAG, "Received message - key: \'"+fname+"\', value: \'"+fdata+"\'");
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("key",fname);
                mContentValues.put("value",fdata);
                getContentResolver().insert(mUri, mContentValues);


                        /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

            return;
        }
    }



    private class ClientTask extends AsyncTask<String, Void, Void> {



        @Override
        protected Void doInBackground(String... msgs) {
            try {
                Socket[] socket =new Socket[5];
                for(int i=0; i<5; i++)
                {
                    socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(ports[i]));


                    String msgToSend = msgs[0];

                    //Create stream to store message
                    PrintWriter op = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[i].getOutputStream())));
//                    Log.d(TAG, "Got object of PrintWriter: ");

                    //write message through stream
                    op.write(msgToSend);
//                    Log.d(TAG, "Wrote object msgToSend: ");

                    op.flush();
//                    Log.d(TAG, "Object flushed ");
//                    socket.close();
//                    socket.close();

//

                }

                for(int i=0; i<5; i++)
                {

                //accept acknowledgement from server socket
                InputStream m = socket[i].getInputStream();
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket[i].getInputStream()));
                    if(in.toString().equals("Ack"))
                    {
                        Log.d(TAG, "Acknowledgment Received ");
//                        socket.close();
                    }
//
                }

            }
            catch (UnknownHostException e)
            {
                Log.e(TAG, "ClientTask UnknownHostException");
            }
            catch (IOException e)
            {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
