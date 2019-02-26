import java.util.ArrayList;

public class Actor {
  private int cast_id;
  private String character_name;
  private String name;

  private int id;

  Actor(int cast_id, String character_name, int id, String name) {
    this.cast_id = cast_id;
    this.character_name = character_name;
    this.id = id;
    this.name = name;
  }

  public int getCast_id() {
    return this.cast_id;
  }

  public void setCast_id(int cast_id) {
    this.cast_id = cast_id;
  }

  public String getCharacter_name() {
    return this.character_name;
  }

  public void setCharacter_name(String character_name) {
    this.character_name = character_name;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean hasId(int id) {
    if (cast_id == id) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "id \t|\t" + "cast_id \t\n" + "character_name \t\n" + id + "\t\t\t" + cast_id + "\t\t\t" + character_name;
  }

}