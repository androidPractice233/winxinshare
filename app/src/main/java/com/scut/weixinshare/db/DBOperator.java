package com.scut.weixinshare.db;

import android.content.ContentValues;
import android.util.Log;

import com.scut.weixinshare.model.User;
import com.tencent.wcdb.Cursor;
import com.tencent.wcdb.database.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DBOperator {
    public static String db_path;
    private SQLiteDatabase database;

    public DBOperator(){
        database = SQLiteDatabase.openDatabase(db_path,null,SQLiteDatabase.OPEN_READWRITE);
    }
    public void close(){
        database.close();
    }

    private boolean isUserExist(String id){
        Cursor cursor = database.rawQuery("select * from user where userId = ?",
                new String[]{id});
        int t = cursor.getCount();
        if(t!=0){
            Log.d("testUser","user was existed");
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    private boolean isUserNameExist(String userName){
        Cursor cursor = database.rawQuery("select * from user where userName = ?",
                new String[]{userName});
        int t = cursor.getCount();
        if(t!=0){
            Log.d("testUser","user was existed");
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /** 用户表的操作 **/
    //插入新用户信息到数据库，如果用户已存在，reutrn false
    public boolean insertUser(User user){
        if(isUserExist(user.getUserId()))
            return false;

        database.execSQL("insert into user(userId,userName,nickName,sex,birthday,location,portrait) values(?,?,?,?,?,?,?)",
                new Object[]{user.getUserId(),user.getUserName(),user.getNickName(),user.getSex(),user.getBirthday(),user.getLocation(),user.getPortrait()});
        Log.d("insertUser","insert user success:"+user.getUserId());
        return true;
    }
    //更新用户头像
    public boolean updateUserPortrait(User user){
        if(isUserExist(user.getUserId())) {
            database.execSQL("update user set portrait = ? where userId = ?",
                    new String[]{user.getPortrait(), user.getUserId()});
            return true;
        }
        return false;
    }
    //更新用户信息
    public boolean updateUser(User user){
        if(isUserExist(user.getUserId())) {
            ContentValues values = new ContentValues();
            values.put("userName",user.getUserName());
            values.put("nickName",user.getNickName());
            values.put("sex",user.getSex());
            values.put("birthday",user.getBirthday());
            values.put("location",user.getLocation());
            values.put("avatarUrl",user.getPortrait());
            database.update("user",values,"userId=?",new String[]{user.getUserId()});
            Log.d("update","success"+user.getUserId());
            return true;
        }
        Log.d("update","fail");
        return false;
    }
    //删除用户信息
    public boolean deleteUser(String userId){
        if(isUserExist(userId)){
            database.execSQL("delete from user where userId=?",
                    new String[]{userId});
            Log.d("delete","success:"+userId);
            return true;
        }
        Log.d("delete","user not exist");
        return false;
    }
    //根据id查找用户
    public User selectUser(String userId){
        Cursor cursor = database.rawQuery("select * from user where userId = ?",
                new String[]{userId});
        int t = cursor.getCount();
        if(t==0){
            Log.d("select user","user not exist");
            return null;
        }
        else if(t==1){
            cursor.moveToFirst();
            String userName = cursor.getString(cursor.getColumnIndex("userName"));
            String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
            int sex = cursor.getInt(cursor.getColumnIndex("sex"));
            String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String portrait = cursor.getString(cursor.getColumnIndex("portrait"));
            User user = new User(userId,userName,nickName);
            user.setSex(sex);
            if(birthday!=null) user.setBirthday(birthday);
            if(location!=null) user.setLocation(location);
            if(portrait!=null) user.setPortrait(portrait);
            return user;
        }
        else{
            //理论上这里不可能执行
            Log.d("select user","more than one user was selected");
            return null;
        }
    }
    //根据用户名查找用户
    public User selectUserByName(String userName){
        Cursor cursor = database.rawQuery("select * from user where userName = ?",
                new String[]{userName});
        int t = cursor.getCount();
        if(t==0){
            Log.d("select user","user not exist");
            return null;
        }
        else if(t==1){
            cursor.moveToFirst();
            String userId = cursor.getString(cursor.getColumnIndex("userId"));
            String nickName = cursor.getString(cursor.getColumnIndex("nickName"));
            int sex = cursor.getInt(cursor.getColumnIndex("sex"));
            String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String portrait = cursor.getString(cursor.getColumnIndex("portrait"));
            User user = new User(userId,userName,nickName);
            user.setSex(sex);
            if(birthday!=null) user.setBirthday(birthday);
            if(location!=null) user.setLocation(location);
            if(portrait!=null) user.setPortrait(portrait);
            return user;
        }
        else{
            //理论上这里不可能执行
            Log.d("select user","more than one user was selected");
            return null;
        }
    }

    /**
     * 动态表的操作
     * 动态表没有更新的需求
     * **/
    private boolean isMomentExist(String id){
        Cursor cursor = database.rawQuery("select * from moment where momentId = ?",
                new String[]{id});
        int t = cursor.getCount();
        if(t!=0){
            Log.d("testMoment","moment was existed");
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    //插入新动态
    public boolean insertMoment(Moment moment){
        if(isMomentExist(moment.getMomentId()))
            return false;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        database.execSQL("insert into moment(momentId,userId,createTime,updateTime, insertTime,location,content,pictureUrl) values(?,?,?,?,?,?,?,?)",
                new String[]{moment.getMomentId(),moment.getUserId(),moment.getCreateTime(),moment.getUpdateTime(), timestamp.toString(),moment.getLocation(),moment.getContent(),moment.getPictureUrl()});
        Log.d("insertMoment","insert moment success:"+moment.getContent());
        return true;
    }
    //删除动态
    public boolean deleteMoment(String momentId){
        if(isMomentExist(momentId)){
            database.execSQL("delete from moment where momentId=?",
                    new Object[]{momentId});
            Log.d("deleteMoment","delete moment success");
            return true;
        }
        Log.d("delete moment","moment not exist");
        return false;
    }
    //查找动态
    public Moment selectMoment(String momentId){
        Cursor cursor = database.rawQuery("select * from moment where momentId = ?",
                new String[]{momentId});
        int t = cursor.getCount();
        if(t==0){
            Log.d("selectMoment","moment not exist");
            return null;
        }
        else if(t==1){
            cursor.moveToFirst();
            String userId = cursor.getString(cursor.getColumnIndex("userId"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            String updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String pictureUrl = cursor.getString(cursor.getColumnIndex("pictureUrl"));
            Moment moment = new Moment(momentId,userId,createTime,updateTime, location);
            if(content!=null) moment.setContent(content);
            if(pictureUrl!=null) moment.setPictureUrl(pictureUrl);
            return moment;
        }
        else{
            //理论上这里不可能执行
            Log.d("select moment","more than one moment was selected");
            return null;
        }
    }
    //获取一定数量的最近动态
    public List<Moment> selectMomentsRecent(){
        List<Moment> moments = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from moment order by insertTime desc",null);
        if(cursor.moveToFirst()){
            do{
                String momentId = cursor.getString(cursor.getColumnIndex("momentId"));
                String userId = cursor.getString(cursor.getColumnIndex("userId"));
                String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                String updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String pictureUrl = cursor.getString(cursor.getColumnIndex("pictureUrl"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                moments.add(new Moment(momentId,userId,createTime,updateTime,location,pictureUrl,content));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return moments;
    }
    //获取某用户的动态
    private List<Moment> selectMomentByUser(String userId){
        List<Moment> moments = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from moment where userId = ? order by createTime desc",new String[]{userId});
        if(cursor.moveToFirst()){
            do{
                String momentId = cursor.getString(cursor.getColumnIndex("momentId"));
                String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                String updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String pictureUrl = cursor.getString(cursor.getColumnIndex("pictureUrl"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                moments.add(new Moment(momentId,userId,createTime,updateTime,location,pictureUrl,content));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return moments;
    }

    /**
     * 留言表操作
     * 没有更新需求
     * 插入频繁
     */
    private boolean isCommentExist(String id){
        Cursor cursor = database.rawQuery("select * from comment where commentId = ?",
                new String[]{id});
        int t = cursor.getCount();
        if(t!=0){
            Log.d("testComment","comment was existed");
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    //插入到留言表
    public boolean insertComment(Comment comment){
        if(isCommentExist(comment.getCommentId())){
            Log.d("insert comment","comment was existed");
            return false;
        }
        database.execSQL("insert into comment(commentId,momentId,senderId,receiverId,createTime,content)values(?,?,?,?,?,?)",
                new String[]{comment.getCommentId(),comment.getMomentId(),comment.getSenderId(),comment.getReceiverId(),comment.getCreateTime(),comment.getContent()});
        Log.d("insert comment","insert success"+comment.getContent());
        return true;
    }
    //删除留言
    public boolean deleteComment(String commentId){
        if(isCommentExist(commentId)){
            database.execSQL("delete from comment where commentId=?",
                    new Object[]{commentId});
            Log.d("delete comment","delete comment success");
            return true;
        }
        Log.d("delete comment","comment not exist");
        return false;
    }
    //查找留言
    public Comment selectCommentById(String commentId){
        Cursor cursor = database.rawQuery("select * from comment where commentId = ?",
                new String[]{commentId});
        int t = cursor.getCount();
        if(t==0){
            Log.d("select comment","comment not exist");
            return null;
        }
        else if(t==1){
            cursor.moveToFirst();
            String momentId = cursor.getString(cursor.getColumnIndex("momentId"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
            Comment comment = new Comment(commentId,momentId,senderId,receiverId,createTime,content);
            return comment;
        }
        else{
            //理论上这里不可能执行
            Log.d("select comment","more than one comment was selected");
            return null;
        }
    }
    //查找某动态的所有留言
    public List<Comment> selectCommentByMoment(String momentId,String userId){
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from comment where momentId = ? and (senderId = ? or receiverId = ?) order by createTime",
                new String[]{momentId,userId,userId});
        //Cursor cursor = database.rawQuery("select * from comment",null);
        int t = cursor.getCount();
        Log.d("cursorSize",t+"");
        if(t==0){
            Log.d("select comment","no comment");
            return comments;
        }
        if(cursor.moveToFirst()) {
            do{
                String commentId = cursor.getString(cursor.getColumnIndex("commentId"));
                String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
                String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
                String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                comments.add(new Comment(commentId,momentId,senderId,receiverId,createTime,content));
            }while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("commentSize",comments.size()+"");
        return comments;
    }
    //获取和某用户有关的评论
    public List<Comment> selectCommentByUser(String userId){
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from comment where receiverId = ?  or receiverId = ? order by createTime desc" ,
                new String[]{userId,""});
        //Cursor cursor = database.rawQuery("select * from comment",null);
        int t = cursor.getCount();
        Log.d("cursorSize",t+"");
        if(t==0){
            Log.d("select comment","no comment");
            return comments;
        }
        if(cursor.moveToFirst()) {
            do{
                String commentId = cursor.getString(cursor.getColumnIndex("commentId"));
                String momentId = cursor.getString(cursor.getColumnIndex("momentId"));
                String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
                String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
                String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                comments.add(new Comment(commentId,momentId,senderId,receiverId,createTime,content));
            }while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("commentSize",comments.size()+"");
        return comments;
    }

    //获取最近的评论时间
    public String getLastTime(){
        Cursor cursor = database.rawQuery("select max(createTime) as maxTime from comment",null);
        if(cursor.getCount()==1) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("maxTime"));
        }
        return null;
    }

    //查找某动态的所有留言
    public List<Comment> selectCommentUnderMoment(String momentId){
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from comment where momentId = ? order by createTime",
                new String[]{momentId});
        Log.d("cursorSize",cursor.getCount()+"");
        if(cursor.moveToFirst()) {
            do{
                String commentId = cursor.getString(cursor.getColumnIndex("commentId"));
                String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
                String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
                String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                comments.add(new Comment(commentId,momentId,senderId,receiverId,createTime,content));
            }while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("commentSize",comments.size()+"");
        return comments;
    }

    //查找某动态的所有留言id
    public List<String> selectCommentIdUnderMoment(String momentId){
        //List<Comment> comments = new ArrayList<>();
        List<String> commentIds = new ArrayList<>();
        Cursor cursor = database.rawQuery("select commentId from comment where momentId = ? order by createTime",
                new String[]{momentId});
        //Cursor cursor = database.rawQuery("select * from comment",null);
        Log.d("cursorSize",cursor.getCount()+"");
        if(cursor.moveToFirst()) {
            do{
                String commentId = cursor.getString(cursor.getColumnIndex("commentId"));
                //String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
                //String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
                //String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                //String content = cursor.getString(cursor.getColumnIndex("content"));
                //comments.add(new Comment(commentId,momentId,senderId,receiverId,createTime,content));
                commentIds.add(commentId);
            }while (cursor.moveToNext());
        }
        cursor.close();
        //Log.d("commentSize",comments.size()+"");
        Log.d("commentSize",commentIds.size()+"");
        return commentIds;
    }
}
