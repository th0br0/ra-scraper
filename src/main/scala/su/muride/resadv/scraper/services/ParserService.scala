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

case class Dj(name: String, id: Option[String])

case class EventDate(text: String, date: LocalDate)

case class Event(id: Int, name: String, description: Option[String], date: EventDate, cost: Option[EventCost], lineup: Seq[Dj],
  promoters: Seq[Promoter], attendeeCount: Int, venue: EventVenue, owner: Profile)

case class EventCost(cost: Option[Float], currency: Option[String])

case class EventVenue(id: Option[Int], name: String, address: String, countryId: Int)

case class Profile(name: String, id: String)

case class Promoter(name: String, id: Option[Int])

trait NameExtractor[T] extends HtmlExtractor[Seq[T]] with NodeVisitor {
  val maxDepth: Int

  val current = ListBuffer[Node]()
  var objs = ListBuffer[T]()

  override def head(node: Node, depth: Int): Unit = {
    // TODO linebreak with meta after dj name...

    node match {
      case e: Element if e.nodeName.equalsIgnoreCase("br")            => done()
      case e: Element if e.nodeName.equalsIgnoreCase("a")             => current += e
      case t: TextNode if !t.text().contains(",") && depth < maxDepth => current += t
      case t: TextNode if depth < maxDepth => {
        val els = t.text().split(",").toSeq
        var bracketCounter = 0
        for (el <- els) {
          if (el.trim.length == 0) done()
          else {
            bracketCounter += el.count(_ == '(')
            bracketCounter -= el.count(_ == ')')

            if (bracketCounter == 0) {
              current += new TextNode(el, "/")
              done()
            } else {
              current += new TextNode(el + ",", "/")
            }
          }
        }
      }
      case _ =>
    }
  }

  override def tail(node: Node, depth: Int): Unit = {}

  def done()

  override def extract(p: Elements): Seq[T] = {
    p.foreach(p => {
      p.traverse(this);
      done();
    })

    objs.toSeq
  }
}

class DjExtractor extends NameExtractor[Dj] {
  val maxDepth = 2

  def done() = {
    if (!current.isEmpty) {
      objs += Dj(
        name = current.foldLeft("") {
          case (a: String, b: TextNode) => a + b.text()
          case (a: String, b: Element)  => a + b.text()
        } trim,
        id = current.filter(_.isInstanceOf[Element]).headOption.map(n => n.attr("href").replace("/dj/", ""))
      )
    }
    current.clear()
  }

}

class PromoterExtractor extends NameExtractor[Promoter] {
  val maxDepth = 2

  def done() = {
    if (!current.isEmpty) {
      objs += Promoter(
        name = current.foldLeft("") {
          case (a: String, b: TextNode) => a + b.text()
          case (a: String, b: Element)  => a + b.text()
        } trim,
        id = current.filter(_.isInstanceOf[Element]).headOption.flatMap(n => n.attr("href").split("id=").lift(1)).map(_.toInt)
      )
    }
    current.clear()
  }
}

class EventDateExtractor extends HtmlExtractor[EventDate] with NodeVisitor {
  val current = StringBuilder.newBuilder
  var timestamp = LocalDate.now()

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

class EventVenueExtractor extends HtmlExtractor[EventVenue] with NodeVisitor {
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

    EventVenue(
      clubEl.flatMap(_.attr("href").split("id=").lift(1)).map(_.toInt),
      clubName.trim.stripSuffix(",").stripSuffix(";"),
      clubAddress.trim.stripSuffix(",").stripSuffix(";"),
      localId
    )
  }
}

trait ParserService {
  val Decimal = """(\d+)(\.\d*)?""".r

  def parseEventPage(pageContent: String): Event = {
    val browser = new Browser
    val doc = browser.parseString(pageContent)

    val id = (doc >> attr("content")("""meta[Property="og:url"]""")).split("\\?")(1).toInt

    val title: String = doc >> text("#sectionHead h1")
    val lineup = (doc >> elementList(".lineup") >> new DjExtractor).flatten.distinct
    val description = (doc >> element("div.left") >> elementList("p")).lift(1).map(_.html())
    val membersFavouriteCount = doc >> text("#MembersFavouriteCount")

    val aside = doc >> element("#detail") >> elementList("li")

    val profile = Profile(
      doc >> text(".links a:first-child"),
      (doc >> attr("href")(".links a:first-child")).replace("/profile/", ""))

    var date: EventDate = null
    var cost: Option[EventCost] = None
    var venue: EventVenue = null
    var promoters: Seq[Promoter] = Seq.empty

    aside.foreach(s => s.text() match {
      case t: String if t.startsWith("Cost") => {
        val costString = t.split("/")(1).trim
        val price = Decimal.findAllIn(costString).find(_ => true)

        val unit = Option(costString.filter(c => !(c.isDigit || c == ' ')).trim).filter(_.nonEmpty)

        cost = Some(EventCost(price.map(_.toFloat), unit))
      }
      case t: String if t.startsWith("Date") => {
        date = s >> new EventDateExtractor
      }
      case t: String if t.startsWith("Venue") => {
        venue = s >> new EventVenueExtractor
      }
      case t: String if t.startsWith("Promoters") => {
        promoters = s >> new PromoterExtractor
      }
    })

    val event = Event(id, title, description, date, cost, lineup, promoters, membersFavouriteCount.toInt, venue, profile)

    event
  }

}

object ParserService extends ParserService
