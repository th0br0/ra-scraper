package su.muride.resadv.scraper.models

import org.joda.time.LocalDate

/**
 * @author Andreas C. Osowski
 */
case class EventEntity(
  id: Int,
  name: String,
  description: Option[String],
  date: LocalDate,
  dateDesc: String,
  price: Option[Float],
  priceDesc: Option[String],
  attendeeCount: Int,
  venueId: Option[Int],
  ownerId: String)
