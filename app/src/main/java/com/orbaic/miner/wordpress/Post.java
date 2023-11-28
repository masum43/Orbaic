package com.orbaic.miner.wordpress;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Post {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private JsonObject title;

        @SerializedName("content")
        private JsonObject content;

        @SerializedName("excerpt")
        private JsonObject excerpt;

        @SerializedName("featured_media")
        private int featured_media;

        public Post() {
        }

        public Post(int id, JsonObject title, JsonObject content, JsonObject excerpt, int featured_media) {
                this.id = id;
                this.title = title;
                this.content = content;
                this.excerpt = excerpt;
                this.featured_media = featured_media;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public JsonObject getTitle() {
                return title;
        }

        public void setTitle(JsonObject title) {
                this.title = title;
        }

        public JsonObject getContent() {
                return content;
        }

        public void setContent(JsonObject content) {
                this.content = content;
        }

        public JsonObject getExcerpt() {
                return excerpt;
        }

        public void setExcerpt(JsonObject excerpt) {
                this.excerpt = excerpt;
        }

        public int getFeatured_media() {
                return featured_media;
        }

        public void setFeatured_media(int featured_media) {
                this.featured_media = featured_media;
        }
}
