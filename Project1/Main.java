import java.sql.*;

public class Main {

    public static void printTable(ResultSet rs) //table 출력하는 printTable 함수
    {
        try
        {
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 0; i < rsmd.getColumnCount(); i++)
            {
                System.out.print(String.format("%30s", rsmd.getColumnName(i + 1))); //table 이름들 출력
            }
            System.out.println();

            while (rs.next())
            {
                for (int i = 0; i < rsmd.getColumnCount(); i++)
                {
                    System.out.print(String.format("%30s", rs.getString(i + 1))); //테이블 내용 출력
                }
                System.out.println();
            }

        }
        catch (SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION" + sqlx);
        }

        System.out.println("\n");
    }

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return;
        } /// 이 부분에서 에러가 난다면, jdbc driver(.jar file)를 제대로 ide에 추가하도록 하세요

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/project_movie", "postgres", "cse3207");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }/// 이 부분에서 에러가 난다면, connection 변수에 비밀번호, 데이터베이스 명, user 명 등을 확인주세요
        /// 이후의 문제는 콘솔에 출력된 에러를 구글에 검색해보세요…

        if (connection != null) {
            System.out.println(connection);
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        ////////// 여기에 코드 작성 //////////
        Statement stmt = connection.createStatement();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;

        try{ //QUERY #1
            // CREATE TABLE
            System.out.println("==============================================");
            System.out.println("Creating tables...");

            stmt.executeUpdate("create table director" + //creating director table
                    "(" +
                    "directorID varchar(5) primary key," + //directorID as PK
                    "directorName varchar(30)," +
                    "dateOfBirth varchar(30)," +
                    "dateOfDeath varchar(30)" +
                    ")");
            System.out.println("Table [director] is created.");

            stmt.executeUpdate("create table actor" + //creating actor table
                    "(" +
                    "actorID varchar(5) primary key," + //actorID as PK
                    "actorName varchar(30)," +
                    "dateOfBirth varchar(30)," +
                    "dateOfDeath varchar(30)," +
                    "gender varchar(6)" +
                    ")");
            System.out.println("Table [actor] is created.");

            stmt.executeUpdate("create table movie" + //creating movie table
                    "(" +
                    "movieID varchar(5) primary key," + //movieID as PK
                    "movieName varchar(30)," +
                    "releaseYear varchar(4)," +
                    "releaseMonth varchar(2)," +
                    "releaseDate varchar(2)," +
                    "publisherName varchar(30)," +
                    "avgRate decimal(3,2)" +
                    ")");
            System.out.println("Table [movie] is created.");

            stmt.executeUpdate("create table award" + //creating award table
                    "(" +
                    "awardID varchar(5) primary key," + //awardID as PK
                    "awardName varchar(30)" +
                    ")");
            System.out.println("Table [award] is created.");

            stmt.executeUpdate("create table genre" + //creating genre table
                    "(" +
                    "genreName varchar(30) primary key" + //genreName as PK
                    ")");
            System.out.println("Table [genre] is created.");

            stmt.executeUpdate("create table customer" + //creating customer table
                    "(" +
                    "customerID varchar(30) primary key," + //customerID as PK
                    "customerName varchar(30)," +
                    "dateOfBirth varchar(30)," +
                    "gender varchar(6)" +
                    ")");
            System.out.println("Table [customer] is created.");

            stmt.executeUpdate("create table movieGenre" + //creating movieGenre table
                    "(" +
                    "movieID varchar(5), " +
                    "genreName varchar(30), " +
                    "constraint movieGenre_PK primary key(movieID, genreName), " + //movieID, genreName as PKs
                    "foreign key(movieID) references movie(movieID) on delete cascade," + //movieID as FK
                    "foreign key(genreName) references genre(genreName) on delete cascade" + //genreName as FK
                    ")");
            System.out.println("Table [movieGenre] is created.");

            stmt.executeUpdate("create table movieObtain" + //creating movieObtain table
                    "(" +
                    "movieID varchar(5), " +
                    "awardID varchar(5), " +
                    "year varchar(4), " +
                    "constraint movieObtain_PK  primary key(movieID, awardID), " + //movieID, awardID as PKs
                    "foreign key(movieID) references movie(movieID) on delete cascade, " + //movieID as FK
                    "foreign key(awardID) references award(awardID) on delete cascade " + //awardID as FK
                    ")");
            System.out.println("Table [movieObtain] is created.");

            stmt.executeUpdate("create table actorObtain" + //creating actorObtain table
                    "(" +
                    "actorID varchar(5), " +
                    "awardID varchar(5), " +
                    "year varchar(4), " +
                    "constraint actorObtain_PK primary key(actorID, awardID), " + //actorID, awardID as PKs
                    "foreign key(actorID) references actor(actorID) on delete cascade, " + //actorID as FK
                    "foreign key(awardID) references award(awardID) on delete cascade "  + //awardID as FK
                    ")");
            System.out.println("Table [actorObtain] is created.");

            stmt.executeUpdate("create table directorObtain" + //creating directorObtain table
                    "(" +
                    "directorID varchar(5), " +
                    "awardID varchar(5), " +
                    "year varchar(4), " +
                    "constraint directorObtain_PK primary key(directorID, awardID), " + //directorID, awardID as PKs
                    "foreign key(directorID) references director(directorID) on delete cascade, " + //directorID as FK
                    "foreign key(awardID) references award(awardID) on delete cascade" + //awardID as FK
                    ")");
            System.out.println("Table [directorObtain] is created.");

            stmt.executeUpdate("create table casting" + //creating casting table
                    "(" +
                    "movieID varchar(5), " +
                    "actorID varchar(5), " +
                    "role varchar(30)," +
                    "constraint casting_PK primary key(movieID, actorID), " +
                    "foreign key(movieID) references movie(movieID) on delete cascade, " +
                    "foreign key(actorID) references actor(actorID) on delete cascade " +
                    ")");
            System.out.println("Table [casting] is created.");

            stmt.executeUpdate("create table make" + //creating make table
                    "(" +
                    "movieID varchar(5), " +
                    "directorID varchar(5), " +
                    "constraint make_PK primary key(movieID, directorID), " +
                    "foreign key(movieID) references movie(movieID) on delete cascade, " +
                    "foreign key(directorID) references director(directorID) on delete cascade " +
                    ")");
            System.out.println("Table [make] is created.");

            stmt.executeUpdate("create table customerRate" + //creating customerRate table
                    "(" +
                    "customerID varchar(5), " +
                    "movieID varchar(5), " +
                    "rate decimal(3,2), " +
                    "constraint customerRate_PK primary key(customerID, movieID), " +
                    "foreign key(customerID) references customer(customerID) on delete cascade, " +
                    "foreign key(movieID) references movie(movieID) on delete cascade " +
                    ")");
            System.out.println("Table [customerRate] is created.");
            System.out.println("All tables created.");
            System.out.println("----------------------------------------------");

            //INSERT INTO DIRECTOR
            System.out.println("Initializing tables...");
            stmt.executeUpdate("insert into director values('001', 'Tim Burton', '1958.08.25', null);");
            stmt.executeUpdate("insert into director values('002', 'David Fincher', '1962.08.28', null);");
            stmt.executeUpdate("insert into director values('003', 'Christopher Nolan', '1970.07.30', null);");
            System.out.println("Table [director] is initialized.");
            //INSERT INTO ACTOR
            stmt.executeUpdate("insert into actor values('101', 'Johnny Depp', '1963.06.09', null, 'Male')");
            stmt.executeUpdate("insert into actor values('102', 'Winona Ryder', '1971.10.29', null, 'Female')");
            stmt.executeUpdate("insert into actor values('103', 'Mia Wasikowska', '1989.10.14', null, 'Female')");
            stmt.executeUpdate("insert into actor values('104', 'Christian Bale', '1974.01.30', null, 'Male')");
            stmt.executeUpdate("insert into actor values('105', 'Heath Ledger', '1979.04.04', '2008.1.22', 'Male')");
            stmt.executeUpdate("insert into actor values('106', 'Jesse Eisenberg', '1983.10.05', null, 'Male')");
            stmt.executeUpdate("insert into actor values('107', 'Justin Timberlake', '1981.01.31', null, 'Male')");
            stmt.executeUpdate("insert into actor values('108', 'Fionn Whitehead', '1997.07.18', null, 'Male')");
            stmt.executeUpdate("insert into actor values('109', 'Tom Hardy', '1977.09.15', null, 'Male')");
            System.out.println("Table [actor] is initialized.");
            //INSERT INTO CUSTOMER
            stmt.executeUpdate("insert into customer values('201', 'Ethan', '1997.11.14', 'Male')");
            stmt.executeUpdate("insert into customer values('202', 'John', '1978.01.23', 'Male')");
            stmt.executeUpdate("insert into customer values('203', 'Hayden', '1980.05.04', 'Female')");
            stmt.executeUpdate("insert into customer values('204', 'Jill', '1981.04.17', 'Female')");
            stmt.executeUpdate("insert into customer values('205', 'Bell', '1990.05.14', 'Male')");
            System.out.println("Table [customer] is initialized.");
            //INSERT INTO MOVIE
            stmt.executeUpdate("insert into movie values('301', 'Edward Scissorhands', '1991', '06', '29', '20th Century Fox Presents', null);");
            stmt.executeUpdate("insert into movie values('302', 'Alice In Wonderland', '2010', '03', '04', 'Korea Sony Pictures', null);");
            stmt.executeUpdate("insert into movie values('303', 'The Social Network', '2010', '11', '18', 'Korea Sony Pictures', null);");
            stmt.executeUpdate("insert into movie values('304', 'The Dark Knight', '2008', '08', '06', 'Warner Brothers Korea', null);");
            stmt.executeUpdate("insert into movie values('305', 'Dunkirk', '2017', '07', '13', 'Warner Brothers Korea', null);");
            System.out.println("Table [movie] is initialized.");
            //INSERT INTO GENRE
            stmt.executeUpdate("insert into genre values('Fantasy')");
            stmt.executeUpdate("insert into genre values('Romance')");
            stmt.executeUpdate("insert into genre values('Adventure')");
            stmt.executeUpdate("insert into genre values('Family')");
            stmt.executeUpdate("insert into genre values('Drama')");
            stmt.executeUpdate("insert into genre values('Action')");
            stmt.executeUpdate("insert into genre values('Mystery')");
            stmt.executeUpdate("insert into genre values('Thriller')");
            stmt.executeUpdate("insert into genre values('War')");
            System.out.println("Table [genre] is initialized.");
            //INSERT INTO MOVIEGENRE
            stmt.executeUpdate("insert into movieGenre values('301', 'Fantasy')");
            stmt.executeUpdate("insert into movieGenre values('301', 'Romance')");
            stmt.executeUpdate("insert into movieGenre values('302', 'Fantasy')");
            stmt.executeUpdate("insert into movieGenre values('302', 'Adventure')");
            stmt.executeUpdate("insert into movieGenre values('302', 'Family')");
            stmt.executeUpdate("insert into movieGenre values('303', 'Drama')");
            stmt.executeUpdate("insert into movieGenre values('304', 'Action')");
            stmt.executeUpdate("insert into movieGenre values('304', 'Drama')");
            stmt.executeUpdate("insert into movieGenre values('304', 'Mystery')");
            stmt.executeUpdate("insert into movieGenre values('304', 'Thriller')");
            stmt.executeUpdate("insert into movieGenre values('305', 'Action')");
            stmt.executeUpdate("insert into movieGenre values('305', 'Drama')");
            stmt.executeUpdate("insert into movieGenre values('305', 'Thriller')");
            stmt.executeUpdate("insert into movieGenre values('305', 'War')");
            System.out.println("Table [movieGenre] is initialized.");
            //INSERT INTO CASTING
            stmt.executeUpdate("insert into casting values('301', '101', 'Main actor')");
            stmt.executeUpdate("insert into casting values('301', '102', 'Main actor')");
            stmt.executeUpdate("insert into casting values('302', '101', 'Main actor')");
            stmt.executeUpdate("insert into casting values('302', '103', 'Main actor')");
            stmt.executeUpdate("insert into casting values('303', '106', 'Main actor')");
            stmt.executeUpdate("insert into casting values('303', '107', 'Supporting actor')");
            stmt.executeUpdate("insert into casting values('304', '104', 'Main actor')");
            stmt.executeUpdate("insert into casting values('304', '105', 'Main actor')");
            stmt.executeUpdate("insert into casting values('305', '108', 'Main actor')");
            stmt.executeUpdate("insert into casting values('305', '109', 'Supporting actor')");
            System.out.println("Table [casting] is initialized.");
            //INSERT INTO MAKE
            stmt.executeUpdate("insert into make values('301', '001')");
            stmt.executeUpdate("insert into make values('302', '001')");
            stmt.executeUpdate("insert into make values('303', '002')");
            stmt.executeUpdate("insert into make values('304', '003')");
            stmt.executeUpdate("insert into make values('305', '003')");

            System.out.println("Table [make] is initialized.");
            System.out.println("All tables initialized.");
            System.out.println("----------------------------------------------");
            System.out.println("Showing all created tables...\n");
            rs = stmt.executeQuery("select schemaname, tablename from PG_TABLES where schemaname = 'public';");
            printTable(rs);

            System.out.println("QUERY #1 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #2
        {
            System.out.println("==============================================");
            System.out.println("Inserting Data...\n");

            System.out.println("Statement(#1): Winona Ryder won the “Best supporting actor” award in 1994"); //BSA = 401
            stmt.executeUpdate("insert into award values('401', 'Best supporting actor')");
            stmt.executeUpdate("insert into actorObtain values('102', '401', '1994')");
            System.out.println("Translated SQL: insert into award values('401', 'Best supporting actor')" +
                    " \n                insert into actorObtain values('102', '401', '1994')\n");

            System.out.println("Statement(#2): Tom Hardy won the “Best supporting actor” award in 2018"); //BSA = 401중복
            stmt.executeUpdate("insert into actorObtain values('109', '401', '2018')");
            System.out.println("Translated SQL: insert into actorObtain values('109', '401', '2018')\n");

            System.out.println("Statement(#3): Heath Ledger won the “Best villain actor” award in 2009"); //BVA = 402
            stmt.executeUpdate("insert into award values('402', 'Best villain actor')");
            stmt.executeUpdate("insert into actorObtain values('105', '402', '2009')");
            System.out.println("Translated SQL: insert into award values('402', 'Best villain actor')" +
                    " \n                insert into actorObtain values('105', '402', '2009')\n");

            System.out.println("Statement(#4): Johnny Depp won the “Best main actor” award in 2011"); //BMA =403
            stmt.executeUpdate("insert into award values('403', 'Best main actor')");
            stmt.executeUpdate("insert into actorObtain values('101', '403', '2011')");
            System.out.println("Translated SQL: insert into award values('403', 'Best main actor') \n" +
                    "                insert into actorObtain values('101', '403', '2011')\n");

            System.out.println("Statement(#5): Edward Scissorhands won the “Best fantasy movie” award in 1991"); //BFM = 404
            stmt.executeUpdate("insert into award values('404', 'Best fantasy movie')");
            stmt.executeUpdate("insert into movieObtain values('301', '404', '1991')");
            System.out.println("Translated SQL: insert into award values('404', 'Best fantasy movie')" +
                    " \n                insert into movieObtain values('301', '404', '1991')\n");

            System.out.println("Statement(#6): Alice In Wonderland won the “Best fantasy movie” award in 2011"); //BFM = 404중복
            stmt.executeUpdate("insert into movieObtain values('302', '404', '2011')");
            System.out.println("Translated SQL: insert into movieObtain values('302', '404', '2011')\n");

            System.out.println("Statement(#7): The Dark Knight won the “Best picture” award in 2009"); //BP = 405
            stmt.executeUpdate("insert into award values('405', 'Best picture')");
            stmt.executeUpdate("insert into movieObtain values('304', '405', '2009')");
            System.out.println("Translated SQL: insert into award values('405', 'Best picture')" +
                    " \n                insert into movieObtain values('304', '405', '2009')\n");

            System.out.println("Statement(#8): Christopher Nolan won the “Best director” award in 2018"); //BD = 406
            stmt.executeUpdate("insert into award values('406', 'Best director')");
            stmt.executeUpdate("insert into directorObtain values('003', '406', '2018')");
            System.out.println("Translated SQL: insert into award values('406', 'Best director')" +
                    " \n                insert into directorObtain values('003', '406', '2018')\n");

            System.out.println("All data inserted.");
            System.out.println("----------------------------------------------");

            System.out.println("Table [award]");
            rs = stmt.executeQuery("select * from award order by awardID;");
            printTable(rs);

            System.out.println("Table [actorObtain]");
            rs = stmt.executeQuery("select * from actorObtain order by actorID;");
            printTable(rs);

            System.out.println("Table [movieObtain]");
            rs = stmt.executeQuery("select * from movieObtain order by movieID;");
            printTable(rs);

            System.out.println("Table [directorObtain]");
            rs = stmt.executeQuery("select * from directorObtain order by directorID;");
            printTable(rs);

            System.out.println("QUERY #2 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #3
        {
            System.out.println("==============================================");

            System.out.println("Statement(#1): Ethan rates 5 to “Dunkirk”");
            stmt.executeUpdate("insert into customerRate values('201', '305', 5)");
            stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)");
            System.out.println("Translated SQL: insert into customerRate values('201', '305', 5)" +
                    " \n                update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)\n");

            System.out.println("Statement(#2): Bell rates 5 to the movies whose director is “Tim Burton”");
            stmt.executeUpdate("insert into customerRate select '205', movieID, 5 from make natural join director where directorID='001'");
            stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)");
            System.out.println("Translated SQL: insert into customerRate select '205', movieID, 5 from make natural join director where directorID='001'" +
                    " \n                update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)\n");

            System.out.println("Statement(#3): Jill rates 4 to the movies whose main actor is female");
            stmt.executeUpdate("insert into customerRate select '204', movieID, 4 from casting natural join actor where gender='Female'");
            stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)");
            System.out.println("Translated SQL: insert into customerRate select '204', movieID, 4 from casting natural join actor where gender='Female'" +
                    " \n                update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)\n");

            System.out.println("Statement(#4): Hayden rates 4 to the fantasy movies");
            stmt.executeUpdate("insert into customerRate select '203', movieID, 4 from movieGenre natural join genre where genreName='Fantasy'");
            stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)");
            System.out.println("Translated SQL: insert into customerRate select '203', movieID, 4 from movieGenre natural join genre where genreName='Fantasy'" +
                    " \n                update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)\n");

            System.out.println("Statement(#5): John rates 5 to the movies whose director won the “Best director” award"); //error
            stmt.executeUpdate("insert into customerRate select '202', movieID, 5 from directorObtain natural join award natural join make where awardID='406'");
            stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)");
            System.out.println("Translated SQL: insert into customerRate select '202', movieID, 5 from directorObtain natural join award natural join make where awardID='406'" +
                    " \n                update movie set avgRate = (select avg(rate) from customerRate where customerRate.movieID=movie.movieID)\n");

            System.out.println("All customer rating inserted.");
            System.out.println("----------------------------------------------");

            System.out.println("Table [customerRate]");
            rs = stmt.executeQuery("select * from customerRate order by customerID");
            printTable(rs);
            System.out.println("Table [movie]");
            rs = stmt.executeQuery("select * from movie order by movieID");
            printTable(rs);

            System.out.println("QUERY #3 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #4
        {
            System.out.println("==============================================");

            System.out.println("Statement: Select the names of the movies whose actor are dead.");
            rs = stmt.executeQuery("select movieName from actor natural join casting natural join movie where dateOfDeath is not null");
            System.out.println("Translated SQL: select movieName from actor natural join casting natural join movie where dateOfDeath is not null\n");
            System.out.println("Table [movie]");
            printTable(rs);

            System.out.println("QUERY #4 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #5
        {
            System.out.println("==============================================");
            System.out.println("Statement: Select the names of the directors who cast the same actor more than once.");

            rs = stmt.executeQuery("select directorName from director natural join casting natural join make " +
                    "group by directorName, actorID having count(directorName) > 1 and count(actorID) > 1");
            System.out.println("Translated SQL: select directorName from director natural join casting natural join make" +
                    " group by directorName, actorID having count(directorName) > 1 and count(actorID) > 1\n");
            printTable(rs);

            System.out.println("QUERY #5 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #6
        {
            System.out.println("==============================================");
            System.out.println("Statement: Select the names of the movies and the genres, where movies have the common genre.");
            rs = stmt.executeQuery("select movieName, genreName from movie natural join movieGenre where genreName in " +
                    "(select genreName from movieGenre group by genreName having count(genreName) > 1) order by genreName");
            System.out.println("Translated SQL: select movieName, genreName from movie natural join movieGenre" +
                    " where genreName in (select genreName from movieGenre group by genreName having count(genreName) > 1) order by genreName\n");
            printTable(rs);

            System.out.println("QUERY #6 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #7
        {
            System.out.println("==============================================");
            System.out.println("Statement: Delete the movies whose director or actor did not get any award and delete data from related tables.");

            stmt.executeUpdate("delete from movie where movieID not in " +
                    "(" +
                    "(select movieID from directorObtain natural join make) " +
                    "intersect" +
                    "(select movieID from actorObtain natural join casting)" +
                    ")");
            System.out.println("Translated SQL: delete from movie where movieID not in " +
                    "((select movieID from directorObtain natural join make) intersect (select movieID from actorObtain natural join casting))\n");

            System.out.println("Deleting related tables data...");
            System.out.println("----------------------------------------------");
            System.out.println("Showing remaining related tables data...\n");

            rs = stmt.executeQuery("select * from movie order by movieID");
            System.out.println("Table [movie]");
            printTable(rs);
            rs = stmt.executeQuery("select * from movieGenre order by movieID");
            System.out.println("Table [movieGenre]");
            printTable(rs);
            rs = stmt.executeQuery("select * from casting order by movieID");
            System.out.println("Table [casting]");
            printTable(rs);
            rs = stmt.executeQuery("select * from make order by movieID");
            System.out.println("Table [make]");
            printTable(rs);
            rs = stmt.executeQuery("select * from customerRate order by movieID");
            System.out.println("Table [customerRate]");
            printTable(rs);

            System.out.println("QUERY #7 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #8
        {
            System.out.println("==============================================");
            System.out.println("Statement: Delete all customers and delete data from related tables.");

            stmt.executeUpdate("delete from customer");
            stmt.executeUpdate("update movie set avgrate = null;");

            System.out.println("Transalted SQL: delete from customer\n");
            System.out.println("Deleting related tables data...");

            System.out.println("----------------------------------------------");
            System.out.println("Showing remaining related tables data...\n");
            rs = stmt.executeQuery("select * from customer order by customerID");
            System.out.println("Table [customer]");
            printTable(rs);
            rs = stmt.executeQuery("select * from movie order by movieID");
            System.out.println("Table [movie]");
            printTable(rs);
            rs = stmt.executeQuery("select * from customerRate order by customerID");
            System.out.println("Table [customerRate]");
            printTable(rs);

            System.out.println("QUERY #8 DONE.");
            System.out.println("==============================================");
        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }

        try //QUERY #9
        {
            System.out.println("==============================================");
            System.out.println("Deleting all tables and data...\n");

            stmt.executeUpdate("drop table movieGenre");
            System.out.println("Table [movieGenre] deleted.");

            stmt.executeUpdate("drop table movieObtain");
            System.out.println("Table [movieObtain] deleted.");

            stmt.executeUpdate("drop table actorObtain");
            System.out.println("Table [actorObtain] deleted.");

            stmt.executeUpdate("drop table directorObtain");
            System.out.println("Table [directorObtain] deleted.");

            stmt.executeUpdate("drop table casting");
            System.out.println("Table [casting] deleted.");

            stmt.executeUpdate("drop table make");
            System.out.println("Table [make] deleted.");

            stmt.executeUpdate("drop table customerRate");
            System.out.println("Table [customerRate] deleted.");

            stmt.executeUpdate("drop table customer");
            System.out.println("Table [customer] deleted.");

            stmt.executeUpdate("drop table director");
            System.out.println("Table [director] deleted.");

            stmt.executeUpdate("drop table actor");
            System.out.println("Table [actor] deleted.");

            stmt.executeUpdate("drop table movie");
            System.out.println("Table [movie] deleted.");

            stmt.executeUpdate("drop table award");
            System.out.println("Table [award] deleted.");

            stmt.executeUpdate("drop table genre");
            System.out.println("Table [genre] deleted.");

            System.out.println("----------------------------------------------");
            System.out.println("Showing remaining tables data...\n");

            rs = stmt.executeQuery("select * from PG_TABLES where schemaname = 'public';");
            printTable(rs);

            System.out.println("QUERY #9 DONE.");
            System.out.println("==============================================");

        }
        catch(SQLException sqlx)
        {
            System.out.println("SQLEXCEPTION : " + sqlx);
        }


        connection.close();
    }



}
