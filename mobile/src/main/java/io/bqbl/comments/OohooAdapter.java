package io.bqbl.comments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.data.User;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class OohooAdapter extends RecyclerView.Adapter<OohooAdapter.ViewHolder> {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd");
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");
  private List<Integer> mUserIds;
  private OohooFragment mFragment;
  private int mNotFriendColor;
  private int mFriendColor;

  public OohooAdapter(OohooFragment fragment, List<Integer> userIds) {
    mFragment = fragment;
    mUserIds = userIds;
    Resources res = fragment.getResources();
    mFriendColor = res.getColor(R.color.primary);
    mNotFriendColor = res.getColor(R.color.material_gray_background);
  }

  public void setData(List<Integer> userIds) {
    mUserIds = userIds;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mUserIds.size();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.oohoo_person, parent, false);

    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.bind(mUserIds.get(position));
    ////Log.d(logTag(this), String.format("Binding comment %d: %s", position, mUserIds.get(position).text()));
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView mPersonImage;
    private TextView mPersonName;
    private TextView mFriendStatus;
    private View mFriendStatusContainer;

    public ViewHolder(View commentView) {
      super(commentView);
      mPersonImage = (ImageView) commentView.findViewById(R.id.person_pic);
      mPersonName = (TextView) commentView.findViewById(R.id.person_name);
      mFriendStatus = (TextView) commentView.findViewById(R.id.friend_status);
      mFriendStatusContainer = commentView.findViewById(R.id.friend_status_card);
    }

    public void bind(final int userId) {
      User.requestUser(userId, true, new Listener<User>() {
        @Override
        public void onResult(User user) {
          WebUtils.setImageRemoteUri(mPersonImage, URLs.getUserPhotoUrl(userId));
          mPersonName.setText(user.name());
          if (userId == MyApplication.getCurrentUser()) {
            mFriendStatusContainer.setVisibility(View.INVISIBLE);
          }
          //Log.d(logTag(this), "Friend " + userId + " Status Visibility: " + mFriendStatus.getVisibility());

          mFragment.requestFriends(new Listener<Collection<Integer>>() {
            @Override
            public void onResult(final Collection<Integer> friends) {
              //Log.d(logTag(this), "onResult called");
              Resources res = mFriendStatus.getResources();
              if (friends.contains(userId)) {
                mFriendStatus.setBackgroundColor(mFriendColor);
                mFriendStatus.setText("Friends");
                mFriendStatus.setOnClickListener(null);
              } else {
                mFriendStatus.setBackgroundColor(mNotFriendColor);
                mFriendStatus.setText("Add Friend");
                mFriendStatus.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    friends.add(userId);
                    mFriendStatus.setVisibility(View.GONE);
                    mFriendStatus.setText("Friends");
                    Animator colorAnimator = animateColor(mFriendStatus, mNotFriendColor, mFriendColor)
                        .setDuration(200);
                    colorAnimator.addListener(new AnimatorListenerAdapter() {
                          @Override
                          public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mFriendStatus.setVisibility(View.VISIBLE);
                          }
                        });
                    colorAnimator.start();
                    MyApplication.getInstance().addToRequestQueue(
                        WebUtils.getJsonRequest(URLs.getAddFriendUrl(userId, true), new Response.Listener<JSONObject>() {
                          @Override
                          public void onResponse(JSONObject response) {

                          }
                        }));
                  }
                });
              }
            }
          });
        }
      });
    }

    private Animator animateColor(final View v, int colorFrom, int colorTo) {
      ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
      colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
          v.setBackgroundColor((Integer)animator.getAnimatedValue());
        }

      });
      return colorAnimation;
    }
  }

  private static String getDateString(Date date) {
    return String.format("%s at %s", DATE_FORMAT.format(date), TIME_FORMAT.format(date));
  }
}