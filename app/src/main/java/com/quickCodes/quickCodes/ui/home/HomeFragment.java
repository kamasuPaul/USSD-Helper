package com.quickCodes.quickCodes.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.adapters.AdapterSimCards;
import com.quickCodes.quickCodes.adapters.RecentAdapter;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.AppLifeCycleListener;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private AdapterSimCards adapterSimcards;
    private AdapterDialer adapterUssdCodesRecent;
    private List<SimCard> simCards;
    private List<UssdActionWithSteps> actions;
    private UssdActionsViewModel ussdActionsViewModel;
    private boolean pointsCalculated = false;
    private RecentAdapter recentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simCards = new ArrayList<>();
        actions = new ArrayList<>();
        adapterSimcards = new AdapterSimCards(getActivity());
        adapterUssdCodesRecent = new AdapterDialer(getActivity());
        recentAdapter = new RecentAdapter(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayout linear_layout_recent = root.findViewById(R.id.linear_layout_recent);
        LinearLayout linear_layout_no_recent_items = root.findViewById(R.id.linear_layout_no_recent_items);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        ussdActionsViewModel =
                new ViewModelProvider(this).get(UssdActionsViewModel.class);
        homeViewModel.getSimCards(getActivity()).observe(getViewLifecycleOwner(), new Observer<List<SimCard>>() {
            @Override
            public void onChanged(List<SimCard> cards) {
                adapterSimcards.setSimcards(cards);
                simCards = cards;
            }
        });
        ussdActionsViewModel.getAllCustomActions().observe(getViewLifecycleOwner(), new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> ussdActionWithSteps) {
                adapterUssdCodesRecent.setUssdActions(ussdActionWithSteps);
                actions = ussdActionWithSteps;

                if (ussdActionWithSteps.size() < 1) {
                    linear_layout_recent.setVisibility(View.GONE);
                    linear_layout_no_recent_items.setVisibility(View.VISIBLE);
                } else {

                    linear_layout_recent.setVisibility(View.VISIBLE);
                    linear_layout_no_recent_items.setVisibility(View.GONE);
                }
                if (!pointsCalculated) {
                    pointsCalculated = true;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int points = 0;
                            for (UssdActionWithSteps a : ussdActionWithSteps
                            ) {
                                points = points + a.action.getWeight();
                            }
                            Tools.setPoints(getActivity(), points);
                        }
                    });
                    thread.start();
                }
            }
        });


        //......SETUP SIMCARDS VIEW PAGER...........................................................
        ViewPager2 viewPagerSimcards = root.findViewById(R.id.viewpager);
        viewPagerSimcards.setAdapter(adapterSimcards);
        viewPagerSimcards.setOffscreenPageLimit(1);
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.card_margin);
        int peekMarginPx = getResources().getDimensionPixelOffset(R.dimen.peek_offset_margin);

        RecyclerView rv = (RecyclerView) viewPagerSimcards.getChildAt(0);
        rv.setClipToPadding(false);
        int padding = peekMarginPx + pageMarginPx;
        rv.setPadding(padding, 0, padding, 0);

        viewPagerSimcards.setPageTransformer(new SideBySideTransformer());

        viewPagerSimcards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                SimCard simCard = simCards.get(position);
//                Toast.makeText(getActivity(), "You have changed to "+simCard.getNetworkName()+" codes", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SimCard simCard = simCards.get(position);
                List<UssdActionWithSteps> filterd = new ArrayList<UssdActionWithSteps>();
                for (UssdActionWithSteps us : actions) {
                    if (us == null || us.action == null) continue;
                    if (us.action.getHni().equals(simCard.getHni())) {
                        filterd.add(us);
                    }
                }
                adapterUssdCodesRecent.setUssdActions(filterd);
                Tools.setSelectedSimcard(getActivity(), simCard.getSlotIndex());
            }

        });
        viewPagerSimcards.beginFakeDrag();
        //..........................................................................................
        ViewPager2 tabsPager = root.findViewById(R.id.pager_tabs);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabsPager.setAdapter(recentAdapter);

        new TabLayoutMediator(tabLayout, tabsPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Recent");
                    if (position == 1) tab.setText("Starred");

                }
        ).attach();


        //.... SETUP RECENT ACTIONS ................................................................
        RecyclerView recentReyclerView = root.findViewById(R.id.recentReyclerView);
        recentReyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recentReyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        recentReyclerView.setAdapter(adapterUssdCodesRecent);
        adapterUssdCodesRecent.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, ussdActionsViewModel);
            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
            }

            @Override
            public void onStarClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
                Toast.makeText(getActivity(), ussdActionWithSteps.action.getName() + "has been " + (ussdActionWithSteps.action.isStarred() ? "un starred" : "starred"), Toast.LENGTH_SHORT).show();
                Tools.updateSetStar(ussdActionWithSteps, ussdActionsViewModel);
            }
        });
        //add this fragment as alifecycle owner so that its lifecycle is observed for lifecycle changes
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifeCycleListener(getActivity()));
        return root;
    }

    public static class SideBySideTransformer implements ViewPager2.PageTransformer {
        private float minScale;

        public SideBySideTransformer() {
            this(0.85f);
        }

        public SideBySideTransformer(float minScale) {
            this.minScale = minScale;
        }

        public float getMinScale() {
            return minScale;
        }

        public void setMinScale(float minScale) {
            this.minScale = minScale;
        }

        @Override
        public void transformPage(@NonNull View page, float position) {
            float pageWidth = page.getWidth();
            float pageHeight = page.getHeight();

            page.setPivotX(pageWidth / 2f);
            page.setPivotY(pageHeight / 2f);

            if (position < -1) {
                page.setPivotX(pageWidth);
                page.setScaleX(minScale);
                page.setScaleY(minScale);
            } else if (position <= 1) {
                float scaleFactor = Math.max(minScale, (1 - Math.abs(position)));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth / 2f * (1 - position));
            } else {
                page.setPivotX(0);
                page.setScaleX(minScale);
                page.setScaleY(minScale);
            }
        }
    }

    class ZoomOutPageTransformer implements ViewPager2.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // [ -Infinity,-1 )
                // This page is way off-screen to the left.
                page.setAlpha(0);
            } else if (position <= 1) { // [ -1,1 ]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down ( between MIN_SCALE and 1 )
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // ( 1,+Infinity ]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }
    }
}