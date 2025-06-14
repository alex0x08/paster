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

class PasterView {
    setupDraw(modelId, allowEdit) {
        Logger.debug('setup draw ', modelId, 'allow edit', allowEdit);
        var self = this;
        const colors_array = ['#D20202', '#ff1101', '#ff0',
        '#0f0', '#0ff', '#00f', '#6d47e7', '#000', '#fff'];

        var toolsEl = document.getElementById(`${modelId}_tools`);
        fastdom.mutate(() => {
            colors_array.forEach(c => {
                const htmlBlock = "<a href='#" + modelId
                    + "_sketch' class='" + modelId + "_sketch_color btn btn-xs' data-color='" + c
                    + "' style='border:1px solid black; background: " + c
                    + ";'>&nbsp;&nbsp;</a> ";
                toolsEl.insertAdjacentHTML('beforeend', htmlBlock);
            });
            Array.from(document.getElementsByClassName(`${modelId}_sketch_color`)).forEach(
                function (el, i, array) {
                    el.addEventListener("click", function (e) {
                        e.preventDefault();
                        const dcolor = el.getAttribute('data-color');
                        currentColor = dcolor;
                        Logger.debug('current color:', currentColor);
                    });
                });
        });
        const sz_array = [1, 3, 5, 10, 15];
        fastdom.mutate(() => {
            sz_array.forEach(c => {
                const htmlBlock = "<a href='#" + modelId + "_sketch' class="
                    + modelId + "_sketch_sz data-size='" + c
                    + "' style='background: #ccc'>" + c + "</a> ";
                toolsEl.insertAdjacentHTML('beforeend', htmlBlock);
            });
            Array.from(document.getElementsByClassName(`${modelId}_sketch_sz`))
                .forEach(
                    function (el, i, array) {
                        el.addEventListener("click", function (e) {
                            e.preventDefault();
                            const sz = el.getAttribute('data-size');
                            currentSz = sz;
                            radius = 0;
                            Logger.debug('current sz:', currentSz);
                        });
                });
        });
        var currentSz = 5;
        var currentColor = '#D20202';
        var radius = 0;
        var inAction = false;
        self.sketchObj = Sketch.create({
            container: document.getElementById(`${modelId}_drawArea`),
            autostart: false,
            autoclear: false,
            fullscreen: false,
            retina: 'auto',
            setup: function () {
                Logger.debug('sketch configured');
            },
            update: function () {
                radius = 2 + abs(sin(this.millis * 0.003) * currentSz);
            },
            // Event handlers
            keydown: function () {
                //if ( this.keys.C )
                this.clear();
            },
            touchstart: function () {
                this.beginPath();
                inAction = true;
            },
            touchend: function () {
                inAction = false;
            },
            // Mouse & touch events are merged, so handling touch events by default
            // and powering sketches using the touches array is recommended for easy
            // scalability. If you only need to handle the mouse / desktop browsers,
            // use the 0th touch element and you get wider device support for free.
            touchmove: function () {
                if (inAction) {
                    for (var i = this.touches.length - 1, touch; i >= 0; i--) {
                        touch = this.touches[i];
                        this.lineCap = 'round';
                        this.lineJoin = 'round';
                        this.fillStyle = this.strokeStyle = currentColor;
                        this.lineWidth = radius;
                        this.moveTo(touch.ox, touch.oy);
                        this.lineTo(touch.x, touch.y);
                        this.stroke();
                    }
                }
            }
        });
        if (allowEdit) {
            document.getElementById(`${modelId}_saveReviewBtn`)
                .addEventListener("click", function (event) {
                    Logger.debug('on click saveReview');
                    event.preventDefault();
                    var self2 = this;
                    self2.querySelector('#btnCaption').text = PasterI18n.text.notify.transmitMessage;
                    self2.disabled = true;
                    self2.querySelector('#btnIcon').style.display = '';
                    self.onSaveReviewDraw(modelId);
                });
        }
    }
    showCommentFormError(modelId, errorMessage) {
        fastdom.mutate(() => {
            const errMsg = document.getElementById(`${modelId}_errorMessage`);
            errMsg.innerText = errorMessage;
            errMsg.style.display = '';
        });
    }
    resetCommentFormErrors(modelId) {
        fastdom.mutate(() => {
            const errMsg = document.getElementById(`${modelId}_errorMessage`);
            errMsg.style.display = 'none';
            errMsg.innerText = '';
        });
    }
    toggleOnSubmit(modelId, display) {
        fastdom.mutate(() => {
            Array.from(document
                .getElementById(`${modelId}_addCommentForm`)
                .getElementsByClassName('disableOnSubmit'))
                    .forEach(
                        function (el) {
                            el.style.display = display;
                        });
            });
    }
    setupCommentsAdd(modelId) {
        Logger.debug('setup comments add ', modelId);
        var self = this;
        document.getElementById(`${modelId}_closeCommentBtn`)
            .addEventListener('click', function (event) {
                event.preventDefault();
                SyntaxHighlighter.hideEditForm(modelId);
                self.resetCommentFormErrors(modelId);
            });
        document.getElementById(`${modelId}_addCommentBtn`)
            .addEventListener('click', function (event) {
                event.preventDefault();
                var self2 = this;
                self2.querySelector('#btnCaption').innerText = PasterI18n.text.notify.transmitMessage;
                self2.disabled = true;
                self.resetCommentFormErrors(modelId);
                self.toggleOnSubmit(modelId, 'none');
                self2.querySelector('#btnIcon').style.display = '';
                self.onSaveComment(modelId, function (errorMessage) {
                    self.showCommentFormError(modelId, errorMessage);
                    self2.disabled = false;
                    self2.querySelector('#btnIcon').style.display = 'none';
                    self.toggleOnSubmit(modelId, '');
                });
        });
    }
    setupLazy(pageUrl, userPageUrl, maxRequests, modelId, idSet) {
        Logger.debug('setup lazy modelId:', modelId);
        var self = this;
        this.lazyPaging = new LazyPagination();
        this.lazyPaging.initialize({
            url: pageUrl,
            method: 'get',
            maxRequests: maxRequests,
            buffer: 100,
            idKey: 'id',

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
            },
            skipMeasure: function () {
                return SyntaxHighlighter.checkLoaded(modelId) != true;
            },
            beforeLoad: function (page) {
                document.getElementById('pageLoadSpinner').style.display = '';
            }, afterAppend: function (block, page) {
                const ptext = document.getElementById(`${page}_pasteText`);
                SyntaxHighlighter.highlight(page, {}, ptext, false, false);
                document.getElementById(`${page}_addCommentBtn`)
                    .addEventListener('click', function () {
                        const cap = this.getElementById('btnCaption');
                        cap.innerText=PasterI18n.text.notify.transmitMessage;
                        cap.disabled = true;
                        this.getElementById('btnIcon').setStyle('display', '');
                        self.onSaveComment(page);
                    });
                try {
                    history.pushState({ id: page },
                            `Page ${page}`,
                            `${userPageUrl}/${page}`);
                } catch (e) { }
                pasterApp.bindDeleteDlg(block);
                self.setupDraw(page);
                self.showAll(page);
                const elSpin = document.getElementById('pageLoadSpinner');
                elSpin.style.display = 'none';
                const elPt = document.getElementById(`${page}_pasteText`);
                elPt.parentNode.insertBefore(elSpin, elPt);
            }
        });
    }
    getTextSizes(el) {
        const h = parseInt(el.offsetHeight) + 10,
            w = parseInt(el.offsetWidth);
        return [h, w];
    }
    showAll(modelId) {
        Logger.debug('called showAll, modelId', modelId);
        fastdom.mutate(() => {
            document.getElementById(`${modelId}_drawBlock`).style.display = "none";
            const sizes = this.getTextSizes(document.getElementById(`${modelId}_pasteText`));
            const sketch = document.getElementById(`${modelId}_sketch_ro`);
            sketch.width = sizes[1];
            sketch.height = sizes[0];
            document.getElementById(modelId + "_all").style.display = '';
            document.getElementById(modelId + "_drawBlock").style.display = 'none';
        });
    }
    init(modelId, allowEdit) {
        const self = this;
        document.getElementById("ctrlc_line")
            .addEventListener("click", function (event) {
                event.preventDefault();
                const dct = this.getAttribute('data-clipboard-target')
                const text = document.getElementById(dct).innerText
                // console.log('to clipboard:', text)
                self.copyToClipboard(text)
            });
        SyntaxHighlighter.highlight(modelId, {},
        document.getElementById(`${modelId}_pasteText`), true, allowEdit);
        document.getElementById(`${modelId}_btnShowAll`)
            .addEventListener("click", function (event) {
                event.preventDefault();
                self.toggleControls(this.getAttribute('id'));
                self.showAll(modelId);
            });
        document.getElementById(`${modelId}_btnShowComments`)
            .addEventListener("click", function (event) {
                event.preventDefault();
                self.toggleControls(this.getAttribute('id'));
                self.showComments(modelId);
            });
        document.getElementById(modelId + '_btnShowDraw')
            .addEventListener("click", function (event) {
                event.preventDefault();
                self.toggleControls(this.getAttribute('id'));
                const drawImg = document.getElementById(modelId + '_drawImg').textContent;
                self.showDrawArea(modelId, drawImg);
            });
        this.setupDraw(modelId, allowEdit);
        this.showAll(modelId);
    }
    toggleControls(selectedId) {
        const controls = document.getElementById('pasteViewControls');
        Array.from(controls.getElementsByTagName('button')).forEach(
            function (el, i, array) {
                if (selectedId == el.getAttribute('id')) {
                    el.className = 'btn btn-primary active'
                } else {
                    el.className = 'btn btn-primary'
                }
            });
    }
    showComments(modelId) {
        Logger.debug('show only comments');
        document.getElementById(`${modelId}_drawBlock`).style.display = 'none';
        document.getElementById(`${modelId}_all`).style.display = 'none';
    }
    showDrawArea(modelId, drawReviewData) {
        const sizes = this.getTextSizes(document
                .getElementById(`${modelId}_pasteBodyContent`));
        Logger.debug('show draw area, sizes: ', sizes);
        const area = document.getElementById(`${modelId}_drawArea`),
            sketch = document.getElementsByClassName("sketch")[0];
        area.style.height = sizes[0];
        area.style.width = sizes[1];
        sketch.height = sizes[0];
        sketch.width = sizes[1];
        const canvas = sketch;
        const ctx = canvas.getContext('2d');
        if (drawReviewData != '') {
            const img = new Image();
            img.src = drawReviewData;
            img.onload = function () {
                ctx.drawImage(img, 0, 0,
                        img.width,
                        img.height,
                        0, 0,
                        sizes[1],
                        sizes[0]);
            };
        }
        document.getElementById(`${modelId}_drawBlock`).style.display = '';
        document.getElementById(`${modelId}_all`).style.display = 'none';
        this.sketchObj.start();
    }
    /**
     * Handles additional fields on comment save
     * Note that trasfer of text from editor to textarea is done by epiceditor itself on form submit.
     * 
     * @param {*} modelId 
     */
    onSaveComment(modelId, onError) {
        Logger.debug('_on save comment: ', modelId);
        const ed = SyntaxHighlighter.getEditor(modelId);
        ed.save();
        var ptext = ed.exportFile();
        if ((/^\s*$/).test(ptext)) {
            Logger.debug('no comment text found.');
            if (onError) {
                onError('Please add some text.');
            }
            return;
        }
        Logger.debug('got text', ptext);
        ptext = ptext.trim().replace(/\r\n|\r/g, '\n')
           // .replace(/\t/g, '    ')
            .replace(/\u2424/g, '\n');
        Logger.debug('formatted text:"', ptext,'"');
        const commentArea = document.getElementById(`commentText-${modelId}`);
        commentArea.innerHTML = ptext;
        const thumbImg = document.getElementById(`${modelId}_thumbImgComment`);
        pasterApp.takeScreenshot(
            document.getElementById(`${modelId}_pasteBodyContent`), function (img) {
            thumbImg.value = img.src;
            document.getElementById(`${modelId}_addCommentForm`).submit();
        });
    }
    onSaveReviewDraw(modelId) {
        setTimeout(function () {
        Logger.debug('saving review..');
        const reviewImg = document.getElementById(`${modelId}_reviewDrawImg`);
        const sketch = document.getElementsByClassName("sketch")[0];
        const imgData = sketch.toDataURL("image/png");
        reviewImg.value = imgData;
        const thumbImg = document.getElementById(`${modelId}_thumbImg`);
        document.getElementById(`${modelId}_all`).style.display = '';
        document.getElementById(`${modelId}_drawBlock`).style.display = 'none';
        const sketchRo = document.getElementById(`${modelId}_sketch_ro`);
        sketchRo.style['background-image'] = `url(${imgData})`;
        pasterApp.takeScreenshot(
            document.getElementById(`${modelId}_centerPanel`), function (img) {
                thumbImg.value = img.src;
                Logger.debug('screenshot taken ', thumbImg.value)
                document.getElementById(`${modelId}_saveReviewDraw`).submit();
            });
        }, 1);
    }
    copyToClipboard(text) {
        var copyElement = document.createElement('input');
        copyElement.setAttribute('type', 'text');
        copyElement.setAttribute('value', text);
        copyElement = document.body.appendChild(copyElement);
        copyElement.select();
        try {
            if (!document.execCommand('copy')) {
                throw 'Not allowed.';
            } else {
                pasterApp.showNotify(`${text.length} symbols copied to clipboard.`);
            }
        } catch (e) {
            copyElement.remove();
            Logger.warn("document.execCommand('copy'); is not supported");
        } finally {
            if (typeof e == 'undefined') {
                copyElement.remove();
            }
        }
    }
}
