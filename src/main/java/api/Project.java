package api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("published_on")
    private int publishedOn;

    @JsonProperty("conceived_on")
    private int conceivedOn;

    public Project() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(int publishedOn) {
        this.publishedOn = publishedOn;
    }

    public int getConceivedOn() {
        return conceivedOn;
    }

    public void setConceivedOn(int conceivedOn) {
        this.conceivedOn = conceivedOn;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", publishedOn=" + publishedOn +
                ", conceivedOn=" + conceivedOn +
                '}';
    }
}
