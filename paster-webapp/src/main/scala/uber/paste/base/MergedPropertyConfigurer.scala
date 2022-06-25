package uber.paste.base

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.{ConfigurableListableBeanFactory, PropertyPlaceholderConfigurer}
import  scala.jdk.CollectionConverters._
class MergedPropertyConfigurer extends PropertyPlaceholderConfigurer{

  private val propertiesMap:java.util.Map[String, String] = new java.util.HashMap[String, String]()

  private var springSystemPropertiesMode:Int = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK

  override def setSystemPropertiesMode(systemPropertiesMode:Int) {
    super.setSystemPropertiesMode(systemPropertiesMode)
    springSystemPropertiesMode = systemPropertiesMode
  }

  @throws(classOf[BeansException])
  override protected def processProperties(beanFactory:ConfigurableListableBeanFactory ,  props:java.util.Properties)  {
    super.processProperties(beanFactory, props)

    for ( key:Object <- props.keySet().asScala) {
      val keyStr = key.toString
      val valueStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
      propertiesMap.put(keyStr, valueStr);
    }
  }


  def getProperty(key:String) = propertiesMap.get(key).toString()
  



}
