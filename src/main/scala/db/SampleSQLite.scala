/**
 *
 */
package db

// Use SQLiteDriver to connect or create a sqlite database
import scala.slick.driver.SQLiteDriver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * @author manuel
 * 
 * Shows how to create a sqlite db, create a table, 
 * populate table and run a query.
 */
object SampleSQLite {

  def main(args: Array[String]): Unit = {

    println("Here we go!")

    Database.forURL("jdbc:sqlite:" + System.getProperty("user.dir") + "/testdb.db",
      driver = "org.sqlite.JDBC") withSession {

      // A simple table named Cats
      object Cats extends Table[(Int, String)]("CATS") {
        def id = column[Int]("ID", O.PrimaryKey) // The primary key column
        def name = column[String]("NAME")
         // Every table needs a * projection with the same type as the table's type parameter
        def * = id ~ name
      }
      
      // create the table
      Cats.ddl.create
      
      // Insert some cats
      Cats.insert(67, "Vlad")
      Cats.insert(85, "Igor")
      
       // Iterate through all cats
      Query(Cats) foreach {
        case (id, name) =>
          println("  " + name + "\t" + id)
      }
      
      }
  }

}