package com.scimmia.mybus.readme;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.GlobalData;
import com.scimmia.mybus.utils.base.BaseFragment;
import com.scimmia.mybus.utils.db.UpdateDBTask;
import com.scimmia.mybus.utils.http.HttpDownloadTask;
import com.scimmia.mybus.utils.http.HttpListener;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.util.LinkedList;

public class ReadmeHomeFragment extends BaseFragment {
    public static ReadmeHomeFragment newInstance() {
        ReadmeHomeFragment fragment = new ReadmeHomeFragment();
        return fragment;
    }


    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    LinkedList<MutablePair<String,String>> mTitles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_readme_home, container, false);


        mTitles = new LinkedList<>();
        String[] titles = getResources().getStringArray(R.array.array_title);
        String[] contents = getResources().getStringArray(R.array.array_content);
        int max = Math.min(titles.length,contents.length);
        for (int i = 0 ; i< max;i++){
            mTitles.add(new MutablePair<String, String>(titles[i],contents[i]));
        }


        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_readme_home);
        mToolbar.setTitle("帮助");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);

        ReadmeAdapter mAdapter = new ReadmeAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MutablePair<String,String> temp = (MutablePair<String, String>) adapter.getData().get(position);
                if (temp.getRight().startsWith("http://") && temp.getRight().endsWith(".apk")){
//                    new HttpDownloadTask(
//                            _mActivity, temp.getRight(), GlobalData.downAPKTag,
//                            GlobalData.newapkFile, new HttpListener() {
//                        @Override
//                        public void onSuccess(String tag, String content) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(Uri.fromFile(new File(GlobalData.newapkFile)), "application/vnd.android.package-archive");
//
//                            startActivity(intent);
//
//                        }
//                    }).execute();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(temp.getRight());
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    class ReadmeAdapter extends BaseQuickAdapter<MutablePair<String,String>, BaseViewHolder> {
        public ReadmeAdapter() {
            super(R.layout.holder_readme, mTitles);
        }

        @Override
        protected void convert(BaseViewHolder viewHolder, MutablePair<String,String> item) {
            viewHolder.setText(R.id.tv_title,item.getLeft())
                    .setText(R.id.tv_content,item.getRight());
        }
    }
}
