package com.Ox08.paster.webapp.base

import com.Ox08.paster.webapp.dao.PasteDao
import org.springframework.beans.factory.annotation.{Autowired, Value}

import java.time.{LocalDateTime, ZoneId}

class PasterPeriodicTasksBean extends Logged{

  @Value("${paster.periodic.removeExpiredAfterDays}")
  private val removeExpiredAfterDays = 0

  @Autowired
  val pasteDao: PasteDao = null

  def checkExpired(): Unit = {

    val expired =LocalDateTime.now().minusDays(removeExpiredAfterDays)
      .atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    if (logger.isDebugEnabled)
      logger.debug(s"removing expired records from: '${expired}'")

    pasteDao.deleteExpired(expired)
  }

  def sendPushNotifications(): Unit = {

  }
}
