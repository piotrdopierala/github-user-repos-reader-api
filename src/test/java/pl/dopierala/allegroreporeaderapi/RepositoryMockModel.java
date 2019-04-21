package pl.dopierala.allegroreporeaderapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

class RepositoryMockModel {
    private String full_name;
    private String html_url;
    private String description;
    private LocalDateTime created_at;

    public RepositoryMockModel() {
    }

    @JsonProperty("full_name")
    public String getName() {
        return full_name;
    }

    public void setName(String name) {
        this.full_name = name;
    }

    @JsonProperty("html_url")
    public String getUrl() {
        return html_url;
    }

    public void setUrl(String url) {
        this.html_url = url;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return created_at.atZone(ZoneId.of("Z")).toInstant().toString();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }
}
