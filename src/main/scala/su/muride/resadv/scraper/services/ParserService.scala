package su.muride.resadv.scraper.services

import com.google.common.base.Splitter
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import org.joda.time.LocalDate
import org.jsoup.nodes.{ Element, Node, TextNode }
import org.jsoup.select.{ Elements, NodeVisitor }

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

/**
 * @author Andreas C. Osowski
 */
object ParserService extends ParserService

case class Dj(name: String, id: Option[String])

case class EventDate(text: String, date: LocalDate)

case class Event(name: String, timestamp: String, lineup: List[Dj])

case class EventCost(cost: Int, currency: Option[String])

case class ClubLocation(id: Option[Int], name: String, address: String, countryId: Int)

class DjExtractor extends HtmlExtractor[Seq[Dj]] with NodeVisitor {

  val current = ListBuffer[Node]()
  var djs = ListBuffer[Dj]()
  val regex = """([^,]+)""".r

  override def head(node: Node, depth: Int): Unit = {
    // TODO linebreak with meta after dj name...

    node match {
      case e: Element if e.nodeName.equalsIgnoreCase("br")     => done()
      case e: Element if e.nodeName.equalsIgnoreCase("a")      => current += e
      case t: TextNode if !t.text().contains(",") && depth < 2 => current += t
      case t: TextNode if depth < 2 => {
        var bracketCounter = 0
        for (el <- regex.findAllIn(t.text) if el.trim.length > 0) {
          current += new TextNode(el, "/")
          bracketCounter += el.count(_ == '(')
          bracketCounter -= el.count(_ == ')')

          if (bracketCounter == 0) {
            done()
          }
        }
      }
      case _ =>
    }
  }

  override def tail(node: Node, depth: Int): Unit = {}

  def done() = {
    if (!current.isEmpty) {
      djs += Dj(
        name = current.foldLeft("") {
          case (a: String, b: TextNode) => a + b.text()
          case (a: String, b: Element)  => a + b.text()
        } trim,
        id = current.filter(_.isInstanceOf[Element]).headOption.map(n => n.attr("href").replace("/dj/", ""))
      )
    }
    current.clear()
  }

  override def extract(p: Elements): Seq[Dj] = {
    p.foreach(p => {
      p.traverse(this);
      done();
    })

    djs.toSeq
  }
}

class DateExtractor extends HtmlExtractor[EventDate] with NodeVisitor {
  val current = StringBuilder.newBuilder
  var timestamp = new LocalDate()

  override def head(node: Node, depth: Int): Unit = {

    node match {

      case e: Element if e.nodeName.equalsIgnoreCase("a") => {
        val pairs = Splitter.on('&').trimResults.withKeyValueSeparator("=").split(e.attr("href").split("\\?")(1))

        timestamp = pairs("v") match {
          case "day" => new LocalDate(pairs("yr").toInt, pairs("mn").toInt, pairs("dy").toInt)
          case _     => throw new RuntimeException("unhandled date type: " + e.attr("href"))
        }
      }
      case t: TextNode if !t.parent.nodeName.equalsIgnoreCase("div") => current ++= t.text() + " "
      case _                                                         =>
    }
  }

  override def tail(node: Node, depth: Int): Unit = {}

  override def extract(p: Elements): EventDate = {
    p.foreach(_.traverse(this))

    EventDate(current.toString, timestamp)
  }
}

class ClubLocationExtractor extends HtmlExtractor[ClubLocation] with NodeVisitor {
  val current = StringBuilder.newBuilder
  var localId: Int = -1

  override def head(node: Node, depth: Int): Unit = {

    node match {
      case t: TextNode if !t.parent.nodeName.matches("a|div") => current ++= t.text() + " "
      case t: TextNode if t.parent.attr("href").contains("local.aspx") =>
        localId = t.parent.attr("href").split("ai=")(1).toInt
      case _ =>
    }
  }

  override def tail(node: Node, depth: Int): Unit = {}

  override def extract(p: Elements) = {
    p.foreach(_.traverse(this))
    val clubEl: Option[Element] = p >?> element(".cat-rev")

    val (clubName, clubAddress) = clubEl match {
      case Some(s) => (s.text(), current.toString.trim)
      case None => {
        val c = current.toString

        c.indexOf(";") match {
          case -1 => (c.trim, "")
          case p => (
            c.substring(0, p).trim,
            c.substring(p + 1).trim
          )
        }
      }
    }

    ClubLocation(
      clubEl.flatMap(_.attr("href").split("id=").lift(1)).map(_.toInt),
      clubName,
      clubAddress,
      localId
    )
  }
}

trait ParserService {
  private val currencyRegex = """[\p{Sc}\u0024\u060B][\d,.]+""".r

  def parseEventPage(pageContent: String): String = {
    val browser = new Browser
    val doc = browser.parseString(pageContent)

    val title: String = doc >> text("#sectionHead h1")
    val lineup = (doc >> elementList(".lineup") >> new DjExtractor).flatten
    val description = (doc >> element("div.left") >> elementList("p")).lift(1)
    val membersFavouriteCount = doc >> text("#MembersFavouriteCount")

    val aside = doc >> element("#detail") >> elementList("li")
    val date = aside(0) >> new DateExtractor
    val location = aside(1) >> new ClubLocationExtractor
    val cost = aside.lift(2).map(_.text()) match {
      case Some(s: String) if s.startsWith("Cost") => {

        val costString = s.split("/")(1).trim
        //println(currencyRegex.findAllIn(costString))
        println(costString)

        "123"
      }
      case _ => ???
    }

    println("Event title: " + title)
    println("Event lineup: " + lineup)
    println("Event description: " + description)
    println("Event date: " + date)
    println("Event location: " + location)
    println("Members favourite count: " + membersFavouriteCount)
    println("aside: " + aside)

    title

  }

}
