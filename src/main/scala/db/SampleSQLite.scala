/**
 *
 */
package db

// Use SQLiteDriver to connect or create a sqlite database
import scala.slick.driver.SQLiteDriver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * A simple table holding cats
 */
object Cats extends Table[(Int, String)]("CATS") {
  def id = column[Int]("ID", O.PrimaryKey) // The primary key column
  def name = column[String]("NAME")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id ~ name
}

/**
 *  some cat owners
 */
object Owners extends Table[(String, String, Int)]("OWNERS") {
  def owner = column[String]("OWNER")
  def cat = column[String]("CAT")
  def id = column[Int]("ID")
  def * = owner ~ cat ~ id
  def pk = primaryKey("PK_OWNER_ID", (owner, id))
}

/**
 * @author manuel
 *
 * Shows how to create a sqlite db, create a table,
 * populate table and run a query, do an insert, and an update.
 */
object SampleSQLite extends App {

  println("Here we go!")
  val fileName = System.getProperty("user.dir") + "/cats.db"
  val dbFile = new java.io.File(fileName)
  val existed = dbFile.exists()

  Database.forURL("jdbc:sqlite:" + fileName,
    driver = "org.sqlite.JDBC") withSession {

      if (existed) {
        println("Dropping the tables")
        // drop the table
        (Cats.ddl ++ Owners.ddl).drop
      }

      println("(Re)creating tables")

      // create the table
      (Cats.ddl ++ Owners.ddl).create

      // Insert some cats
      Cats.insert(67, "Vlad")
      Cats.insert(85, "Igor")
      Cats.insert(12, "Felix")
      Cats.insert(11, "Tom")
      Cats.insert(13, "Tom")
      Cats.insert(66, "Fritz")

      Owners.insert("MC", "Vlad", 67)
      Owners.insert("MC", "Igor", 85)
      Owners.insert("Pat", "Felix", 12)
      Owners.insert("Jerry", "Tom", 11)
      Owners.insert("Bommel", "Tom", 13)
      Owners.insert("Robert", "Frits", 66)

      // Iterate through all cats
      Query(Cats) foreach {
        case (id, name) =>
          println("  " + name + "\t" + id)
      }

      val owners = Owners.map(_.owner).list
      println(owners)

      val mc = for {
        o <- Owners
        if (o.owner === "MC")
      } yield (o.cat, o.id)

      println(mc.list)

      // Now do an insert  // Now do an update
      Cats.insert(77, "Casper")
      Owners.insert("Susna", "Casper", 77)

      println(Owners.map(_.owner).list)
      // Now do an update
      val errorRec = for { o <- Owners if o.owner === "Susna" } yield o.owner
      errorRec.update("Susan")
      println(Owners.map(_.owner).list)
    }

}