package com.cs442project.appmonitor;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cs442project.appmonitor.comparator.AppNameComparator;
import com.cs442project.appmonitor.comparator.LastTimeUsedComparator;
import com.cs442project.appmonitor.comparator.UsageTimeComparator;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;

import org.eazegraph.lib.charts.BarChart;

import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Snehal on 2/27/2015.
 */
public class Sorting extends Fragment {
    private static final String TAG = "UsageStatsActivity";
    private static final boolean localLOGV = false;
    private UsageStatsManager mUsageStatsManager;
    private LayoutInflater mInflater;
    private UsageStatsAdapter mAdapter;
    private PackageManager mPm;
    PieChart mPieChart;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View all = inflater.inflate(R.layout.sorting, container, false);
        //((TextView) last_month.findViewById(R.id.textView)).setText("Same UI as 'TODAY' tab");
        mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPm = getActivity().getPackageManager();

        ListView listView = (ListView) all.findViewById(R.id.LastMonthList);
        mAdapter = new UsageStatsAdapter();
        listView.setAdapter(mAdapter);

//        BarChart mBarChart = (BarChart) last_month.findViewById(R.id.barchart);
//
//        mBarChart.addBar(new BarModel("1", 2.3f, 0xFF123456));
//        mBarChart.addBar(new BarModel("2",2.f,  0xFF343456));
//        mBarChart.addBar(new BarModel("3",3.3f, 0xFF563456));
//        mBarChart.addBar(new BarModel("4",1.1f, 0xFF873F56));
//        mBarChart.addBar(new BarModel("5",2.7f, 0xFF56B7F1));
//        mBarChart.addBar(new BarModel("6",2.f,  0xFF343456));
//        mBarChart.addBar(new BarModel("7",0.4f, 0xFF1FF4AC));
//        mBarChart.addBar(new BarModel("8",4.f,  0xFF1BA4E6));
//
//        mBarChart.startAnimation();
        mPieChart = (PieChart) all.findViewById(R.id.piechart);
        mPieChart.startAnimation();
        for (int i = 0; i < mAdapter.mPackageStats.size(); i++) {
            Random rnd = new Random();
            //int color = Color.argb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            System.out.println("***pack name**" + mAdapter.mPackageStats.get(i).getPackageName());
            System.out.println("***label name**" + mAdapter.mAppLabelMap.get(mAdapter.mPackageStats.get(i).getPackageName()));


            int duration = (int) mAdapter.mPackageStats.get(i).getTotalTimeInForeground() / 1000;

//            String hms = String.format("%02d:%02d:%02d",
//                    TimeUnit.MILLISECONDS.toHours(duration),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) -
//                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
//                    TimeUnit.MILLISECONDS.toSeconds(duration) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//            float hmsINT = Float.parseFloat(hms);
//            System.out.println("$"+hmsINT);
            mPieChart.addPieSlice(new PieModel(mAdapter.mAppLabelMap.get(mAdapter.mPackageStats.get(i).getPackageName()), duration, Color.rgb(255, rnd.nextInt(), rnd.nextInt())));

            this.mPieChart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    System.out.println("FOCUS CHANGED");
                }
            });

            System.out.println("***color = " + Color.rgb(255, rnd.nextInt(), rnd.nextInt()));
            System.out.println("***duration***" + duration);
        }

        return all;

    }

    static class AppViewHolder {
        TextView pkgName;
        TextView usageTime;
        ImageView imgViAppIcon;
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    class UsageStatsAdapter extends BaseAdapter {
        // Constants defining order for display order
        private static final int _DISPLAY_ORDER_USAGE_TIME = 0;
        private static final int _DISPLAY_ORDER_LAST_TIME_USED = 1;
        private static final int _DISPLAY_ORDER_APP_NAME = 2;

        private int mDisplayOrder = _DISPLAY_ORDER_USAGE_TIME;
        private LastTimeUsedComparator mLastTimeUsedComparator = new LastTimeUsedComparator();
        private UsageTimeComparator mUsageTimeComparator = new UsageTimeComparator();
        private AppNameComparator mAppLabelComparator;
        private final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
        private final ArrayList<UsageStats> mPackageStats = new ArrayList<>();

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        UsageStatsAdapter() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -5);

            final List<UsageStats> stats =
                    mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY,
                            cal.getTimeInMillis(), System.currentTimeMillis());

            for (UsageStats usg : stats) {
                System.out.println("---------------------");
                System.out.println("" + usg.getPackageName());
                System.out.println("First time: " + usg.getFirstTimeStamp());
                System.out.println("Last time :" + usg.getLastTimeStamp());
                System.out.println("Total Time: " + usg.getTotalTimeInForeground());
                System.out.println(usg.getLastTimeUsed());
                System.out.println("---------------------");

            }
            if (stats == null) {
                return;
            }

            ArrayMap<String, UsageStats> map = new ArrayMap<>();
            final int statCount = stats.size();
            for (int i = 0; i < statCount; i++) {
                final UsageStats pkgStats = stats.get(i);

                // load application labels for each application
                try {
                    ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);
                    String label = appInfo.loadLabel(mPm).toString();
                    mAppLabelMap.put(pkgStats.getPackageName(), label);

                    UsageStats existingStats =
                            map.get(pkgStats.getPackageName());
                    if (existingStats == null) {
                        map.put(pkgStats.getPackageName(), pkgStats);
                    } else {
                        existingStats.add(pkgStats);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    // This package may be gone.
                }
            }
            mPackageStats.addAll(map.values());

            // Sort list
            mAppLabelComparator = new AppNameComparator(mAppLabelMap);
            sortList();
        }

        @Override
        public int getCount() {
            return mPackageStats.size();
        }

        @Override
        public Object getItem(int position) {
            return mPackageStats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            AppViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_duration, null);

                holder = new AppViewHolder();
                holder.pkgName = (TextView) convertView.findViewById(R.id.text);
                //holder.lastTimeUsed = (TextView) convertView.findViewById(R.id.last_time_used);
                holder.usageTime = (TextView) convertView.findViewById(R.id.duration);
                holder.imgViAppIcon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {

                holder = (AppViewHolder) convertView.getTag();
            }

            UsageStats pkgStats = mPackageStats.get(position);
            if (pkgStats != null) {
                String label = mAppLabelMap.get(pkgStats.getPackageName());
                holder.pkgName.setText(label);
                try {
                    Drawable icon = mPm.getApplicationIcon(pkgStats.getPackageName());
                    holder.imgViAppIcon.setImageDrawable(icon);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                holder.usageTime.setText(
                        DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));
            } else {
                Log.w(TAG, "No usage stats info for package:" + position);
            }
            return convertView;
        }

        void sortList(int sortOrder) {
            if (mDisplayOrder == sortOrder) {
                // do nothing
                return;
            }
            mDisplayOrder = sortOrder;
            sortList();
        }

        private void sortList() {
            if (mDisplayOrder == _DISPLAY_ORDER_USAGE_TIME) {
                if (localLOGV) Log.i(TAG, "Sorting by usage time");
                Collections.sort(mPackageStats, mUsageTimeComparator);
            } else if (mDisplayOrder == _DISPLAY_ORDER_LAST_TIME_USED) {
                if (localLOGV) Log.i(TAG, "Sorting by last time used");
                Collections.sort(mPackageStats, mLastTimeUsedComparator);
            } else if (mDisplayOrder == _DISPLAY_ORDER_APP_NAME) {
                if (localLOGV) Log.i(TAG, "Sorting by application name");
                Collections.sort(mPackageStats, mAppLabelComparator);
            }
            notifyDataSetChanged();
        }
    }
}