package io.bqbl.data;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    addSport(Sport.create(48,"Long Jump",1,0xFF66BB6A,R.drawable.athlete8));
    addSport(Sport.create(24,"Golf",3,0xFF66BB6A,R.drawable.golf25));
    addSport(Sport.create(55,"Mini Golf",3,0xFF66BB6A,R.drawable.golf25));
    addSport(Sport.create(46,"Javelin",1,0xFF66BB6A,R.drawable.javelin1));
    addSport(Sport.create(47,"High Jump",1,0xFF66BB6A,R.drawable.jumping30));
    addSport(Sport.create(61,"Decathlon",2,0xFF66BB6A,R.drawable.jumping30));
    addSport(Sport.create(72,"Hurling",1,0xFF66BB6A,R.drawable.lacrosse2));
    addSport(Sport.create(44,"Polo",1,0xFF66BB6A,R.drawable.polo1));
    addSport(Sport.create(7,"Tennis",4,0xFF66BB6A,R.drawable.racket1));
    addSport(Sport.create(62,"Hurdles",2,0xFF66BB6A,R.drawable.running35));
    addSport(Sport.create(12,"Soccer",1,0xFF66BB6A,R.drawable.sportsball15));
    addSport(Sport.create(101,"Three-legged race",7,0xFF66BB6A,R.drawable.sprint));
    addSport(Sport.create(99,"Sack Race",7,0xFF66BB6A,R.drawable.sprint));
    addSport(Sport.create(9,"Field Hockey",1,0xFF66BB6A,R.drawable.hockey1));
    addSport(Sport.create(88,"FIFA",1,0xFF66BB6A,R.drawable.sportsball15));
    addSport(Sport.create(92,"Foosball",1,0xFF66BB6A,R.drawable.football68));
    addSport(Sport.create(71,"Floorball",1,0xFF66BB6A,R.drawable.hockey1));
    addSport(Sport.create(8,"Hockey",1,0xFF66BB6A,R.drawable.ice46));
    addSport(Sport.create(13,"Lacrosse",1,0xFF66BB6A,R.drawable.lacrosse2));
    addSport(Sport.create(52,"Jousting",5,0xFF66BB6A,R.drawable.polo1));
    addSport(Sport.create(5,"Squash",4,0xFF66BB6A,R.drawable.racket4));
    addSport(Sport.create(6,"Racquetball",4,0xFF66BB6A,R.drawable.racket4));
    addSport(Sport.create(43,"Race",2,0xFF66BB6A,R.drawable.runer));
    addSport(Sport.create(34,"Cricket",1,0xFFBCAAA4,R.drawable.cricket2));
    addSport(Sport.create(54,"Rugby",1,0xFFBCAAA4,R.drawable.rugby28));
    addSport(Sport.create(11,"Football",1,0xFFBCAAA4,R.drawable.rugbyball));
    addSport(Sport.create(28,"Aussie Rules Football",1,0xFFBCAAA4,R.drawable.rugbyball));
    addSport(Sport.create(53,"Gaelic Football",1,0xFFBCAAA4,R.drawable.rugbyball));
    addSport(Sport.create(98,"Competitive eating",1,0xFFCE93D8,R.drawable.cutlery22));
    addSport(Sport.create(33,"Yahtzee",1,0xFFCE93D8,R.drawable.dice18));
    addSport(Sport.create(85,"Dota",5,0xFFCE93D8,R.drawable.fanstasy));
    addSport(Sport.create(87,"LoL",5,0xFFCE93D8,R.drawable.fanstasy));
    addSport(Sport.create(79,"Backgamon",5,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(80,"Checkers",6,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(81,"Chinese Checkers",5,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(82,"Mancala",5,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(84,"Puzzles",2,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(90,"Connect Four",5,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(91,"Reversi",1,0xFFCE93D8,R.drawable.games39));
    addSport(Sport.create(97,"Chess Boxing",6,0xFFCE93D8,R.drawable.horse170));
    addSport(Sport.create(83,"Scrabble",1,0xFFCE93D8,R.drawable.scrabble20));
    addSport(Sport.create(100,"Fantasy Draft",8,0xFFCE93D8,R.drawable.sky2));
    addSport(Sport.create(36,"Quidditch",1,0xFFCE93D8,R.drawable.wishes));
    addSport(Sport.create(78,"Chess",6,0xFFCE93D8,R.drawable.chesspiece1));
    addSport(Sport.create(89,"Air Hockey",4,0xFFCE93D8,R.drawable.game99));
    addSport(Sport.create(21,"Archery",1,0xFFF4511E,R.drawable.arrow42));
    addSport(Sport.create(22,"Badminton",4,0xFFF4511E,R.drawable.badminton9));
    addSport(Sport.create(41,"Billiards",5,0xFFF4511E,R.drawable.billiard4));
    addSport(Sport.create(95,"Bowling",1,0xFFF4511E,R.drawable.bowling3));
    addSport(Sport.create(96,"Fowling",5,0xFFF4511E,R.drawable.bowls7));
    addSport(Sport.create(65,"Snowboarding",2,0xFFF4511E,R.drawable.extreme1));
    addSport(Sport.create(49,"Fishing",1,0xFFF4511E,R.drawable.fishing21));
    addSport(Sport.create(14,"Diving",1,0xFFF4511E,R.drawable.rescue));
    addSport(Sport.create(63,"Sailing",2,0xFFF4511E,R.drawable.sail7));
    addSport(Sport.create(77,"Speedcubing",2,0xFFF4511E,R.drawable.scrabble21));
    addSport(Sport.create(25,"Rowing",2,0xFFF4511E,R.drawable.sea19));
    addSport(Sport.create(15,"Swimming",2,0xFFF4511E,R.drawable.silhouette66));
    addSport(Sport.create(66,"Ski Jumping",1,0xFFF4511E,R.drawable.skiing6));
    addSport(Sport.create(64,"Skiing",2,0xFFF4511E,R.drawable.sport22));
    addSport(Sport.create(18,"Kayaking",2,0xFFF4511E,R.drawable.sport26));
    addSport(Sport.create(16,"Synchronized Swimming",1,0xFFF4511E,R.drawable.swimming27));
    addSport(Sport.create(1,"Baseball",1,0xFFF48FB1,R.drawable.balls3));
    addSport(Sport.create(42,"Snooker",5,0xFFF48FB1,R.drawable.billiard2));
    addSport(Sport.create(2,"Kickball",1,0xFFF48FB1,R.drawable.football68));
    addSport(Sport.create(19,"BMX",1,0xFFF48FB1,R.drawable.cycling5));
    addSport(Sport.create(57,"Darts",5,0xFFF48FB1,R.drawable.dart13));
    addSport(Sport.create(30,"Weight Lifting",1,0xFFF48FB1,R.drawable.dumbbell7));
    addSport(Sport.create(23,"Fencing",1,0xFFF48FB1,R.drawable.fencing3));
    addSport(Sport.create(26,"Table Tennis",4,0xFFF48FB1,R.drawable.game65));
    addSport(Sport.create(50,"Ultimate",1,0xFFF48FB1,R.drawable.game69));
    addSport(Sport.create(51,"Disc Golf",3,0xFFF48FB1,R.drawable.game69));
    addSport(Sport.create(27,"Softball",1,0xFFF48FB1,R.drawable.sportsball8));
    addSport(Sport.create(20,"Volleyball",4,0xFFF48FB1,R.drawable.volleyball6));
    addSport(Sport.create(35,"Dodgeball",5,0xFFF48FB1,R.drawable.volleyball6));
    addSport(Sport.create(10,"Basketball",1,0xFFFF6D00,R.drawable.basketball32));
    addSport(Sport.create(86,"NBA 2K15",1,0xFFFF6D00,R.drawable.basketball32));
    addSport(Sport.create(38,"Karate",5,0xFFFF7043,R.drawable.fight1));
    addSport(Sport.create(40,"UFC",5,0xFFFF7043,R.drawable.martialarts));
    addSport(Sport.create(39,"Boxing",5,0xFFFF7043,R.drawable.boxing10));
    addSport(Sport.create(31,"Beer Pong",5,0xFFFF7043,R.drawable.drink24));
    addSport(Sport.create(32,"Flip Cup",5,0xFFFF7043,R.drawable.drink24));
    addSport(Sport.create(37,"Skeeball",1,0xFFFF7043,R.drawable.drink24));
    addSport(Sport.create(58,"Shuffleboard",1,0xFFFF7043,R.drawable.drink24));
    addSport(Sport.create(93,"Cornhole",1,0xFFFF7043,R.drawable.drink24));
    addSport(Sport.create(94,"Horseshoes",1,0xFFFF7043,R.drawable.drink24));

    for (Integer sportId : sports.keySet()) {
      sportList.add(sports.get(sportId));
    }

    Collections.sort(sportList, new Comparator<Sport>() {
      @Override
      public int compare(Sport lhs, Sport rhs) {
        return lhs.name().compareTo(rhs.name());
      }
    });
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