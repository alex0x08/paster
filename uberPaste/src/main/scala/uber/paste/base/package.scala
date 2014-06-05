package uber.paste.base

/** Provides classes for dealing with interface plugins
  *
  * ==Overview==
  * 
  * [[uber.paste.base.plugins.PluginUI]] stores "integration points" and all api
  * to bind to them   
  */
package object plugins {
}

/**
 * Contains some object builders
 */
package object build {
}

/**
 *  Provides all spring mvc controllers
 */
package object controller {
}

/**
 *  All classes for retrieve/persist data from database
 */
package object dao {
}

/**
 *  Contains all logic for email usage in project
 */
package object mail {
}

/**
 *  Layer between dao and controllers, provides access control and
 *  additional logic, not related to database access or data representation
 */
package object manager {
}

/**
 *  Provides all model classes
 */
package object model {
}

/**
 * Dealing with OpenID
 */
package object openid {
}

/**
 *  Contains classes related to startup logic
 */
package object startup {
}

/**
 *  Customized tiles view classes
 */
package object tiles2 {
}
