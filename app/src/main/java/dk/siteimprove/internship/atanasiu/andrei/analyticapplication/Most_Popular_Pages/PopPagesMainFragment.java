package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class PopPagesMainFragment extends Fragment
{
    private FragmentTabHost mTabHost;
    private OnFragmentInteractionListener mListener;
    View rootView;
    boolean landscapeMode;

    public PopPagesMainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            landscapeMode = true;
        }
        else
        {
            landscapeMode = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle("Most Popular Pages");
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_main);

        Bundle arg1 = new Bundle();
        arg1.putInt("Arg for Frag1", 1);
        mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Today"), PopPagesFragment.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putInt("Arg for Frag2", 2);
        mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Week"), PopPagesWeekFragment.class, arg2);

        Bundle arg3 = new Bundle();
        arg3.putInt("Arg for Frag3", 3);
        mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Month"), PopPagesMonthFragment.class, arg3);

        Bundle arg4 = new Bundle();
        arg4.putInt("Arg for Frag4", 4);
        mTabHost.addTab(mTabHost.newTabSpec("Tab4").setIndicator("Year"), PopPagesYearFragment.class, arg4);

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.rgb(49, 79, 79));
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_indicator_holo);

            final TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title);

            // Look for the title view to ensure this is an indicator and not a divider.(I didn't know, it would return divider too, so I was getting an NPE)
            if (tv == null)
                continue;
            else
            {
                tv.setTextColor(getResources().getColor(R.color.JavaBlue));
            }
            Resources r = getResources();
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, r.getDisplayMetrics());

            mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int)height;

        }
        mTabHost.getTabWidget().setDividerDrawable(null);
        if(landscapeMode)
        {
            mTabHost.getTabWidget().setVisibility(View.GONE);
        }
        return mTabHost;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
