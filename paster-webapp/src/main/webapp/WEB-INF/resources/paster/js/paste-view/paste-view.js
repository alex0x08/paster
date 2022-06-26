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




class PasterView {
    initialize() {
        SyntaxHighlighter.config.tagName = "pre";
        this.clip = undefined;
        this.clipLine = undefined;
        this.lazyPaging = undefined;

    }
    setupDraw(modelId) {
        var $j = jQuery.noConflict();
        var mainThis = this;

        const colors_array = ['#ff1101', '#ff0', '#0f0', '#0ff', '#00f', '#6d47e7', '#000', '#fff'];

        var toolsEl =document.getElementById(modelId + '_tools')

        colors_array.forEach(c => {

            const htmlBlock =  "<a href='#" + modelId
                                + "_sketch' class='btn btn-xs' data-color='" + c
                                + "' style='border:1px solid black; background: " + c
                                + ";'>&nbsp;&nbsp;</a> ";

                toolsEl.insertAdjacentHTML( 'beforeend', htmlBlock );
         });


        const sz_array = [1, 3, 5, 10, 15];

        sz_array.forEach(c => {

            const htmlBlock =  "<a href='#" + modelId + "_sketch' data-size='" + c
                                + "' style='background: #ccc'>" + c + "</a> ";
                toolsEl.insertAdjacentHTML( 'beforeend', htmlBlock );
        });

        $j('#' + modelId + '_sketch').sketch();

       document.getElementById(modelId + '_saveReviewBtn')
                .addEventListener("click", function(event){
                event.preventDefault();
                mainThis.onSaveReviewDraw(modelId);
       });

    }
    setupCommentsAdd(modelId) {
        console.log('setup comments add ', modelId);

        var mainThis = this;
        document.getElementById(modelId + '_addCommentBtn').addEventListener('click', function (event) {
            event.preventDefault();
            this.getElementById('btnCaption').set('text', PasterI18n.text.notify.transmitMessage);
            this.set('disabled', true);
            $(modelId + '_addCommentForm').getElements('.disableOnSubmit').each(function (el, i) {
                el.toggle();
            });

            this.getElementById('btnIcon').toggle();
            mainThis.onSaveComment(modelId);

        });

    }
    setupLazy(pageUrl, userPageUrl, maxRequests, modelId, idSet) {

        var mainThis = this;

        this.lazyPaging = new LazyPagination(document, {
            url: pageUrl,
            method: 'get',
            maxRequests: maxRequests,
            buffer: 100,
            pageDataIndex: 'page',
            idMode: true,
            data: {
                page: 0,
                id: modelId
            },
            idSet: idSet,
            inject: {
                element: 'morePages',
                where: 'before'
            }, beforeLoad: function (page) {
                //alert(page);
                //SyntaxHighlighter.hideEditForm(this.);
                $('pageLoadSpinner').setStyle('display', '');
            }, afterAppend: function (block, page) {

                var ptext = document.getElementById(page + '_pasteText');

                SyntaxHighlighter.highlight(page, {}, ptext, false, false);

                //ptext.setStyle('display', 'none');

                $(page + '_addCommentBtn').addEvent('click', function () {
                    this.getElementById('btnCaption').set('text',
                            PasterI18n.text.notify.transmitMessage).disabled = true;
                    this.getElementById('btnIcon').setStyle('display', '');
                    mainThis.onSaveComment(page);
                });

                try {
                    history.pushState({id: page}, "Page " + page, userPageUrl + "/" + page);
                } catch (e) {
                }
                pasterApp.bindDeleteDlg(block);

                mainThis.setupDraw(page);
                mainThis.showAll(page);

                $('pageLoadSpinner').setStyle('display', 'none');
                $(page + '_pasteText').grab($('pageLoadSpinner'), "after");
            }

        });
    }
    getTextSizes(el) {

        var obj = el.getComputedSize();

        var h = parseInt(obj["totalHeight"]),
                w = parseInt(obj["totalWidth"]);
        return [h, w];

    }
    showAll(modelId) {

        $(modelId + "_drawBlock").setStyle("display", "none");

        var sizes = this.getTextSizes($(modelId + "_pasteText"));

        var sketch = $(modelId + "_sketch_ro");

        sketch.set({
            'height': sizes[0],
            'width': sizes[1]
        });

        $(modelId + "_all").show();

        $(modelId + "_drawBlock").hide();

    }
    init(modelId) {

        this.clip = new ZeroClipboard(document.id("ctrlc_link"));
        this.clip.on('aftercopy', function (event) {
            pasterApp.showNotify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });
        this.clipLine = new ZeroClipboard(document.id("ctrlc_line"));
        this.clipLine.on('aftercopy', function (event) {
            pasterApp.showNotify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });

        SyntaxHighlighter.highlight(modelId, {}, $(modelId + '_pasteText'), true, true);

        this.setupDraw(modelId);
        this.showAll(modelId);
    }
    showComments(modelId) {
        $(modelId + "_drawBlock").hide();
        $(modelId + "_all").hide();
    }
    showDrawArea(modelId, drawReviewData) {

        var sizes = this.getTextSizes($(modelId + "_pasteText"));
        var area = $(modelId + "_drawArea"),
                sketch = $(modelId + "_sketch");
        area.set({
            styles: {
                'height': sizes[0],
                'width': sizes[1]
            }
        });

        sketch.set({
            'height': sizes[0],
            'width': sizes[1]
        });

        const canvas = document.getElementById(modelId + '_sketch');
        const ctx = canvas.getContext('2d');

        if (drawReviewData != '') {
           const img = new Image();
            img.src = drawReviewData;
            img.onload = function () {
                var $j = jQuery.noConflict();

                const sk = $j('#' + modelId + '_sketch')
                        .sketch();
                ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, sizes[1], sizes[0]);
            };
        }
        $(modelId + "_drawBlock").show();
        $(modelId + "_all").hide();
    }
    onSaveComment(modelId) {
        console.log('_on save comment: ', modelId);

        var thumbImg = document.getElementById(modelId + '_thumbImgComment');

        pasterApp.takeScreenshot(document.getElementById(modelId + '_pasteBodyContent'), function (img) {
            thumbImg.set('value', img.src);

            $(modelId + "_addCommentForm").submit();
        });

    }
    onSaveReviewDraw(modelId) {
        console.log('saving review..');
        var $j = jQuery.noConflict();
        var reviewImg = document.getElementById(modelId + '_reviewDrawImg');
        var imgData = $j('#' + modelId + '_sketch').sketch().getData();
        reviewImg.set('value', imgData);

        var thumbImg = document.getElementById(modelId + '_thumbImg');

        this.showAll(modelId);

        var sketch = $(modelId + "_sketch_ro");

        sketch.setStyle('background-image', 'url(' + imgData + ')');

        pasterApp.takeScreenshot($(modelId + '_centerPanel'), function (img) {
            thumbImg.set('value', img.src);

            document.getElementById(modelId + "_saveReviewDraw").submit();
        });

    }

}
   