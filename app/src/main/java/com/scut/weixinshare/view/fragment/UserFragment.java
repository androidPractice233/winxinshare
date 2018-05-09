package com.scut.weixinshare.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.contract.UserContract;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements UserContract.View ,View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public User getShowUser() {
        return presenter.getUser();
    }

    private UserContract.Presenter presenter;
    private FragmentManager fragmentManager;

    private LinearLayout ll_username;
    private LinearLayout ll_location;
    private LinearLayout ll_nickname;
    private LinearLayout ll_sex;
    private LinearLayout ll_birthday;

    private ImageView iv_portrait;
    private TextView text_nickname;
    private TextView text_username;
    private TextView text_userid;
    private TextView text_sex;
    private TextView text_birthday;
    private TextView text_Location;


    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId 要查看的user
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String userId) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getFragmentManager();
        assert getArguments() != null;
        presenter.getUserInfo(getArguments().getString("userId"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user, container, false);
        ll_username=view.findViewById(R.id.ll_username);
        ll_birthday=view.findViewById(R.id.ll_birthday);
        ll_nickname=view.findViewById(R.id.ll_nickname);
        iv_portrait=view.findViewById(R.id.iv_portrait);
        ll_sex=view.findViewById(R.id.ll_sex);
        ll_location=view.findViewById(R.id.ll_location);
        text_birthday=view.findViewById(R.id.textBirth);
        text_nickname=view.findViewById(R.id.textNickName);
        text_Location=view.findViewById(R.id.textLocation);
        text_sex=view.findViewById(R.id.textSex);
        text_username=view.findViewById(R.id.textUserName);
        text_userid=view.findViewById(R.id.textID);
        view.setOnClickListener(this);
        //初始化显示个人界面
        presenter.start();

        if(presenter.getUser().getUserId().equals(MyApplication.user.getUserId())) {
            ll_nickname.setOnClickListener(this);
            iv_portrait.setOnClickListener(this);
            ll_birthday.setOnClickListener(this);
            ll_sex.setOnClickListener(this);
            ll_location.setOnClickListener(this);

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        showUserInfo(presenter.getUser());
    }

    @Override
    public void showUserInfo(User user) {
        text_userid.setText(user.getUserId());
        text_username.setText(user.getUserName());
        if(user.getSex()==1)
            text_sex.setText("女");
        if(user.getSex()==0)
            text_sex.setText("男");
        text_Location.setText(user.getLocation());
        text_nickname.setText(user.getNickName());
        text_birthday.setText(user.getBirthday());
    }

    @Override
    public void showUserPhoto() {

    }

    @Override
    public void setPresenter(UserContract.Presenter presenter) {
        this.presenter=presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.updateUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_nickname:
                jumpToChange("昵称");
            case R.id.ll_birthday:
                jumpToChange("生日");
            case R.id.ll_sex:
                jumpToChange("性别");
                break;
            case R.id.ll_location:
                jumpToChange("城市");
            case R.id.iv_portrait:
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio(
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
        }
    }
    private void jumpToChange(String type){
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        UserInputFragment inputFragment=new UserInputFragment();
        inputFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_user,inputFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
