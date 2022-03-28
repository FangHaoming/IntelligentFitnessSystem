package com.example.intelligentfitnesssystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.intelligentfitnesssystem.R;
import com.example.intelligentfitnesssystem.bean.Article;
import com.example.intelligentfitnesssystem.bean.Comment;
import com.example.intelligentfitnesssystem.bean.User;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    public static String commitRegister(Context context, String nickname, String phone, String pwd) throws IOException {
        User user = new User();
        user.setPhone(phone);
        user.setPwdHex(FileUtils.sha1String(pwd));
        user.setNickname(nickname);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", user);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(user), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String commitLogin(Context context, String phone, String pwd) throws IOException {
        User user = new User();
        user.setPhone(phone);
        user.setPwdHex(FileUtils.sha1String(pwd));
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_login);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(user), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String commitArticle(Context context, Article article) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);

        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(article), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String commitComment(Context context, Comment comment) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_comment);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(comment), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String modifyUser(Context context, User user) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + local_sp.getInt("user_id", 0);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(user), TYPE);
        Request request = new Request.Builder()
                .url(path)
                .put(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String followUser(Context context, int userId) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + userId + "/followers/" + local_sp.getInt("user_id", 0);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create("", TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getArticleList(Context context, String type, int pageNum, int pageSize) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + local_sp.getInt("user_id", 0) + "/" + type + "?page=" + pageNum + "&perPage=" + pageSize;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getArticleInfo(Context context, int articleId) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + articleId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getUserInfo(Context context, String phone) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + phone;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String praiseArticle(Context context, int articleId) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + articleId + "/like/" + local_sp.getInt("user_id", 0);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create("", TYPE);
        Request request = new Request.Builder()
                .url(url)
                .patch(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String praiseComment(Context context, int commentId) throws IOException {
        SharedPreferences global_sp = context.getSharedPreferences("data_global", Context.MODE_PRIVATE);
        SharedPreferences local_sp = context.getSharedPreferences("data_" + global_sp.getString("user_id", ""), Context.MODE_PRIVATE);
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/comments/" + commentId + "/like/" + local_sp.getInt("user_id", 0);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create("", TYPE);
        Request request = new Request.Builder()
                .url(url)
                .patch(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }
}
