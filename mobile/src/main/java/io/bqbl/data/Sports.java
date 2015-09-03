package io.bqbl.data;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.bqbl.R;

/**
 * Created by sam on 7/31/2015.
 */
public final class Sports {

  private static final Map<Integer, Sport> sports = new LinkedHashMap<>();
  private static final List<Sport> sportList = new ArrayList<>(110);

  static {
    addSport(Sport.create(1, "Basketball", 1, 0xFFF4511E, R.drawable.ic_basketball));
    addSport(Sport.create(31, "Beer Pong", 1, 0xFFF4511E, R.drawable.ic_basketball));
    addSport(Sport.create(100, "Fantasy Draft", 1, 0xFFF4511E, R.drawable.ic_basketball));

    for (Integer sportId : sports.keySet()) {
      sportList.add(sports.get(sportId));
    }
  }

  public static Sport getSport(int id) {
    Sport sport = sports.get(id);
    return (sport == null) ? sports.get(1) : sport;
  }

  private static void addSport(Sport sport) {
    sports.put(sport.id(), sport);
  }

  public static List<Sport> getSports() {
    return sportList;
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