package com.Ox08.paster.webapp.base

import com.Ox08.paster.webapp.dao.{CommentDao, PasteDao}
import com.Ox08.paster.webapp.manager.ResourceManager
import com.Ox08.paster.webapp.model.Paste
import com.Ox08.paster.webapp.mvc.MvcConstants
import org.springframework.beans.factory.annotation.{Autowired, Value}

import java.time.{LocalDateTime, ZoneId}
import java.util

/**
 * This class stores all periodic tasks, which configured from push.xml and scheduler.xml
 * @since 3.0
 * @author 0x08
 */
class PasterPeriodicTasksBean extends Logged{

  @Value("${paster.periodic.removeExpiredAfterDays}")
  private val removeExpiredAfterDays = 0

  @Autowired
  val pasteDao: PasteDao = null

  @Autowired
  val commentsDao: CommentDao = null

  @Autowired
  val ws: WebhookService = null

  @Autowired
  private val resourceDao: ResourceManager = null

  /**
   * Checks records for expiration, expired records will be removed from database
   */
  def checkExpired(): Unit = {

    val expired =LocalDateTime.now().minusDays(removeExpiredAfterDays)
      .atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    if (logger.isDebugEnabled)
      logger.debug(s"removing expired records from: '${expired}'")

    val pastas = pasteDao.getListForRemoval(expired)

    if (pastas.isEmpty)
      return

    val ids: util.List[Integer] = new util.ArrayList

    pastas.forEach( e => ids.add(e.id))

    try {
      pastas.forEach(e => removePaste(e))
    } finally {
      pasteDao.markNotified(ids,value = true)
    }

  }

  /**
   * This method is called periodically
   */
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
      pasteDao.markNotified(ids,value = true)
    }
  }

  private def removePaste(p: Paste): Unit = {

    resourceDao.tryDelete(p.thumbImage,'t')
    resourceDao.tryDelete(p.reviewImgData,'r')
    commentsDao.deleteCommentsFor(p.id,null)
    pasteDao.remove(p.id)
  }

  private def transferNotify(p: Paste): Unit = {
    ws.notifyHooks(new PasteEvent(p.id,
          if (p.isReviewed) "REVIEWED" else "CREATED",
          p.title,
          if (p.isReviewed) p.reviewer else p.author))

  }
}
