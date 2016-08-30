package api;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class R {
    public final static String TOKEN = "YOUR_ACCOUNT_TOKEN";

    public final static String GET = "get";
    public final static String POST = "post";
    public static final String DELETE = "delete";

    public final static String SORT_DATE = "published_date";
    public final static String SORT_FOLLOWED = "followed";

    public final static String CLIENT = "&client_id=vUJcND5iFeqtS0eVUyVsBtzAiGPd5RVN";
    public final static String USER_TOKEN = "user_token=" + TOKEN;
    public final static String API_KEY = "api_key=BehanceAndroid1&";
    public final static String LOCALE = "locale=ru_RU&";
    public final static String CLIENT_ID = "client_id=BehanceAndroid1&";
    public final static String REQUEST_DATA = "&" + API_KEY + LOCALE + CLIENT_ID + USER_TOKEN;

    public final static String HOST_USERS = "https://cc-api-behance.adobe.io/v2/users/";
    public final static String USERS = "https://cc-api-behance.adobe.io/v2/users?";
    public final static String HOST_PROJECT = "https://cc-api-behance.adobe.io/v2/projects/";
    public final static String API_HOST = "https://api.behance.net/v2/";
    public final static String SEARCH = API_HOST + "users?";
    public final static String FOLLOW = "/follow?";
    public final static String FOLLOWERS = "/followers?";
    public final static String FOLLOWING = "/following?";
    public final static String PROJECTS = "/projects?";
    public final static String LIKE = "/appreciate?";
    public final static String COMMENT = "/comments?";

    public final static int ID = 12465969;

    public static HttpClient getClient(){
        return HttpClientBuilder.create().build();
    }
}
