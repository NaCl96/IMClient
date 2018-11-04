package com.xjtuse.student.IMClient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class InformationAdapter extends ArrayAdapter<Friend> {

    private List<Friend> information;
    private  int res;
    public InformationAdapter(@NonNull Context context, int res, @NonNull List<Friend> information) {
        super(context, res, information);
        this.information = information;
        this.res = res;
    }

    @Override
    public int getCount() {
        return information.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Friend friend = information.get(position);
        View view = LayoutInflater.from(getContext()).inflate(res,parent,false);

        TextView nameTv = view.findViewById(R.id.name);
//        TextView enterpriseTv = view.findViewById(R.id.enterprise);

        nameTv.setText(friend.getName());
//        enterpriseTv.setText(in.getEnterprise());

        return view;
    }
}
