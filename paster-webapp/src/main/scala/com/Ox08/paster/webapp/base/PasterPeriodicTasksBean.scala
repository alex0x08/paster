package com.Ox08.paster.webapp.base

import com.Ox08.paster.webapp.dao.PasteDao
import com.Ox08.paster.webapp.model.Paste
import org.springframework.beans.factory.annotation.{Autowired, Value}

import java.time.{LocalDateTime, ZoneId}
import java.util

class PasterPeriodicTasksBean extends Logged{

  @Value("${paster.periodic.removeExpiredAfterDays}")
  private val removeExpiredAfterDays = 0

  @Autowired
  val pasteDao: PasteDao = null

  @Autowired
  val ws: WebhookService = null

  def checkExpired(): Unit = {

    val expired =LocalDateTime.now().minusDays(removeExpiredAfterDays)
      .atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    if (logger.isDebugEnabled)
      logger.debug(s"removing expired records from: '${expired}'")

    pasteDao.deleteExpired(expired)
  }

  def sendPushNotifications(): Unit = {

    val notificationRun =LocalDateTime.now()
        .minusMinutes(5)
          .atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    val pastas = pasteDao.getListToNotify(notificationRun)

    if (pastas.isEmpty)
      return

    val ids: util.List[Integer] = new util.ArrayList

    pastas.forEach( e => ids.add(e.id))

    try {
      pastas.forEach(e => transferNotify(e))
    } finally {
      pasteDao.markNotified(ids)
    }
  }

  private def transferNotify(p: Paste): Unit = {
    ws.notifyHooks(new PasteEvent(p.id,
          if (p.isReviewed) "REVIEWED" else "CREATED",
      p.title,
          if (p.isReviewed) p.reviewer else p.author))

  }
}
