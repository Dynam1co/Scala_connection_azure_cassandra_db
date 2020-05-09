import com.datastax.driver.core.{PreparedStatement, Session}

object Main extends App {
  val utils = new CassandraUtils(null, "", "", "", "", null, "changeit")
  val cassandraSession = utils.getSession

  try {
    val repository = new UserRepository(cassandraSession)

    //Create keyspace in cassandra database
    repository.createKeyspace()

    //Create table in cassandra database
    repository.createTable()

    //Insert rows into user table
    val preparedStatement: PreparedStatement  = repository.prepareInsertStatement()

    repository.insertUser(preparedStatement, 1, "LyubovK", "Bangalore")
    repository.insertUser(preparedStatement, 2, "JiriK", "Mumbai")
    repository.insertUser(preparedStatement, 3, "IvanH", "Belgum")
    repository.insertUser(preparedStatement, 4, "YuliaT", "Gurgaon")
    repository.insertUser(preparedStatement, 5, "IvanaV", "Dubai")

    println("Select all users")
    repository.selectAllUsers()

    println("Select a user by id (3)")
    repository.selectUser(3)
  } finally {
    utils.close()

    println("Please delete your table after verifying the presence of the data in portal or from CQL")
  }
}
