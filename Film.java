import java.util.ArrayList;

public class Film {
  private String original_title;
  private int movie_id;
  private float vote_average;
  private int vote_count;
  public ArrayList<Actor> actors = new ArrayList<Actor>();
  public ArrayList<Integer> genres = new ArrayList<Integer>();
  public ArrayList<Integer> keywords = new ArrayList<Integer>();
  private String director_name;
  private int director_id;

  Film(String original_title, int movie_id, float vote_average, int vote_count) {
    this.original_title = original_title;
    this.movie_id = movie_id;
    this.vote_average = vote_average;
    this.vote_count = vote_count;
  }

  public ArrayList<Actor> getActors() {
    return this.actors;
  }

  public ArrayList<Integer> getActorIDs() {
    ArrayList<Integer> actorIds = new ArrayList<>();
    for (Actor actor : actors) {
      actorIds.add(actor.getId());
    }
    return actorIds;
  }

  public ArrayList<Integer> getGenres() {
    return this.genres;
  }

  public ArrayList<Integer> getKeywords() {
    return this.keywords;
  }

  public String getOriginal_title() {
    return this.original_title;
  }

  public int getDirector() {
    return this.director_id;
  }

  public void setOriginal_title(String original_title) {
    this.original_title = original_title;
  }

  public int getMovie_id() {
    return this.movie_id;
  }

  public void setMovie_id(int movie_id) {
    this.movie_id = movie_id;
  }

  public void addActor(Actor actor) {
    actors.add(actor);
  }

  public void addGenre(Integer genre_id) {
    genres.add(genre_id);
  }

  public void addKeyword(Integer keyword_id) {
    keywords.add(keyword_id);
  }

  public void addDirector(String director_name, int director_id) {
    this.director_name = director_name;
    this.director_id = director_id;
  }

  public boolean hasActor(int id) {
    for (Actor actor : actors) {
      if (actor.hasId(id)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasGenre(int id) {
    for (Integer genre : genres) {
      if (genres.contains(genre)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasKeyword(int id) {
    for (Integer keyword : keywords) {
      if (keywords.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  public String stringifyGenres() {
    String response = "";
    for (Integer genre : genres) {
      response += Integer.toString(genre) + "\n";
    }
    return response;
  }

  public String stringifyActors() {
    String response = "";
    for (Actor actor : actors) {
      response += actor.getName() + "\n";
    }
    return response;
  }

  public String stringifyKeywords() {
    String response = "";
    for (Integer keyword : keywords) {
      response += Integer.toString(keyword) + "\n";
    }
    return response;
  }

  public void printAll() {
    System.out.println("//////////////////////////////////////");
    System.out.println("MOVIE: " + original_title);
    System.out.println("ID: " + movie_id + "\n");
    System.out.println("Vote average: " + vote_average);
    System.out.println("Vote count: " + vote_count + "\n");
    System.out.println("Director: " + director_name);
    System.out.println(stringifyActors());
    System.out.println(stringifyGenres());
    System.out.println(stringifyKeywords());
    System.out.println("//////////////////////////////////////");
  }

  @Override
  public String toString() {
    String response = "#############################\n" + "TITLE: " + original_title + "\n" + "movie_id: " + movie_id
        + "\n" + "vote_average: " + vote_average + "\n" + "vote_count: " + vote_count + "\n" + "actors: "
        + stringifyActors() + "\n" + "genres" + stringifyGenres() + "\n" + "keywords: " + stringifyKeywords() + "\n"
        + "director: " + director_name;
    return response;
  }

}