import pymysql.cursors
import pymysql
import json
import csv
from collections import defaultdict

connection = pymysql.connect(host='localhost',
                             user='root',
                             password='tearsforfears', charset="utf8")


def main():
    try:
        with connection.cursor() as cursor:
            print('Creating database...')

            cursor.execute('DROP DATABASE IF EXISTS films;')
            cursor.execute('CREATE DATABASE IF NOT EXISTS films;')
            cursor.execute('USE films;')

            print('Creating films table...')
            cursor.execute(
                'CREATE TABLE films(budget int, homepage varchar(255), movie_id int, original_language varchar(16), original_title varchar(128), overview varchar(4096), popularity float, release_date varchar(64), revenue bigint, runtime varchar(16), status varchar(64), tagline varchar(512), title varchar(128), vote_average float, vote_count int);')

            print('Creating genre table...')
            cursor.execute(
                'CREATE TABLE genre(movie_id int, genre_id int, name varchar(64));')

            print('Creating keywords table...')
            cursor.execute(
                'CREATE TABLE keywords(movie_id int, keyword_id int, name varchar(64));')

            print('Creating crew table...')
            cursor.execute(
                'CREATE TABLE crew(movie_id int, credit_id varchar(64), department varchar(128), gender varchar(16), id int, job varchar(128), name varchar(128));')

            print('Creating actors table...')
            cursor.execute(
                'CREATE TABLE actors(movie_id int, cast_id int, character_name varchar(512), credit_id varchar(64), gender varchar(16), id int, name varchar(128), order_num int);')

            print('Created database and table.')
            print('Inserting into table...')

            sqlFilms = "INSERT INTO films VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
            sqlKeywords = "INSERT INTO keywords VALUES(%s, %s, %s)"
            sqlGenre = "INSERT INTO genre VALUES(%s, %s, %s)"
            sqlCast = "INSERT INTO actors VALUES(%s, %s, %s, %s, %s, %s, %s, %s)"
            sqlCrew = "INSERT INTO crew VALUES(%s, %s, %s, %s, %s, %s, %s)"

            with open('./csv/tmdb_5000_movies.csv', 'r', encoding='utf-8') as csvfile:
                movies = csv.DictReader(csvfile)

                # Count keywords
                keys = defaultdict(int)
                for row in movies:
                    keywords = json.loads(row['keywords'])
                    for i in keywords:
                        keys[i['id']] += 1
                keywordCount = {k: v for k, v in keys.items() if (v >= 5)}

                # Reset cursor
                csvfile.seek(0)
                movies = csv.DictReader(csvfile)

                for row in movies:

                    budget = int(row['budget'])
                    homepage = row['homepage']
                    movie_id = int(row['id'])
                    original_language = row['original_language']
                    original_title = row['original_title']
                    overview = row['overview']
                    popularity = float(row['popularity'])
                    release_date = row['release_date']
                    revenue = int(row['revenue'])
                    runtime = row['runtime']
                    status = row['status']
                    tagline = row['tagline']
                    title = row['title']
                    vote_average = float(row['vote_average'])
                    vote_count = int(row['vote_count'])

                    cursor.execute(
                        sqlFilms, (budget, homepage, movie_id, original_language, original_title, overview, popularity, release_date, revenue, runtime, status, tagline, title, vote_average, vote_count))

                    genres = json.loads(row['genres'])
                    keywords = json.loads(row['keywords'])

                    for genre in genres:
                        genreID = int(genre['id'])
                        genreName = (genre['name'])
                        cursor.execute(
                            sqlGenre, (movie_id, genreID, genreName))

                    for keyword in keywords:
                        keywordID = int(keyword['id'])
                        keywordName = keyword['name']
                        if keywordID in keywordCount:
                            cursor.execute(
                                sqlKeywords, (movie_id, keywordID, keywordName))

                else:
                    print('Completed insert into table.')

            print('Inserting cast and crew...')
            with open('./csv/tmdb_5000_credits.csv', 'r', encoding='utf-8') as csvfile:
                credits = csv.DictReader(csvfile)
                for row in credits:

                    title = row['title']
                    movie_id = int(row['movie_id'])
                    cast = json.loads(row['cast'])
                    crew = json.loads(row['crew'])

                    for cast_member in cast:
                        cast_id = int(cast_member['cast_id'])
                        character = cast_member['character']
                        credit_id = cast_member['credit_id']
                        gender = cast_member['gender']
                        id = int(cast_member['id'])
                        name = cast_member['name']
                        order = int(cast_member['order'])
                        cursor.execute(
                            sqlCast, (movie_id, cast_id, character,
                                      credit_id, gender, id, name, order))

                    for crew_member in crew:
                        credit_id = crew_member['credit_id']
                        department = crew_member['department']
                        gender = crew_member['gender']
                        id = int(crew_member['id'])
                        job = crew_member['job']
                        name = crew_member['name']

                        cursor.execute(
                            sqlCrew, (movie_id, credit_id, department, gender, id, job, name))

                else:
                    print('Completed insert into table.')

        connection.commit()
    finally:
        connection.close()

    # Check how file is accessed
if __name__ == '__main__':
    main()
