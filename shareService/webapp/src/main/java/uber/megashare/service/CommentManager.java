/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.service;

import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.Comment;

/**
 * Сервис работы с комментами
 *
 * @author alex
 */
@Transactional(readOnly = true)
public interface CommentManager extends GenericManager<Comment, Long> {

   

     /**
      * получить комментарий полностью со всеми lazy-полями
      * @param id
      *         id комментария
      * @return
      *         коммент
      */
     public Comment getFull(Long id);
}
