package io.bqbl.data;

import com.google.auto.value.AutoValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 7/31/2015.
 */
public final class Sports {

  private static final Map<Integer, Sport> sports = new HashMap<>();

  public static Sport getSport(int id) {
    return sports.get(id);
  }

  @AutoValue
  public static abstract class Sport {

    public static Sport create(int id, String name, int scoreType) {
      return new AutoValue_Sports_Sport(id, name, scoreType);
    }

    abstract int id();
    abstract String name();
    abstract int scoreType();
  }
}