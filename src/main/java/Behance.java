import api.Project;
import api.R;
import api.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Behance {
    private Path followingFile = Paths.get("src/main/resources/following.txt");
    private Path commentsFile = Paths.get("src/main/resources/commented_projects.txt");
    private ArrayList<Integer> fields = new ArrayList<>();
    private ClassLoader classLoader = getClass().getClassLoader();
    private ObjectMapper mapper = new ObjectMapper();
    private HashSet<String> followedUsersID;
    private HashSet<String> commentedProjectsID;

    public static void main(String[] args) throws IOException, InterruptedException {
        new Behance();
    }

    public Behance() throws IOException, InterruptedException {
        followedUsersID = new HashSet<>(Files.readAllLines(followingFile, Charset.forName("UTF-8")));
        commentedProjectsID = new HashSet<>((Files.readAllLines(commentsFile, Charset.forName("UTF-8"))));

        fields.add(135);
        fields.add(102);
        fields.add(44);
        fields.add(5);
        fields.add(48);
        fields.add(109);
        fields.add(133);
        fields.add(7);
        fields.add(9);
        fields.add(12);
        fields.add(25);

        followNewUsers();
        commentNewProjects();
        unfollow(300);
    }

    private void unfollow(int count) throws IOException, InterruptedException {
        for (int i = 0; i < (count / 20); i++) {
            ArrayList<User> users = new ArrayList<>(getFollowing(R.ID, 20));
            for (User user : users)
                if (unfollow(user))
                    System.out.println("Unfollow from [" + user.getUsername() + "]");
            Thread.sleep(2000);
        }

    }

    private boolean unfollow(User user) throws UnsupportedEncodingException {
        String url = R.HOST_USERS + user.getId() + R.FOLLOW;
        JSONObject response = basic(url, R.DELETE);
        return response.getInt("http_code") == 200;
    }

    private void commentNewProjects() throws IOException, InterruptedException {
        boolean isFinished = false;
        File commentFile = new File(classLoader.getResource("comments.txt").getFile());
        File callbackFile = new File(classLoader.getResource("callback.txt").getFile());
        ArrayList<String> comments = (ArrayList<String>) Files.readAllLines(commentFile.toPath());
        ArrayList<String> callback = (ArrayList<String>) Files.readAllLines(callbackFile.toPath());
        int commentsCount = 0;

        for (Integer field : fields) {
            for (int k = 0; k < 30 && !isFinished; k++) {
                ArrayList<User> users = new ArrayList<>();
                users.addAll(recentUsers("RU", field, k));
                if (users.size() == 0)
                    break;

                for (User user : users) {
                    if (commentsCount > 100) {
                        isFinished = true;
                        return;
                    }

                    if (commentFilter(user)) {
                        Random random = new Random();
                        Project project = user.getProjects().get(0);
                        String commentString = comments.get(random.nextInt(comments.size())) + " \n"
                                + callback.get(random.nextInt(callback.size()));

                        if (like(project))
                            System.out.println("[" + commentsCount + "] Like " + project.getName());

                        if (comment(project, commentString)) {
                            System.out.println("[" + commentsCount++ + "] Comment:\n" + commentString);
                            commentedProjectsID.add(project.getId() + "");
                        }

                        Thread.sleep(3000);
                    }
                }

                Files.write(commentsFile, commentedProjectsID, Charset.forName("UTF-8"));
            }
        }
    }


    private void likeNewProjects() throws IOException, InterruptedException {
        ArrayList<User> users = new ArrayList<User>();
        for (int k = 0; k < 30; k++) {
            users.addAll(recentUsers("CA", 102, k));
            Thread.sleep(1000);
        }

        for (User user : users) {
            System.out.println("User: " + user.getFirstName() + " " + user.getLastName());
            ArrayList<Project> projects = (ArrayList<Project>) getProjects(user);
            for (int i = 0; i < 2 && i < projects.size(); i++) {
                Project project = projects.get(i);
                System.out.println("Project: " + project.getName());
                if (like(project))
                    System.out.println("Liked Project.");
                else {
                    System.out.println("Like error.");
                    Thread.sleep(600000);
                }
                Thread.sleep(2000);
            }
        }
    }

    private void followNewUsers() throws IOException, InterruptedException {
        ArrayList<User> users = new ArrayList<>();
        System.out.println("Followers count: " + followedUsersID.size());

        for (Integer field : fields) {
            for (int k = 0; k < 30; k++) {
                users.addAll(recentUsers(null, field, k));
                if (users.size() == 0)
                    break;

                for (User user : users) {
                    if (followFilter(user)) {
                        try {
                            if (follow(user)) {
                                System.out.println("Follow [" + user.getFirstName() + " " + user.getLastName() + "] (" +
                                        user.getFollowers() + "/" + user.getFollowing() + ")");
                                followedUsersID.add(user.getId() + "");
                                like(user.getProjects().get(0));
                            } else {
                                System.err.println("Follow error.");
                                Files.write(followingFile, followedUsersID, Charset.forName("UTF-8"));
                                return;
                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }

                        Thread.sleep(3000);
                    }
                }

                Files.write(followingFile, followedUsersID, Charset.forName("UTF-8"));
                users = new ArrayList<>();
            }
        }
    }

    private boolean followFilter(User user) throws IOException {
        return user.getFollowers() < 120 && user.getFollowers() > 40 && user.getFollowing() > 5 &&
                !followedUsersID.contains(user.getId() + "") && getProjects(user).size() > 4;

    }

    private boolean commentFilter(User user) throws IOException {
        return user.getFollowers() < 100 && user.getFollowers() > 30 &&
                user.getFollowing() > 5 && user.setProjects(getProjects(user)).size() > 0 &&
                !commentedProjectsID.contains(user.getProjects().get(0).getId() + "");
    }

    private void followAll() throws IOException, InterruptedException {
        int page = 2;
        ArrayList<User> pageUsers;
        ArrayList<User> users = new ArrayList<User>();

        while (true) {
            pageUsers = (ArrayList<User>) search("RU", "Samara", page);
            if (pageUsers == null || pageUsers.isEmpty())
                break;

            users.addAll(pageUsers);
            System.out.println("Processing " + users.size() + " people....");
            page++;
        }

        System.out.println("Find " + users.size() + " people.");

        for (User user : users) {
            follow(user);
            Thread.sleep(2000);
        }
    }


    private List<User> search(String country, String city, int page) throws IOException {
        String url = R.SEARCH + "country=" + country + "&city=" + city + "&page=" + page + R.CLIENT;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);

        get.setHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1)");

        HttpResponse response = client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            System.out.println(statusCode);
            return null;
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null)
            result.append(line);

        JSONObject responseJson = new JSONObject(result.toString());

        List<User> users = mapper.readValue(
                responseJson.getJSONArray("users").toString(),
                mapper.getTypeFactory().constructCollectionType(List.class, User.class));

        return users;
    }

    private boolean follow(User user) throws IOException {
        String url = R.HOST_USERS + user.getId() + R.FOLLOW;
        JSONObject response = basic(url, R.POST);
        return response.getInt("http_code") == 200;
    }

    private boolean like(Project project) throws IOException {
        String url = R.HOST_PROJECT + project.getId() + R.LIKE;
        JSONObject response = basic(url, R.POST);
        return (response.getInt("http_code") == 200);
    }

    private boolean comment(Project project, String comment) throws IOException {
        String url = R.HOST_PROJECT + project.getId() + R.COMMENT;
        JSONObject response = basic(url, comment);
        return response.has("http_code") && (response.getInt("http_code") == 201);
    }

    private List<User> recentUsers(String country, int field, int page) throws IOException {
        String fieldString;
        if (country == null)
            country = "";
        else
            country = "&country=" + country;

        if (field == 0)
            fieldString = "";
        else
            fieldString = "&field=" + field;

        String url = R.USERS +
                "page=" + page +
                "&per_page=24" +
                "&sort=" + R.SORT_DATE +
                country + fieldString;

        return mapper.readValue(
                basic(url, R.GET).getJSONArray("users").toString(),
                mapper.getTypeFactory().constructCollectionType(List.class, User.class));
    }


    public List<Project> getProjects(User user) throws IOException {
        String url = R.HOST_USERS + user.getId() + R.PROJECTS + "&per_page=6";
        JSONObject response = basic(url, R.GET);
        if (response.has("projects")) {
            ArrayList<Project> projects = mapper.readValue(
                    response.getJSONArray("projects").toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Project.class));

            user.setProjects(projects);
            return projects;
        } else
            return new ArrayList<>();
    }

    public List<User> getFollowers(int id) throws IOException {
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<User> requestUsers;
        int page = 1;
        do {
            String url = R.HOST_USERS + id + R.FOLLOWERS +
                    "page=" + page++ +
                    "&per_page=20" +
                    "&sort=" + R.SORT_FOLLOWED;
            requestUsers = mapper.readValue(
                    basic(url, R.GET).getJSONArray("followers").toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, User.class));

            users.addAll(requestUsers);
        } while (requestUsers.size() == 20);

        return users;
    }

    public List<User> getFollowing(int id, int count) throws IOException {
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<User> requestUsers;
        int page = 1;
        do {
            String url = R.HOST_USERS + id + R.FOLLOWING +
                    "page=" + page++ +
                    "&per_page=20" +
                    "&sort=" + R.SORT_FOLLOWED;
            requestUsers = mapper.readValue(
                    basic(url, R.GET).getJSONArray("following").toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, User.class));

            users.addAll(requestUsers);
            System.out.println("Обработка подписок (" + users.size() + ")");
        } while (requestUsers.size() == 20 && users.size() < count);

        return users;
    }


    private JSONObject basic(String url, String method) throws UnsupportedEncodingException {
        HttpRequestBase request;
        if (method.equals(R.GET))
            request = new HttpGet(url + R.REQUEST_DATA);
        else if (method.equals(R.POST))
            request = new HttpPost(url + R.REQUEST_DATA);
        else if (method.equals(R.DELETE)) {
            request = new HttpDelete(url + R.REQUEST_DATA);
        } else {
            request = new HttpPost(url + R.REQUEST_DATA);
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("comment", method));
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));
        }

        request.setHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1)");
        HttpClient client = R.getClient();

        try {
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null)
                result.append(line);

            if (statusCode != 200 && statusCode != 429 && statusCode != 201) {
                if (statusCode != 404) {
                    System.out.println(statusCode);
                    System.out.println(result);
                }

                return new JSONObject();
            }

            JSONObject resObject = new JSONObject(result.toString());
            return resObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
