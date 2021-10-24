package com.project.iway.Nav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.project.iway.Auth.Login;
import com.project.iway.R;


public class Setting extends DialogFragment {
    private Button logoutBtn, backBtn;
    private FirebaseAuth fAuth;
    public Setting() {
        // Required empty public constructor
    }

    public static Setting newInstance() {
      Setting fragment = new Setting();
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
        View view = inflater.inflate(R.layout.setting, container, false);
        logoutBtn = view.findViewById(R.id.logout);
        backBtn = view.findViewById(R.id.backBtn);

        fAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                getActivity().startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        return view;

    }


}
