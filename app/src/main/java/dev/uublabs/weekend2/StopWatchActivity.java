package dev.uublabs.weekend2;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StopWatchActivity extends AppCompatActivity implements TopFragment.OnFragmentInteractionListener
{

    private TopFragment topFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);
        topFragment = (TopFragment) getSupportFragmentManager().findFragmentById(R.id.fragTop);
    }

    @Override
    public void onFragmentInteraction(int run)
    {
        BottomFragment bt = (BottomFragment) topFragment.getChildFragmentManager().findFragmentByTag("BOTTOM");
        int seconds = bt.getSeconds();
        bt = BottomFragment.newInstance(run, seconds);

        topFragment.getChildFragmentManager().beginTransaction()
                .replace(R.id.flChildFrag, bt, "BOTTOM").commit();

    }
}
