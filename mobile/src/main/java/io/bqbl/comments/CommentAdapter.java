package io.bqbl.comments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.bqbl.R;
import io.bqbl.data.Comment;
import io.bqbl.data.User;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd");
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");
  private List<Comment> mCommentList;

  public CommentAdapter(List<Comment> commentList) {
    mCommentList = commentList;
  }

  public void setData(List<Comment> commentList) {
    mCommentList = commentList;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mCommentList.size();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.comment_box, parent, false);

    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.bind(mCommentList.get(position));
    ////Log.d(logTag(this), String.format("Binding comment %d: %s", position, mCommentList.get(position).text()));
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView mCommentImageView;
    private TextView mCommentTextView;
    private TextView mCommentNameView;
    private TextView mCommentTimeView;

    public ViewHolder(View commentView) {
      super(commentView);
      mCommentImageView = (ImageView) commentView.findViewById(R.id.comment_user_image);
      mCommentTextView = (TextView) commentView.findViewById(R.id.comment_text);
      mCommentNameView = (TextView) commentView.findViewById(R.id.comment_name);
      mCommentTimeView = (TextView) commentView.findViewById(R.id.comment_time);
    }

    public void bind(Comment comment) {
      //Log.d(logTag(this), "Binding comment: time " + comment.date().getTime());
      WebUtils.setImageRemoteUri(mCommentImageView, URLs.getUserPhotoUrl(comment.userId()));
      mCommentTextView.setText(comment.text());
      mCommentTimeView.setText(getDateString(comment.date()));
      User.requestUser(comment.userId(), true, new Listener<User>() {
        @Override
        public void onResult(User user) {
          mCommentNameView.setText(user.name());
        }
      });
    }
  }

  public static String getDateString(Date date) {
    return String.format("%s at %s", DATE_FORMAT.format(date), TIME_FORMAT.format(date));
  }
}