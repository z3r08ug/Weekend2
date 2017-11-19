package dev.uublabs.weekend2;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class BottomFragment extends Fragment
{
    public int getSeconds()
    {
        return seconds;
    }

    private static int seconds;
    private static int running;
    private TextView tvTime;
    private static Handler handler;
    private String time;
    private Timer t;
    private int mParam1;


    public BottomFragment()
    {
        //required empty constructor
    }


    public static BottomFragment newInstance(int run, int sec)
    {
        BottomFragment fragment = new BottomFragment();
        Bundle args = new Bundle();
        args.putInt("param1", run);
        fragment.setArguments(args);
        running = run;
        //if reset was pressed then reset seconds to zero
        if (running == 3)
            seconds = 0;
        else
            seconds = sec;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getInt("param1");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d("BOTTOM", "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
        outState.putInt("sec", seconds);
        outState.putString("time", time);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom, container, false);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //bind time display
        tvTime = view.findViewById(R.id.tvTime);

        //if there is a saved instance state set the time and seconds to those values
        if (savedInstanceState != null)
        {
            seconds = savedInstanceState.getInt("sec");
            time = savedInstanceState.getString("time");
        }

        //display the current time
        setTime();
        tvTime.setText(time);

        //create a handler to post the time update back to UI every second
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                tvTime.setText(msg.getData().getString("time")); //this is the textview
            }
        };

        //make a new timer
        t = new Timer();
        //if start was pressed run the timer
        if (running == 1)
        {
            runTimer();
        }
        //if stop was pressed stop the timer
        else if (running == 2)
        {
            t.cancel();
            tvTime.setText(time);
        }

    }

    private void setTime()
    {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        time = String.format("%d:%02d:%02d", hours, minutes, secs);
    }

    public void runTimer()
    {
        t.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (running == 1)
                {
                    seconds++;

                }
                setTime();

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("time", time);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        t.cancel();
    }

    public String getTime() {
        return time;
    }
}
