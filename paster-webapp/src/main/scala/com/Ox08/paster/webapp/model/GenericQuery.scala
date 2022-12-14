/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.model
import org.apache.commons.lang3.StringUtils
/**
 * Query trait
 */
trait Query {
  /**
   * if this query empty
   */
  def isEmpty: Boolean
  /**
   * @return query string
   */
  def getQuery: String
  def setQuery(query: String): Unit
  /**
   * @return current page
   */
  def getPage: Int
  def setPage(page: Int): Unit
}
/**
 * Generic search query DTO
 */
class GenericQuery extends Query {
  // text to search
  var query: String = _
  // initial page
  var page: Int = 1
  def setPage(page: Int): Unit = {
    this.page = page
  }
  def getPage: Int = page
  /**
   * Checks if specified query is blank or wildcard.
   * @return
   *    true if query is blank/null or wildcard
   */
  def isEmpty: Boolean = StringUtils.isBlank(query) || query.equals("*")
  def getQuery: String = query
  def setQuery(query: String): Unit = {
    this.query = query
  }
}
class AuthorQuery extends GenericQuery with Query {
  var authorId: Long = _
}
