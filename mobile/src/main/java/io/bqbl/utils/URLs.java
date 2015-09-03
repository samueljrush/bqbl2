package io.bqbl.utils;

import android.util.Log;

import io.bqbl.MyApplication;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 7/25/2015.
 */
public final class URLs {
  private static final String BQBLIO_URL = "http://www.bqbl.io/io";
  public static final String BQBLIO_JSON_URL = BQBLIO_URL + "/json";
  public static final String GAME_PHP = BQBLIO_JSON_URL + "/game.php";
  public static final String FEED_PHP = BQBLIO_JSON_URL + "/feed.php";
  public static final String SETTINGS_PHP = BQBLIO_JSON_URL + "/settings.php";
  public static final String PROFILE_PHP = BQBLIO_JSON_URL + "/profile.php";
  public static final String USER_PHOTO_FORMAT = BQBLIO_URL + "/img/profile_%d.png";
  public static final String FRIENDS_PHP = BQBLIO_JSON_URL + "/friends.php";
  public static final String PLACE_PHP = BQBLIO_JSON_URL + "/place.php";
  public static final String ADD_COMMENT_PHP = BQBLIO_JSON_URL + "/comment.php";

  private static final String ADD_FRIEND_FORMAT = BQBLIO_JSON_URL + "/addfriend.php?user=%d&friend=%d&add=%d";
  private static final String SET_GCM_FORMAT = BQBLIO_JSON_URL + "/setgcm.php?user=%d&token=%s";
  private static final String ADD_OOHOO_FORMAT = BQBLIO_JSON_URL + "/woohoo.php?user_id=%d&game_id=%d&value=%d";
  private URLs() {}

  public static final String getUserPhotoUrl(int userId) {
    return String.format(USER_PHOTO_FORMAT, userId);
  }

  public static final String getSetGcmFormatUrl(int userId, String token) {
    return String.format(SET_GCM_FORMAT, userId, token);
  }

  public static final String getSetOohooUrl(int userId, int gameId, int value) {
    return String.format(ADD_OOHOO_FORMAT, userId, gameId, value);
  }

  public static String getAddFriendUrl(int userId, boolean isAddOperation) {
    String url = String.format(ADD_FRIEND_FORMAT, MyApplication.getCurrentUser(), userId,
        isAddOperation ? 1 : 0);
    Log.d(logTag("DEBUGLOG"), "URL: " + url);
    return url;
  }
}
