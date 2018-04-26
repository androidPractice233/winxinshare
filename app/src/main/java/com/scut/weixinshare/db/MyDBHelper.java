package com.scut.weixinshare.db;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public static  String DB_NAME = "test.db";

    /**
    用户表：
    用户id是定长的数字串，所以用text
     userName为登录名
     nickName为昵称
    portrait是用户头像存放的位置
     */
    private static final String CREATE_USER = "create table user(" +
            "userId text primary key, " +
            "userName text not null, " +
            "nickName text not null,"+
            "sex integer, " +
            "birthday text, " +
            "location text, " +
            "portrait text)";
    /**
    动态内容表：
    本地数据库表中不存储具体的经纬度，只保留位置名
    在联网获取最新动态前，展示本地数据库中最近的动态
     insertTime是动态被插入到数据库的时间，当选择最近动态展示时根据insertTime而不是createTime
    content是动态中的文字描述，可以为空
    pictureUrl是图片或者视频的地址
     */
    private static final String CREATE_MOMENT = "create table moment(" +
            "momentId text primary key," +
            "userId text not null," +
            "createTime text not null, " +
            "insertTime text not null,"+
            "location text not null," +
            "pictureUrl text," +
            "content text)";
    /**
    留言表：
    发送者和接收者不能为空
     */
    private static final String CREATE_COMMENT = "create table comment(" +
            "commentId text primary key," +
            "momentId text not null,"+
            "senderId text not null," +
            "receiverId text not null," +
            "createTime text not null," +
            "content text not null)";

    private Context context;

    public MyDBHelper(Context context, int version){
        super(context,DB_NAME,null,version);
        this.context = context;
        DBOperator.db_path = this.getReadableDatabase().getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_MOMENT);
        db.execSQL(CREATE_COMMENT);
        //软件完成后最后去掉
        Log.d("dbCreate","success");
        Toast.makeText(context,"db was created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists moment");
        db.execSQL("drop table if exists comment");

        onCreate(db);
    }
}