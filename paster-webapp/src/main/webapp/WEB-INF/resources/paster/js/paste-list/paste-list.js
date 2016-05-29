/* 
 * Copyright 2016 Ubersoft, LLC.
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



var PasterList = new Class({
    initialize: function () {

        this.lazy = undefined;

    },
    init: function (pageUrl, userPageUrl, maxRequests, currentPage) {

        var mainThis = this;

        this.lazy = new LazyPagination(document, {
            url: pageUrl,
            method: 'get',
            maxRequests: maxRequests,
            buffer: 250,
            pageDataIndex: 'page',
            data: {
                page: currentPage
            },
            inject: {
                element: 'morePages',
                where: 'before'
            }, beforeLoad: function () {
                $('pageLoadSpinner').toggle();
            }, afterAppend: function (block, page) {
                // alert(page);
                try {
                    history.pushState({page: page}, "Page " + page, userPageUrl + "/" + page);
                } catch (e) {
                }
                $('paste_list_' + page).grab($('pageLoadSpinner'), "after");

                $('pageLoadSpinner').toggle();
                mainThis.parseSearchResults(block);
                pasterApp.bindDeleteDlg(block);
            }
        });


        this.parseSearchResults($('pastas'));

    },
    parseSearchResults: function (parent) {

        parent.getElements('.pasteTitle').each(function (el, i)
        {
            el.set(
                    'html', el.get('html').replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,
                    "<span style='background-color: #e3e658; '>$1</span>")
                    );
        });

    }

});