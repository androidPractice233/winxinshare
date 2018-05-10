package com.scut.weixinshare.view.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.R;
import com.scut.weixinshare.contract.UserContract;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.PersonalMomentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.scut.weixinshare.MyApplication.currentUser;

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
    private static final String ARG_USER = "userId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String type;
    private String mParam2;
    public User getShowUser() {
        return presenter.getUser();
    }

    private UserContract.Presenter presenter;
    private FragmentManager fragmentManager;
    private Boolean isEdit;
    private LinearLayout ll_username;
    private LinearLayout ll_location;
    private LinearLayout ll_nickname;
    private LinearLayout ll_sex;
    private LinearLayout ll_birthday;
    private RadioGroup group;
    private ImageView iv_portrait;
    private TextView text_nickname;
    private TextView text_username;
    private TextView text_userid;
    private TextView text_sex;
    private TextView text_birthday;
    private TextView text_Location;
    private EditText editText;
    private ConstraintLayout layout_info;
    private RelativeLayout layout_input;
    private TextView personweb;
private Button button;
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
        presenter.setShowUser(getArguments().getString("userId"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user, container, false);
        ll_username=view.findViewById(R.id.ll_username);
        layout_info=view.findViewById(R.id.frame_info);
        layout_input=view.findViewById(R.id.content_input);
        ll_birthday=view.findViewById(R.id.ll_birthday);
        ll_nickname=view.findViewById(R.id.ll_nickname);
        iv_portrait=view.findViewById(R.id.iv_portrait);
        ll_sex=view.findViewById(R.id.ll_sex);
        ll_location=view.findViewById(R.id.ll_location);
        text_birthday=view.findViewById(R.id.textBirth);
        text_nickname=view.findViewById(R.id.textNickName);
        text_Location=view.findViewById(R.id.textLocation);
        text_sex=view.findViewById(R.id.textSex);
         group=view.findViewById(R.id.radio_sex);
        text_username=view.findViewById(R.id.textUserName);
        text_userid=view.findViewById(R.id.textID);
        view.setOnClickListener(this);
        editText=view.findViewById(R.id.edit_input);
        button=view.findViewById(R.id.button);
//        personweb=view.findViewById(R.id.personweb);
        //初始化显示个人界面
        presenter.start();

        if(presenter.getUser().getUserId().equals(currentUser.getUserId())) {
            ll_nickname.setOnClickListener(this);
            iv_portrait.setOnClickListener(this);
            ll_birthday.setOnClickListener(this);
            ll_sex.setOnClickListener(this);
            ll_location.setOnClickListener(this);
            editText.setOnClickListener(this);
            button.setOnClickListener(this);
//            personweb.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void showUserInfo(User currentUser) {
        text_userid.setText(currentUser.getUserId());
        text_username.setText(currentUser.getUserName());
        if(currentUser.getSex()==1)
            text_sex.setText("女");
        if(currentUser.getSex()==0)
            text_sex.setText("男");
        text_Location.setText(currentUser.getLocation());
        text_nickname.setText(currentUser.getNickName());
        text_birthday.setText(currentUser.getBirthday());
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
        //presenter.updateUserInfo();保存
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_nickname:
                getActivity().setTitle("昵称");
                type="昵称";
                changeVisibility(1);
                break;
            case R.id.ll_birthday:
                getActivity().setTitle("生日");
                type="生日";
                changeVisibility(1);

                break;
            case R.id.ll_sex:
                getActivity().setTitle("性别");
                type="性别";
                changeVisibility(1);
                editText.setVisibility(View.INVISIBLE);
                group.setVisibility(View.VISIBLE);
                int selected=group.getCheckedRadioButtonId();
                if (selected==R.id.btn_male)
                    currentUser.setSex(0);
                else
                    currentUser.setSex(1);
                break;

            case R.id.ll_location:
                getActivity().setTitle("城市");
                type="城市";
                changeVisibility(1);
                break;
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
            case R.id.button:
                switch(type){
                    case "昵称":
                        currentUser.setNickName(editText.getText().toString());
                        break;
                    case "城市":
                        currentUser.setLocation(editText.getText().toString());
                        break;
                    case "生日":
                        currentUser.setBirthday(editText.getText().toString());
                        break;

                }break;
//            case R.id.personweb:
//                PersonalMomentActivity.actionStart(getActivity());
//                showUserInfo(currentUser);
//                changeVisibility(0);
//                break;
        }
    }

    private void changeVisibility(int flag){
        if (flag==0){
            layout_info.setVisibility(View.VISIBLE);
            layout_input.setVisibility(View.INVISIBLE);
        }
        else {
            layout_info.setVisibility(View.INVISIBLE);
            layout_input.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList = new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia p : selectList) {
                    fileList.add(new File(p.getPath()));
                }
                try {
                    Uri uri = data.getData();
                    String img_url = uri.getPath();
                    currentUser.setPortrait(img_url);
                    ContentResolver cr = getContext().getContentResolver();
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                    //将Bitmap设定到ImageView
                    iv_portrait.setImageBitmap(bitmap);
                    presenter.updateUserPhoto(fileList);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
