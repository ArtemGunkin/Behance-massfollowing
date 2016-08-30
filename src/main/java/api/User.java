package api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String country;
    private String occupation;
    private String location;
    private String company;
    private String state;
    private String username;
    private String website;
    private String city;
    private String url;

    private int id;
    private int followers;
    private int likes;
    private int views;
    private int following;
    private int comments;

    private List<Project> projects;
    private Map<String, Integer> stats;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("create_on")
    private long createDate;

    @JsonProperty("fields")
    private ArrayList<String> fields;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("has_default_image")
    private boolean defaultImage;

    public User() {

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(boolean defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
        followers = stats.get("followers");
        likes = stats.get("appreciations");
        comments = stats.get("comments");
        following = stats.get("following");
        views = stats.get("views");
    }

    public int getFollowers() {
        return followers;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }

    public int getFollowing() {
        return following;
    }

    public int getComments() {
        return comments;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Project> setProjects(List<Project> projects) {
        this.projects = projects;
        return projects;
    }

    @Override
    public String toString() {
        return "User{" +
                "country='" + country + '\'' +
                ", website='" + website + '\'' +
                ", city='" + city + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createDate=" + createDate +
                ", id=" + id +
                ", stats=" + stats +
                '}';
    }
}
