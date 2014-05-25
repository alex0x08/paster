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

object ReviewStatus extends KeyValueObj[ReviewStatus] {
 
    val NOT_REVIEWED = new ReviewStatus("NOT_REVIEWED","review.status.not-reviewed")
    val REVIEWED = new ReviewStatus("REVIEWED","review.status.reviewed")
    val ON_REVIEW = new ReviewStatus("NOT_REVIEWED","review.status.on-review")

  add(NOT_REVIEWED)
  add(REVIEWED)
  add(ON_REVIEW)

}

class ReviewStatus(code:String,desc:String) extends KeyValue(code,desc){

}
