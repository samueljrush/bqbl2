package io.bqbl.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.bqbl.BuildConfig;
import io.bqbl.MyApplication;
import io.bqbl.utils.DiskLruCache;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 7/26/2015.
 */
public final class CacheManager {
  private static final String DISK_CACHE_SUBDIR = "images";
  private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
  private static CacheManager sInstance;

  private LruCache<Integer, User> mUsers = new LruCache<>(100);
  private LruCache<Pair<Integer, Integer>, Team> mTeams = new LruCache<>(100);
  private LruCache<Integer, Game> mGames = new LruCache<>(100);
  private LruCache<String, Place> mPlaces = new LruCache<>(100);
  private LruCache<String, Bitmap> mBitmapMemCache = new LruCache<>(10);
  private Set<Integer> mCurrentUserFriends = null;

  private Object mBitmapDiskCacheLock = new Object();
  private DiskLruCache<Bitmap> mBitmapDiskCache;
  private boolean mDiskCacheStarting = true;

  private CacheManager() {
    File cacheDir = getDiskCacheDir(MyApplication.getInstance(), DISK_CACHE_SUBDIR);
    new InitDiskCacheTask().execute(cacheDir);
  }

  class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
    @Override
    protected Void doInBackground(File... params) {
      synchronized (mBitmapDiskCacheLock) {
        File cacheDir = params[0];
        try {
          mBitmapDiskCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1 /* value count */, DISK_CACHE_SIZE);
          mDiskCacheStarting = false; // Finished initialization
          mBitmapDiskCacheLock.notifyAll(); // Wake any waiting threads
        } catch (Exception e) {
          Log.e(logTag(this), "Error setting up Disk Cache", e);
        }
      }
      return null;
    }
  }

  public static CacheManager getInstance() {
    if (sInstance == null) {
      sInstance = new CacheManager();
    }
    return sInstance;
  }

  public void addUser(User user) {
    mUsers.put(new Integer(user.id()), user);
  }

  public User getUser(int id) {
    return mUsers.get(id);
  }

  public void addTeam(Team team) {
    mTeams.put(new Pair<Integer, Integer>(team.gameId(), team.teamId()), team);
  }

  public Team getTeam(int gameId, int teamId) {
    return mTeams.get(new Pair<Integer, Integer>(gameId, teamId));
  }

  public void addGame(Game game) {
    mGames.put(new Integer(game.id()), game);
  }

  public Game getGame(int id) {
    return mGames.get(id);
  }

  public void addPlace(Place place) {
    mPlaces.put(place.id(), place);
  }

  public Place getPlace(String id) {
    return mPlaces.get(id);
  }

  public void addBitmapToDiskCache(String key, Bitmap bitmap) {
    mBitmapMemCache.put(key, bitmap);

    // Also add to disk cache
    synchronized (mBitmapDiskCacheLock) {
      if (mBitmapDiskCache != null && mBitmapDiskCache.getValue(key) == null) {
        mBitmapDiskCache.put(key, bitmap);
      }
    }
  }

  public Bitmap getBitmapFromDiskCache(String key) {
    Bitmap bitmap = mBitmapMemCache.get(key);
    if (bitmap != null) {
      return bitmap;
    }

    synchronized (mBitmapDiskCacheLock) {
      // Wait while disk cache is started from background thread
      while (mDiskCacheStarting) {
        try {
          mBitmapDiskCacheLock.wait();
        } catch (InterruptedException e) {}
      }
      if (mBitmapDiskCache != null) {
        return mBitmapDiskCache.getValue(key);
      }
    }
    return null;
  }

  public Set<Integer> getCurrentUserFriends() {
    return mCurrentUserFriends;
  }

  public void setCurrentUserFriends(Set<Integer> friends) {
    mCurrentUserFriends = friends;
  }

  // Creates a unique subdirectory of the designated app cache directory. Tries to use external
  // but if not mounted, falls back on internal storage.
  public static File getDiskCacheDir(Context context, String uniqueName) {
    // Check if media is mounted or storage is built-in, if so, try and use external cache dir
    // otherwise use internal cache dir
    final String cachePath = context.getCacheDir().getPath();

    return new File(cachePath + File.separator + uniqueName);
  }
}
