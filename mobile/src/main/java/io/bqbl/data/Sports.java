package io.bqbl.data;

import com.google.auto.value.AutoValue;

import java.util.HashMap;
import java.util.Map;

import io.bqbl.R;

/**
 * Created by sam on 7/31/2015.
 */
public final class Sports {

  private static final Map<Integer, Sport> sports = new HashMap<>();

  static {
    addSport(Sport.create(1, "Basketball", 1, 0xFFF4511E, R.drawable.ic_place_black_24dp));
  }

  public static Sport getSport(int id) {
    return sports.get(1);
  }

  private static void addSport(Sport sport) {
    sports.put(sport.id(), sport);
  }

  @AutoValue
  public static abstract class Sport {

    public static Sport create(int id, String name, int scoreType, int color, int iconResource) {
      return new AutoValue_Sports_Sport(id, name, scoreType, color, iconResource);
    }

    public abstract int id();
    public abstract String name();
    public abstract int scoreType();
    public abstract int color();
    public abstract int iconResource();
  }
}