package com.project.iway.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.project.iway.R;


public class Terms extends DialogFragment {
    WebView myWebView;

    public Terms() {
        // Required empty public constructor
    }

    public static Terms newInstance() {
      Terms fragment = new Terms();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.term, container, false);
        myWebView = view.findViewById(R.id.webview);
        myWebView.loadUrl("https://drive.google.com/file/d/1unY-Eoe09i4Qi5w-tiGJ_PCjvFc7xuTY/view");
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        return view;

    }




}
