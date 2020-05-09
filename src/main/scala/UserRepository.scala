import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session

class UserRepository(session: Session) {
  /**
   * Create keyspace uprofile in cassandra DB
   */
  def createKeyspace(): Unit = {
    val query = "CREATE KEYSPACE IF NOT EXISTS uprofile WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 }"
    session.execute(query)

    println("Created keyspace 'uprofile'")
  }

  /**
   * Create user table in cassandra DB
   */
  def createTable(): Unit = {
    val query = "CREATE TABLE IF NOT EXISTS uprofile.user (user_id int PRIMARY KEY, user_name text, user_bcity text)"
    session.execute(query)

    println("Created table 'user'")
  }

  /**
   * Select all rows from user table
   */
  def selectAllUsers(): Unit = {
    val query = "SELECT * FROM uprofile.user"
    val rows = session.execute(query).all

    rows.forEach(row => println(row.getString("user_name")))
  }

  /**
   * Select a row from user table
   *
   *
   */
  def selectUser(id: Int): Unit = {
    val query = "SELECT * FROM uprofile.user where user_id = 3"
    val row = session.execute(query).one

    println("Obtained row: {} | {} | {} ", row.getInt("user_id"), row.getString("user_name"), row.getString("user_bcity"))
  }

  /**
   * Delete user table.
   */
  def deleteTable(): Unit = {
    val query = "DROP TABLE IF EXISTS uprofile.user"
    session.execute(query)
  }

  /**
   * Insert a row into user table
   *
   * @param id   user_id
   * @param name user_name
   * @param city user_bcity
   */
  def insertUser(statement: PreparedStatement, id: Int, name: String, city: String): Unit = {
    val boundStatement = new BoundStatement(statement)
    session.execute(boundStatement.bind(id, name, city))
  }

  /**
   * Create a PrepareStatement to insert a row to user table
   *
   * @return PreparedStatement
   */
  def prepareInsertStatement(): PreparedStatement = {
    val insertStatement = "INSERT INTO  uprofile.user (user_id, user_name , user_bcity) VALUES (?,?,?)"
    session.prepare(insertStatement)
  }
}
