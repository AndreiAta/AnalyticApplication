package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsWeekFragment;


public class SocialMediaMainFragment extends Fragment
{
    private FragmentTabHost mTabHost;
    private OnFragmentInteractionListener mListener;

    public SocialMediaMainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getActivity().setTitle("Social Media");
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_social_media_main);

        Bundle arg1 = new Bundle();
        arg1.putInt("Arg for Frag1", 1);
        mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Today"), SocialMediaFragment.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putInt("Arg for Frag2", 2);
        mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Week"), SocialMediaWeekFragment.class, arg2);

        Bundle arg3 = new Bundle();
        arg3.putInt("Arg for Frag3", 3);
        mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Month"), SocialMediaMonthFragment.class, arg3);

        Bundle arg4 = new Bundle();
        arg4.putInt("Arg for Frag4", 4);
        mTabHost.addTab(mTabHost.newTabSpec("Tab4").setIndicator("Year"), SocialMediaYearFragment.class, arg4);
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
