package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fast0n.xdalabsconsole.R;

import java.util.ArrayList;

public class ManageFragment extends Fragment {

    CustomAdapterManagerFragment adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        ListView listView = view.findViewById(R.id.list);
        ArrayList<DataItems> dataHours = new ArrayList<>();

        int value = 3;

        for (int i = 0; i<value;i++ ){

            dataHours.add(new DataItems("pippo" + i, "pippo" + i, "pippo" + i));
        }

        adapter = new CustomAdapterManagerFragment(view.getContext(), dataHours);
        listView.setAdapter(adapter);
        return view;
    }
}
