package io.bqbl.data;

import android.util.LruCache;
import android.util.Pair;

import com.google.android.gms.location.places.Place;

/**
 * Created by sam on 7/26/2015.
 */
public final class CacheManager {
  private static CacheManager sInstance;

  private LruCache<Integer, User> mUsers = new LruCache<>(100);
  private LruCache<Pair<Integer, Integer>, Team> mTeams = new LruCache<>(100);
  private LruCache<Integer, Game> mGames = new LruCache<>(100);
  private LruCache<String, Place> mPlaces = new LruCache<>(100);

  private CacheManager() {
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
    mPlaces.put(place.getId(), place);
  }

  public Place getPlace(String id) {
    return mPlaces.get(id);
  }
}
