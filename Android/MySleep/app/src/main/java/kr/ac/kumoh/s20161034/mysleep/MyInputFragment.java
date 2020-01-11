package kr.ac.kumoh.s20161034.mysleep;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyInputFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_input, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("이름을 입력하세요");
        return rootView;
    }
}