import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toList;

public class Recommender {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	private static HashMap<Integer, Film> filmMap = new HashMap<>();
	private static HashMap<Integer, Integer[]> actorMap = new HashMap<>();
	private static float averageTotal;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter user: ");
		String USER = scanner.nextLine();

		System.out.println("Enter password: ");
		String PASS = scanner.nextLine();

		System.out.println(
				"Skip database creation and data insertion?(Y/N) (only choose [Y] if database has already been filled.) ");
		char skip = scanner.nextLine().toUpperCase().charAt(0);

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			//Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected.");

			if (skip == 'N') {
				// Create Database
				System.out.println("Creating database...");
				stmt = conn.createStatement();

				stmt.executeUpdate("DROP DATABASE IF EXISTS films");
				stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS films");
				stmt.executeUpdate("USE films");

				// Create Tables
				System.out.println("Creating table films...");
				stmt.executeUpdate(
						"CREATE TABLE films(budget int, homepage varchar(255), movie_id int, original_language varchar(16), original_title varchar(128), overview varchar(4096), popularity float, release_date varchar(64), revenue bigint, runtime varchar(16), status varchar(64), tagline varchar(512), title varchar(128), vote_average float, vote_count int);");

				System.out.println("Creating table genre...");
				stmt.executeUpdate("CREATE TABLE genre(movie_id int, genre_id int, name varchar(64));");

				System.out.println("Creating table keywords...");
				stmt.executeUpdate("CREATE TABLE keywords(movie_id int, keyword_id int, name varchar(64));");

				System.out.println("Creating table crew...");
				stmt.executeUpdate(
						"CREATE TABLE crew(movie_id int, credit_id varchar(64), department varchar(128), gender varchar(16), id int, job varchar(128), name varchar(128));");

				System.out.println("Creating table actors...");
				stmt.executeUpdate(
						"CREATE TABLE actors(movie_id int, cast_id int, character_name varchar(512), credit_id varchar(64), gender varchar(16), id int, name varchar(128), order_num int);");

				// Insert Data
				System.out.println("Database and tables created.");
				System.out.println("Inserting films...");
				PreparedStatement statement = conn
						.prepareStatement("INSERT INTO films VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				Reader in = new FileReader("./parsed/films.csv");
				Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
				for (CSVRecord record : records) {
					int budget = Integer.parseInt(record.get("budget"));
					String homepage = record.get("homepage");
					int movie_id = Integer.parseInt(record.get("movie_id"));
					String original_language = record.get("original_language");
					String original_title = record.get("original_title");
					String overview = record.get("overview");
					float popularity = Float.parseFloat(record.get("popularity"));
					String release_date = record.get("release_date");
					long revenue = Long.parseLong(record.get("revenue"));
					String runtime = record.get("runtime");
					String status = record.get("status");
					String tagline = record.get("tagline");
					String title = record.get("title");
					float vote_average = Float.parseFloat(record.get("vote_average"));
					float vote_count = Integer.parseInt(record.get("vote_count"));

					statement.setInt(1, budget);
					statement.setString(2, homepage);
					statement.setInt(3, movie_id);
					statement.setString(4, original_language);
					statement.setString(5, original_title);
					statement.setString(6, overview);
					statement.setFloat(7, popularity);
					statement.setString(8, release_date);
					statement.setLong(9, revenue);
					statement.setString(10, runtime);
					statement.setString(11, status);
					statement.setString(12, tagline);
					statement.setString(13, title);
					statement.setFloat(14, vote_average);
					statement.setFloat(15, vote_count);
					statement.addBatch();

				}
				statement.executeBatch();

				System.out.println("Inserting genres...");
				statement = conn.prepareStatement("INSERT INTO genre VALUES(?, ?, ?)");
				in = new FileReader("./parsed/genre.csv");
				records = CSVFormat.EXCEL.withHeader().parse(in);
				for (CSVRecord record : records) {
					int movie_id = Integer.parseInt(record.get("movie_id"));
					int genre_id = Integer.parseInt(record.get("genre_id"));
					String name = record.get("name");

					statement.setInt(1, movie_id);
					statement.setInt(2, genre_id);
					statement.setString(3, name);
					statement.addBatch();

				}
				statement.executeBatch();

				System.out.println("Inserting keywords...");
				statement = conn.prepareStatement("INSERT INTO keywords VALUES(?, ?, ?)");
				in = new FileReader("./parsed/keywords.csv");
				records = CSVFormat.EXCEL.withHeader().parse(in);
				for (CSVRecord record : records) {
					int movie_id = Integer.parseInt(record.get("movie_id"));
					int keyword_id = Integer.parseInt(record.get("keyword_id"));
					String name = record.get("name");

					statement.setInt(1, movie_id);
					statement.setInt(2, keyword_id);
					statement.setString(3, name);
					statement.addBatch();

				}
				statement.executeBatch();

				System.out.println("Inserting crew...");
				statement = conn.prepareStatement("INSERT INTO crew VALUES(?, ?, ?, ?, ?, ?, ?)");
				in = new FileReader("./parsed/crew.csv");
				records = CSVFormat.EXCEL.withHeader().parse(in);
				for (CSVRecord record : records) {
					int movie_id = Integer.parseInt(record.get("movie_id"));
					String credit_id = record.get("credit_id");
					String department = record.get("department");
					String gender = record.get("gender");
					int id = Integer.parseInt(record.get("id"));
					String job = record.get("job");
					String name = record.get("name");

					statement.setInt(1, movie_id);
					statement.setString(2, credit_id);
					statement.setString(3, department);
					statement.setString(4, gender);
					statement.setInt(5, id);
					statement.setString(6, job);
					statement.setString(7, name);

					statement.addBatch();

				}
				statement.executeBatch();

				System.out.println("Inserting actors...");
				statement = conn.prepareStatement("INSERT INTO actors VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
				in = new FileReader("./parsed/actors.csv");
				records = CSVFormat.EXCEL.withHeader().parse(in);
				for (CSVRecord record : records) {
					int movie_id = Integer.parseInt(record.get("movie_id"));
					int cast_id = Integer.parseInt(record.get("cast_id"));
					String character_name = record.get("character_name");
					String credit_id = record.get("credit_id");
					String gender = record.get("gender");
					int id = Integer.parseInt(record.get("id"));
					String name = record.get("name");
					int order_num = Integer.parseInt(record.get("order_num"));

					statement.setInt(1, movie_id);
					statement.setInt(2, cast_id);
					statement.setString(3, character_name);
					statement.setString(4, credit_id);
					statement.setString(5, gender);
					statement.setInt(6, id);
					statement.setString(7, name);
					statement.setInt(8, order_num);

					statement.addBatch();

				}
				statement.executeBatch();
				System.out.println("Data inserted.");
			}

			stmt = conn.createStatement();
			stmt.executeUpdate("USE films");

			// CachedRowSet filmSetCached = new CachedRowSetImpl();
			// filmSetCached.populate(filmSet);
			// System.out.println(filmSetCached);

			// 
			// CachedRowSet actorSetCached = new CachedRowSetImpl();
			// filmSetCached.populate(actorSet);

			// 
			// CachedRowSet crewSetCached = new CachedRowSetImpl();
			// filmSetCached.populate(crewSet);

			// ResultSet keywordSet = stmt.executeQuery("select * from keywords");
			// CachedRowSet keywordSetCached = new CachedRowSetImpl();
			// filmSetCached.populate(keywordSet);

			// ResultSet genreSet = stmt.executeQuery("select * from genre");
			// CachedRowSet genreSetached = new CachedRowSetImpl();
			// filmSetCached.populate(genreSet);

			// Add all films into memory
			ResultSet filmSet = stmt.executeQuery("select * from films");
			System.out.println("Adding films to memory...");
			while (filmSet.next()) {
				String original_title = filmSet.getString("original_title");
				int movie_id = filmSet.getInt("movie_id");
				float vote_average = filmSet.getFloat("vote_average");
				int vote_count = filmSet.getInt("vote_count");
				Film myFilm = new Film(original_title, movie_id, vote_average, vote_count);
				filmMap.put(movie_id, myFilm);
			}

			// Add actors into memory
			ResultSet actorSet = stmt.executeQuery("select * from actors");
			while (actorSet.next()) {
				int cast_id = actorSet.getInt("cast_id");
				String character_name = actorSet.getString("character_name");
				String name = actorSet.getString("name");
				int id = actorSet.getInt("id");
				int movie_id = actorSet.getInt("movie_id");
				Actor actor = new Actor(cast_id, character_name, id, name);

				if (filmMap.containsKey(movie_id)) {
					filmMap.get(movie_id).addActor(actor);
					;
				}
			}

			ResultSet crewSet = stmt.executeQuery("select job, movie_id, id, name from crew where job='Director'");
			while (crewSet.next()) {
				int movie_id = crewSet.getInt("movie_id");
				int id = crewSet.getInt("id");
				String name = crewSet.getString("name");

				if (filmMap.containsKey(movie_id)) {
					filmMap.get(movie_id).addDirector(name, id);
				}
			}

			ResultSet genreSet = stmt.executeQuery("select * from genre");
			while (genreSet.next()) {
				int movie_id = genreSet.getInt("movie_id");
				int genre_id = genreSet.getInt("genre_id");

				if (filmMap.containsKey(movie_id)) {
					filmMap.get(movie_id).addGenre(genre_id);
				}
			}

			ResultSet keywordSet = stmt.executeQuery("select * from keywords");
			while (keywordSet.next()) {
				int movie_id = keywordSet.getInt("movie_id");
				int keyword_id = keywordSet.getInt("keyword_id");

				if (filmMap.containsKey(movie_id)) {
					filmMap.get(movie_id).addKeyword(keyword_id);
				}
			}

			System.out.println("Finished.\n");

			ResultSet rec;
			// Get input for film recommendation
			while (true) {
				System.out.println("Enter a movie title...");
				String movie = scanner.nextLine().trim();
				rec = stmt.executeQuery("select movie_id from films where original_title = " + "\"" + movie + "\"");
				if (rec.next()) {
					int movie_id = rec.getInt("movie_id");

					if (filmMap.containsKey(movie_id)) {
						// System.out.println("Found film in memory!");
						Film userFilm = filmMap.get(movie_id);
						getReccomendations(userFilm, filmMap);

					} else {
						System.out.println("Did not find film in memory!");
					}
				} else {
					System.out.println("Did not find film in memory!");
				}
			}

			// while (rs.next()) {
			// 	String original_title = rs.getString("original_title");
			// 	int movie_id = rs.getInt("movie_id");
			// 	Film myFilm = new Film(original_title, movie_id);

			// 	filmMap.put(movie_id, myFilm);
			// 	for (Map.Entry<Integer, Film> e : filmMap.entrySet()) {
			// 		System.out.println("Key " + e.getKey());
			// 		System.out.println("Value " + e.getValue().toString());
			// 	}
			// }

		} catch (

		SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} //end finally try
		} //end try 
	}

	// Compare director, genres, keywords, actors for each film and keep tally in hashmap (instead of 2d array).
	public static void getReccomendations(Film film, HashMap<Integer, Film> filmMap) {
		HashMap<Film, Integer> counter = new HashMap<>();
		int director_id = film.getDirector();
		ArrayList<Integer> myActors = film.getActorIDs();
		ArrayList<Integer> myGenres = film.getGenres();
		ArrayList<Integer> myKeywords = film.getKeywords();
		for (Map.Entry<Integer, Film> e : filmMap.entrySet()) {
			int score = 0;
			Film filmComparingTo = e.getValue();

			// Compute directors score
			if (filmComparingTo.getDirector() == director_id) {
				score++;
			}

			// Compute actors score
			for (Actor actor : filmComparingTo.getActors()) {
				if (myActors.contains(actor.getId())) {
					score++;
				}
			}

			// Compute genres score
			for (Integer genre_id : filmComparingTo.getGenres()) {
				if (myGenres.contains(genre_id)) {
					score++;
				}
			}

			// Compute keywords score
			for (Integer keyword_id : filmComparingTo.getKeywords()) {
				if (myKeywords.contains(keyword_id)) {
					score++;
				}
			}
			counter.put(e.getValue(), score);
			counter.remove(film);
		}

		System.out.println("");
		System.out.println("Here are the recommendations...");
		counter.entrySet().stream().sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).limit(10)
				.forEach((k) -> System.out.println(k.getKey().getOriginal_title()));
		System.out.println("");

	}
}