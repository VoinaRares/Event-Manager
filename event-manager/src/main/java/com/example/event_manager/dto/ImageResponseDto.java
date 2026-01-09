package com.example.event_manager.dto;

public class ImageResponseDto {
    private Long id;
    private String url;
    private String placeholder;

    public ImageResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
}