/*
 * Copyright 2014 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionNumber
import org.hibernate.envers.RevisionTimestamp

@Entity
@RevisionEntity
class PasterRevEntity {

    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name="revision_id")
    private var id:java.lang.Long  = null

    @RevisionTimestamp
    @Column(name="revision_timestamp")
    private var timestamp:java.lang.Long = null
  
    def getId() = id
    def setId(id:java.lang.Long) = {this.id = id}
    
    def getTimestamp() = timestamp
    def setTimestamp(ts:java.lang.Long) {this.timestamp =ts}
}
