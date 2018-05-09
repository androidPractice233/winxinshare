package com.scut.weixinshare.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.scut.weixinshare.R;
import com.scut.weixinshare.model.User;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInputFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TYPE = "type";
    private static final String VALUE = "value";

    private FragmentInteraction listener;
    private User user;
    // TODO: Rename and change types of parameters
    private String type="";

    private Button button;
    private EditText editText;
    public UserInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInputFragment newInstance(String param1, String param2) {
        UserInputFragment fragment = new UserInputFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view= inflater.inflate(R.layout.fragment_user_input, container, false);
        UserFragment fragment=(UserFragment)getActivity().getSupportFragmentManager().findFragmentByTag("user");
        user=fragment.getShowUser();
        editText=view.findViewById(R.id.edit_input);
        button=view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(type){
                    case "昵称":
                        user.setNickName(editText.getText().toString());
                        break;
                    case "城市":
                        user.setLocation(editText.getText().toString());
                        break;
                    case "生日":
                        user.setBirthday(editText.getText().toString());
                        break;

                }
                getActivity().onBackPressed();
            }
        });

        RadioGroup group=view.findViewById(R.id.radio_sex);
        if(type.equals("性别")){
            editText.setVisibility(View.GONE);
            group.setVisibility(View.VISIBLE);
            int selected=group.getCheckedRadioButtonId();
            if (selected==R.id.btn_male)
                user.setSex(0);
            else
                user.setSex(1);
            editText.setVisibility(View.VISIBLE);
            group.setVisibility(View.GONE);
        }
        return view;
    }



    @Override
    public void onAttach(final Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
        if (getArguments() != null) {
            type = getArguments().getString(TYPE);
        }
        if (context instanceof FragmentInteraction) {
            listener=(FragmentInteraction)context;
        }else
            throw new IllegalArgumentException("activity must implement FragmentInteraction");
        listener.changeTitle(type);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener.changeTitle("个人设置");
        listener=null;
    }

    public interface FragmentInteraction{
        void changeTitle(String title);
        public void updateUser(User user);
    }

}
