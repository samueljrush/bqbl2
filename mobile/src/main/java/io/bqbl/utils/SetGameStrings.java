package io.bqbl.utils;

public class SetGameStrings {

  public static String generateSomeString(String first, String last) {
    final String[] winnerStrings = {
        String.format("The name is %s. %s %s.", last, first, last),
        String.format("If you want to crown %s, then crown zer ass", first, last.substring(0, 1)),
        String.format("All %s %s does is win", first, last.substring(0,1)),
        String.format("%s! %s! %s!", first, first, first),
        String.format("%s > God", first),
        String.format("You can't spell MVP without %s", first),
        String.format("Putting the %s in champion", first),
        String.format("Does %s ever tire of winning?", first),
        String.format("First comes %s. Then comes...everyone else", first),
        String.format("Heroic effort by %s there", first),
        String.format("Classic %s", first),
        String.format("%s kept it classy as always", first),
        String.format("Bow down to %s", first),
        String.format("Look at all %s's bitches", first),
        String.format("Too easy. Too easy %s says", first)};
    int index = (int) (Math.random() * winnerStrings.length);
    return winnerStrings[index];
  }


  public static String generateWinnerString(String first, String last) {
    final String[] winnerStrings = {
        String.format("The name is %s. %s %s.", last, first, last),
        String.format("If you want to crown %s, then crown zer ass", first, last.substring(0, 1)),
        String.format("All %s %s does is win", first, last.substring(0, 1)),
        "Fuck ya",
        String.format("%s! %s! %s!", first, first, first),
        String.format("%s > God", first),
        String.format("You can't spell MVP without %s", first),
        String.format("Putting the %s in champion", first),
        String.format("Does %s ever tire of winning?", first),
        String.format("First comes %s. Then comes...everyone else", first),
        String.format("Heroic effort by %s there", first),
        "GOAT",
        String.format("Classic %s", first),
        String.format("%s kept it classy as always", first),
        String.format("Bow down to %s", first),
        String.format("Look at all %s's bitches", first),
        String.format("Too easy. Too easy %s says", first),
        "Winner. Winner. Chicken dinner"};
    int index = (int) (Math.random() * winnerStrings.length);
    return winnerStrings[index];
  }

  public static String generateLoserString(String first, String last) {
    final String[] loserStrings = {
        ":(",
        String.format("%s sucks", first),
        String.format("Was %s even trying?", first),
        "Better luck next time.",
        "Do it the same, but, uh, better.",
        String.format("Can %s be any worse?", first),
        String.format("%s shouldn't quit zer day job", first),
        "It must be hard to suck so much",
        String.format("%s is on his knees", first),
        String.format("%s took it. Ze took it good", first),
        String.format("No surprise here. %s lost. Again.", first),
        "Putting the blow in blowhard.",
        String.format("%s bit the dust.", first),
        "Well that was an unmitigated disaster",
        String.format("%s give up. Give up now", first),
        "Ouch. That had to hurt",
        "Anirwin."};
    int index =(int) (Math.random() * loserStrings.length);
    return loserStrings[index];
  }
}