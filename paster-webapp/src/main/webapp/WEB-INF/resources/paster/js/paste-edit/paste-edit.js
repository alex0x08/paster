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

class PasterEdit {

    MAX_TITLE_LENGTH = 100;

    editor = undefined;
    counter = undefined;

    getEditorType(codeType) {

        switch (codeType) {
            case 'bash':
                return 'sh';
            case 'cpp':
                return 'c_cpp';
            case 'js':
                return 'javascript';
            case 'plain':
                return 'text';
            case 'py':
                return 'python';
            case 'vb':
                return 'vbscript';
            default:
                return codeType;
        }

    }

    init(codeType) {
        const mainThis = this;
        this.counter = new WordCount();
        this.counter.initialize('wordsCount', {
            countWordsTo: document.getElementById('wordsCount'),
            countSymbolsTo: document.getElementById('symbolsCount'),
            inputName: null, //The input name from which text should be retrieved, defaults null
            countWords: true, //Whether or not to count words
            countChars: true, //Whether or not to count characters
            charText: '<fmt:message key="paste.edit.word.counter.charText"/>', //The text that follows the number of characters
            wordText: '<fmt:message key="paste.edit.word.counter.wordText"/>', //The text that follows the number of words
            separator: ', ', //The text that separates the number of words and the number of characters
            liveCount: false, //Whether or not to use the event trigger, set false if you'd like to call the getCount function separately
            eventTrigger: 'keyup'			//The event that triggers the count update
        })

        var counter = this.counter;

        this.editor = ace.edit("editor");

        var editor = this.editor;

        editor.setTheme("ace/theme/chrome");
        editor.setOptions({
            autoScrollEditorIntoView: true
        });

        editor.getSession().setMode("ace/mode/" + mainThis.getEditorType(codeType));

        const textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.value);

        counter.getCount(textarea.value);

        const elSelectFileBtn = document.getElementById('select-file-btn');

        elSelectFileBtn.addEventListener('change', function (e) {
            mainThis.readLocalFile(e);
        });

        //To focus the ace editor
        // editor.focus();
        //Get the number of lines
        var count = editor.getSession().getLength();
        //Go to end of the last line
        editor.gotoLine(count, editor.getSession().getLine(count - 1).length);

        editor.getSession().on('change', function () {
            const text = editor.getSession().getValue();
            textarea.value = text;
            counter.getCount(text);
        });

        document.getElementById('ptheme').addEventListener('change', function (event) {
            editor.setTheme(this.querySelector('option:selected').value);
        });

        document.getElementById('normalized').addEventListener('click', function (event) {
            if (this.getAttribute('checked')) {
                var col = 80;
                editor.getSession().setUseWrapMode(true);
                editor.getSession().setWrapLimitRange(col, col);
                editor.renderer.setPrintMarginColumn(col);
            } else {
                editor.getSession().setUseWrapMode(false);
                editor.renderer.setPrintMarginColumn(80);
            }
        });


        document.getElementById('fontsize').addEventListener('change', function (event) {
            editor.setFontSize(this.querySelector('option:checked').getAttribute("value"));
        });

        document.getElementById('ptype').addEventListener('change', function (event) {
            editor.getSession()
                .setMode("ace/mode/" + mainThis.getEditorType(this.querySelector('option:checked').getAttribute("value")));
        });

        document.getElementById('pprior').addEventListener('change', function (event) {
            Logger.debug('selected: ', this.querySelector('option:checked'))

            var prPreview = document.getElementById('priorPreview');

            prPreview.className = 'i ' + this.querySelector('option:checked').getAttribute("x-css-class-name");
        });

        Array.from(document.getElementsByClassName('submitBtn')).forEach(
            function (el, i, array) {
                el.addEventListener('click', function (event) {
                    const el2 = document.getElementById('btnCaption');
                    el2.text = PasterI18n.text.notify.transmitMessage;
                    el2.disabled = true;
                    document.getElementById('btnIcon').style.display = '';

                    event.preventDefault();
                    mainThis.onSave();
                });
            });
    }
    clearTitle() {
        document.getElementById('pname').setValue('value', '');
    }
    readLocalFile(e) {
        const file = e.target.files[0];
        if (!file) {
            return;
        }

        const mainThis = this;

        const freader = new FileReader();
        freader.onload = function (e) {
            mainThis.editor.getSession().setValue(e.target.result);
        };
        freader.readAsText(file);

    }
    onPaste(event) {

        let text = (event.clipboardData || window.clipboardData).getData('text/plain');

        Logger.debug('paste event: ', text)

        if (text) {
            const block = (text.length < this.MAX_TITLE_LENGTH - 2)
                ? text.substring(0, text.length) :
                text.substring(0, this.MAX_TITLE_LENGTH - 2) + '..';

            const ptitleEl = document.getElementById('pname');

            if (ptitleEl.getAttribute('value') == '') {
                ptitleEl.setAttribute('value', block);
            }
        }

    }
    onSave() {
        const thumbImg = document.getElementById('thumbImg');
        const editorEl = document.getElementById('editor');

        pasterApp.takeScreenshot(editorEl, function (img) {
            thumbImg.setAttribute('value', img.src);
            document.getElementById('editForm').submit();
        });

    }
}

