package com.orbaic.miner.wordpress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.orbaic.miner.R;
import com.orbaic.miner.WebViewContent;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> post) {
        this.context = context;
        this.posts = post;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        String title = post.getTitle().get("rendered").toString().replaceAll("\"", "");
        String excerpt = post.getExcerpt().get("rendered").toString().trim().replaceAll("\"", "");
      /*  if (excerpt.endsWith("\n")) {
            excerpt = excerpt.substring(0, excerpt.length() - 1);
        }*/
        excerpt = excerpt.replaceAll("\\\\n$", "");

        Log.e("featured_media", "featured_media: "+ post.getFeatured_media());

        Log.e("NEWS", "excerpt: "+excerpt );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.title.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
            holder.shortContent.setText(Html.fromHtml(excerpt, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,shortContent;
        ImageView ivShare;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemViewTitle);
            shortContent = itemView.findViewById(R.id.itemViewContent);
            ivShare = itemView.findViewById(R.id.ivShare);
            itemView.setOnClickListener(this::onClick);

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareSocial(ivShare.getContext());
                }


            });
        }

        private void shareSocial(Context context) {
            int position = this.getAdapterPosition();
            Post data = posts.get(position);
            String title = data.getTitle().get("rendered").toString().replaceAll("\"", "");
            String content = data.getContent().get("rendered").toString().replaceAll("\"", "");
            String excerpt = data.getExcerpt().get("rendered").toString().replaceAll("\"", "");

            content = contentFilter(content, "<ins", "</ins>");
            content = videoFilter(content, "<iframe", "/iframe>");

// Modify the content as needed for formatting, filtering, or HTML processing

            shareToSocial((Activity) context, title, excerpt, content);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {

            int position = this.getAdapterPosition();
            Post data = posts.get(position);

            /*int id = data.getId();
            String title = data.getTitle().get("rendered").toString().replaceAll("\"", "");
            String content = data.getContent().get("rendered").toString();

            Intent intent = new Intent(context, WebViewContent.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            context.startActivity(intent);*/

            String title = data.getTitle().get("rendered").toString().replaceAll("\"", "");
            String content = data.getContent().get("rendered").toString().replaceAll("\"", "");
            String excerpt = data.getExcerpt().get("rendered").toString().replaceAll("\"", "");

            content = contentFilter(content, "<ins", "</ins>");
            content = videoFilter(content, "<iframe", "/iframe>");

            Intent intent;
                intent = WebViewContent.createIntent(view.getContext(), data.getId(),
                        Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY).toString(), excerpt, content);

            view.getContext().startActivity(intent);

            //System.out.println(id);

        }

    }

    public String contentFilter(String content, String first, String last) {

        String contentOutput;
        String contentResult;


        //set index
        int firstIndex = content.indexOf(first);
        int lastIndex = content.lastIndexOf(last);

        if (firstIndex != -1 || lastIndex != -1) {

            //get substring
            contentOutput = content.substring(firstIndex, lastIndex + last.length());

            //replace
            contentResult = content.replace(contentOutput, "");

        } else {
            contentResult = content;
        }
        return contentResult;
    }

    public String videoFilter(String content, String first, String last) {

        String oldContentSubstring;
        String newContentSubstring;
        String contentResult;


        //set index
        int firstIndex = content.indexOf(first);
        int lastIndex = content.lastIndexOf(last);

        if (firstIndex != -1 || lastIndex != -1) {

            //get substring
            oldContentSubstring = content.substring(firstIndex, lastIndex + last.length());

            newContentSubstring = "<div class=\"videoWrapper\">" + oldContentSubstring + "</div>";

            contentResult = content.replace(oldContentSubstring, newContentSubstring);

        } else {
            contentResult = content;
        }
        return contentResult;

    }

    public void shareToSocial(Activity activity, String title, String excerpt, String formattedContent) {
        // Replace "\n" with actual newline characters
        title = title.replace("\\n", "\n");
        excerpt = excerpt.replace("\\n", "\n");
        formattedContent = formattedContent.replace("\\n", "\n");

        String cleanTitle = cleanHtmlTags(title).replaceAll("\\\\n$", "");
        String cleanExcerpt = cleanHtmlTags(excerpt).replaceAll("\\\\n$", "");
        String cleanContent = cleanHtmlTags(formattedContent).replaceAll("\\\\n$", "");
        String message = "Title: " + cleanTitle + "\n\n" +
                "Excerpt: " + cleanExcerpt + "\n\n" +
                "Content:\n" + cleanContent;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private String cleanHtmlTags(String htmlContent) {
        // Remove HTML tags using regex
        return htmlContent.replaceAll("<[^>]*>", "");
    }
}
