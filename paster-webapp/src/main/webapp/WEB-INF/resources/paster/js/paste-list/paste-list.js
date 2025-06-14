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
/*
 Main logic for list page
*/
class PasterList {

    init(pageUrl, userPageUrl, maxRequests, currentPage) {
        const self = this;
        this.lazy = new LazyPagination();
        this.lazy.initialize({
            url: pageUrl,
            method: 'get',
            maxRequests: maxRequests,
            buffer: 250,
            pageDataIndex: 'page',
            idMode: false,
            data: {
                page: currentPage
            },
            inject: {
                element: 'morePages'
            }, 
            beforeLoad: function () {
                document.getElementById('pageLoadSpinner').style.display = '';
            },
            afterAppend: function (block, page) {
                try {
                    history.pushState({ page: page },
                            "Page " + page, userPageUrl + "/" + page);
                } catch (e) {
                }
                const elSpinner = document.getElementById('pageLoadSpinner');
                const newPage = document.getElementById('paste_list_' + page);

                //elSpinner.insertAdjacentHTML('afterEnd', newPage);
                elSpinner.style.display = 'none';

                self.parseSearchResults(block);
                pasterApp.bindDeleteDlg(block);
            }
        });
        self.parseSearchResults(document.getElementById('pastas'));
    }
    parseSearchResults(parent) {
        Array.from(parent.getElementsByClassName('pasteTitle')).forEach(
            function (el, i, array) {
                el.innerHTML = el.innerHTML
                    .replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,
                        "<span style='background-color: #e3e658; '>$1</span>");
            }
        );
    }
};