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

import static com.example.intelligentfitnesssystem.MyApplication.global_sp;
import static com.example.intelligentfitnesssystem.MyApplication.localUser;
import static com.example.intelligentfitnesssystem.MyApplication.local_sp;

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
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(jsonObject), TYPE);
        System.out.println("****register request " + JSON.toJSONString(user));
        Request request = new Request.Builder()
                .url(path)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }
    //??????????????????
    public static String commitLogin(Context context, String phone, String pwd) throws IOException {
        User user = new User();
        user.setPhone(phone);
        user.setPwdHex(FileUtils.sha1String(pwd));
//        user.setPwdHex(pwd);
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String commitArticle(Context context, Article article) throws IOException {
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String commitComment(Context context, Comment comment) throws IOException {
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String modifyUser(Context context, User user, byte[] imgData) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + localUser.getId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", user);
        jsonObject.put("imgData", imgData);
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(jsonObject), TYPE);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String followUser(Context context, int userId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + userId + "/followers/" + localUser.getId();
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create("", TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String unFollowUser(Context context, int userId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/" + userId + "/followers/" + localUser.getId();
        MediaType TYPE = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create("", TYPE);
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getArticleList(Context context, String type, int pageNum, int pageSize) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + localUser.getId() + "/" + type + "?page=" + pageNum + "&perPage=" + pageSize;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getArticleInfo(Context context, int articleId) throws IOException {
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getChildComment(Context context, int commentId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/comments/" + commentId + "/subComment";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getUserInfo(Context context, String phone) throws IOException {
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String getUserInfo(Context context, int userId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_register);
        String url = path + "/id/" + userId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", local_sp.getString("token", ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String praiseArticle(Context context, int articleId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + articleId + "/like/" + localUser.getId();
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String cancelPraiseArticle(Context context, int articleId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/" + articleId + "/dislike/" + localUser.getId();
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String praiseComment(Context context, int commentId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/comments/" + commentId + "/like/" + localUser.getId();
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }

    public static String cancelPraiseComment(Context context, int commentId) throws IOException {
        String path = context.getResources().getString(R.string.baseUrl) + context.getResources().getString(R.string.api_release_article);
        String url = path + "/comments/" + commentId + "/dislike/" + localUser.getId();
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
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).string();
        }
        return context.getResources().getString(R.string.status_server_error);
    }
}
