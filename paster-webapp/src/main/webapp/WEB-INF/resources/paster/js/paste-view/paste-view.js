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

        var $j = jQuery.noConflict();
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
            this.querySelector('#btnCaption').text =  PasterI18n.text.notify.transmitMessage;
            this.disabled =true;

             Array.from(document
                            .getElementById(modelId + '_addCommentForm')
                            .getElementsByClassName('disableOnSubmit')).forEach(
                        function (el, i, array) {
                            el.style.display='none';
                        });

            this.querySelector('#btnIcon').style.display='';
            mainThis.onSaveComment(modelId);
        });

    }
    setupLazy(pageUrl, userPageUrl, maxRequests, modelId, idSet) {

        var mainThis = this;

    }
    getTextSizes(el) {
        var h = parseInt(el.offsetHeight),
                w = parseInt(el.offsetWidth);
        return [h, w];

    }
    showAll(modelId) {

        document.getElementById(modelId + "_drawBlock").style.display = "none";
        const sizes = this.getTextSizes(document.getElementById(modelId + "_pasteText"));
        const sketch = document.getElementById(modelId + "_sketch_ro");

        sketch.width = sizes[1];
        sketch.height = sizes[0];

        document.getElementById(modelId + "_all").style.display = '';
        document.getElementById(modelId + "_drawBlock").style.display='none';

    }
    init(modelId) {

        /*this.clip = new ZeroClipboard(document.id("ctrlc_link"));
        this.clip.on('aftercopy', function (event) {
            pasterApp.showNotify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });
        this.clipLine = new ZeroClipboard(document.id("ctrlc_line"));
        this.clipLine.on('aftercopy', function (event) {
            pasterApp.showNotify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });*/

        SyntaxHighlighter.highlight(modelId, {}, document.getElementById(modelId + '_pasteText'), true, true);

        this.setupDraw(modelId);
        this.showAll(modelId);
    }
    showComments(modelId) {
        document.getElementById(modelId + "_drawBlock").style.display='none';
        document.getElementById(modelId + "_all").style.display='none';
    }
    showDrawArea(modelId, drawReviewData) {

        var sizes = this.getTextSizes(document.getElementById(modelId + "_pasteBodyContent"));
        console.log('sizes: ',sizes);
        var area = document.getElementById(modelId + "_drawArea"),
                sketch = document.getElementById(modelId + "_sketch");
        area.style.height =sizes[0];
        area.style.width = sizes[1];

        sketch.height = sizes[0];
        sketch.width = sizes[1];

        const canvas = document.getElementById(modelId + '_sketch');
        const ctx = canvas.getContext('2d');

        if (drawReviewData != '') {
           const img = new Image();
            img.src = drawReviewData;
            img.onload = function () {
                ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, sizes[1], sizes[0]);
            };
        }
        document.getElementById(modelId + "_drawBlock").style.display='';
        document.getElementById(modelId + "_all").style.display='none';
    }
    onSaveComment(modelId) {
        console.log('_on save comment: ', modelId);

        const thumbImg = document.getElementById(modelId + '_thumbImgComment');

        pasterApp.takeScreenshot(document.getElementById(modelId + '_pasteBodyContent'), function (img) {
            thumbImg.value = img.src;
            document.getElementById(modelId + "_addCommentForm").submit();
        });

    }
    onSaveReviewDraw(modelId) {
        console.log('saving review..');
        const reviewImg = document.getElementById(modelId + '_reviewDrawImg');

        const $j = jQuery.noConflict();

        const imgData = $j('#' + modelId + '_sketch').sketch().getData();
        reviewImg.value = imgData;

        const thumbImg = document.getElementById(modelId + '_thumbImg');

        this.showAll(modelId);

        var sketch = document.getElementById(modelId + "_sketch_ro");
        sketch.style['background-image'] = 'url(' + imgData + ')';

        pasterApp.takeScreenshot(
                    document.getElementById(modelId + '_centerPanel'), function (img) {
            thumbImg.value = img.src;
            console.log('screenshot taken ',thumbImg.value)
            document.getElementById(modelId + "_saveReviewDraw").submit();
        });

    }

}
   