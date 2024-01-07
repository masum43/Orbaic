package com.orbaic.miner.home;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post2 extends ArrayList<Post2.Post2Item> {
    public static class Post2Item {
        @SerializedName("post_body")
        private String postBody;
        @SerializedName("post_image")
        private String postImage;
        @SerializedName("post_link")
        private String postLink;
        @SerializedName("post_title")
        private String postTitle;

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }

        public String getPostImage() {
            return postImage;
        }

        public void setPostImage(String postImage) {
            this.postImage = postImage;
        }

        public String getPostLink() {
            return postLink;
        }

        public void setPostLink(String postLink) {
            this.postLink = postLink;
        }

        public String getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(String postTitle) {
            this.postTitle = postTitle;
        }
    }
}
