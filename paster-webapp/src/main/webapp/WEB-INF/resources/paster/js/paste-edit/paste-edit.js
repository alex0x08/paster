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



var PasterEdit = new Class({
    
    initialize: function () {

        this.max_length = 100;

        this.editor = undefined;

        this.counter = undefined;

    },
    init: function (codeType) {

        var mainThis = this;

        this.counter = new WordCount('wordCount', {
            countWordsTo: $('wordsCount'),
            countSymbolsTo: $('symbolsCount'),
            inputName: null, //The input name from which text should be retrieved, defaults null
            countWords: true, //Whether or not to count words
            countChars: true, //Whether or not to count characters
            charText: '<fmt:message key="paste.edit.word.counter.charText"/>', //The text that follows the number of characters
            wordText: '<fmt:message key="paste.edit.word.counter.wordText"/>', //The text that follows the number of words
            separator: ', ', //The text that separates the number of words and the number of characters
            liveCount: false, //Whether or not to use the event trigger, set false if you'd like to call the getCount function separately
            eventTrigger: 'keyup'			//The event that triggers the count update
        });
        
        var counter = this.counter;

        this.editor = ace.edit("editor");
        
        var editor = this.editor;
        
        editor.setTheme("ace/theme/chrome");
        editor.setOptions({
            autoScrollEditorIntoView: true
        });
        
        editor.getSession().setMode("ace/mode/"+codeType);

        var textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.get('value'));
        
         counter.getCount(textarea.get('value'));


        $('select-file-btn').removeEvents("change");

        $('select-file-btn').addEvent('change', function (e) {
            mainThis.readLocalFile(e);
        });



        //To focus the ace editor
        // editor.focus();
        //Get the number of lines
        var count = editor.getSession().getLength();
        //Go to end of the last line
        editor.gotoLine(count, editor.getSession().getLine(count - 1).length);

        editor.getSession().on('change', function () {
            var text = editor.getSession().getValue();
            textarea.set('value', text);
            counter.getCount(text);
        });

        $('ptheme').addEvent('change', function (event) {
            editor.setTheme(this.getElement(':selected').value);
        });

        $('normalized').addEvent('click', function () {
            if (this.get('checked')) {
                var col = 80;
                editor.getSession().setUseWrapMode(true);
                editor.getSession().setWrapLimitRange(col, col);
                editor.renderer.setPrintMarginColumn(col);
            } else {
                editor.getSession().setUseWrapMode(false);
                editor.renderer.setPrintMarginColumn(80);
            }
        });


        $('fontsize').addEvent('change', function (event) {
            editor.setFontSize(this.getElement(':selected').get("value"));
        });

        $('ptype').addEvent('change', function (event) {
            editor.getSession()
                    .setMode("ace/mode/" + this.getElement(':selected').get("editCode"));
        });

        $('pprior').addEvent('change', function (event) {
            var prPreview = $('priorPreview');
            prPreview.erase('class');
            prPreview.addClass('i');
            prPreview.addClass(this.getElement(':selected').get("x-css-class-name"));
        });


        $$('.submitBtn').each(function (el, i) {

            el.addEvent('click', function (event) {
                this.getElementById('btnCaption').set('text', PasterI18n.text.notify.transmitMessage).disabled = true;
                this.getElementById('btnIcon').toggle();

                event.stop();
                mainThis.onSave();
            });
        });

    },
    clearTitle: function () {

        $('pname').set('value', '');
   },
    readLocalFile: function (e) {
        
        var file = e.target.files[0];

        if (!file) {
            return;
        }
        
        mainThis = this;
        
        var reader = new FileReader();
        reader.onload = function (e) {
            var contents = e.target.result;
            mainThis.editor.getSession().setValue(contents);
        };
        reader.readAsText(file);

   },
    onPaste: function (e) {

        if (e.event.clipboardData) {

            /*console.log(e.event.clipboardData);*/

            var text = e.event.clipboardData.getData('text/plain');

            var block = (text.length < this.max_length - 2) 
                        ? text.substring(0, text.length) : 
                                text.substring(0, this.max_length - 2) + '..';

            var ptitle = $("pname");

            if (ptitle.get('value') == '') {
                ptitle.set('value', block);
            }

            //e.stop();
        }
    },
    onSave: function () {

        var thumbImg = document.getElementById('thumbImg');

          pasterApp.takeScreenshot($('editor'), function (img) {
            thumbImg.set('value', img.src);

            $('editForm').submit();
        });


    }
});
