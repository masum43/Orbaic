package com.orbaic.miner.wordpress;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
        String excerpt = post.getExcerpt().get("rendered").toString().replaceAll("\"", "");
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemViewTitle);
            shortContent = itemView.findViewById(R.id.itemViewContent);
            itemView.setOnClickListener(this::onClick);
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
                        Html.fromHtml(title,
                                Html.FROM_HTML_MODE_LEGACY).toString(), excerpt, content);

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
}
