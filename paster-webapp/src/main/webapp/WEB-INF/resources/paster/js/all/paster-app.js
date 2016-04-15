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


var PasterApp = new Class({
    initialize: function () {
        //this.age = age;

        this.growl = undefined;

        this.modalDlg = undefined;



    },
    appInit: function () {
        this.growl = new Growler.init();
        this.bindDeleteDlg(document.body);

    },
    showNotify: function (message) {
        this.growl.notify(message);
    },
    showModal: function (dlg, redirectUrl, action, title, message) {

        if (title !== null) {
            dlg.getElementById('dialogTitle').set('text', title);
        }

        if (action !== null) {
            dlg.getElementById('dialogAction').set('text', action).set('href', redirectUrl);
        }

        dlg.getElementById('dialogMessage').set('html', message);

        if (!this.modalDlg) {
            this.modalDlg = new Bootstrap.Popup(dlg, {animate: false, closeOnEsc: true});
        }
        this.modalDlg.show();
    },
    bindDeleteDlg: function (parent) {

        var $paster = this;

        parent.getElements('.deleteBtn')
                .each(function (el, i) {
                    el.addEvent("click", function (e) {
                        e.stop();
                        var source = e.target || e.srcElement;
                        $paster.showModal($('deletePopup'), source.parentElement.href,
                                PasterI18n.text.dialog.removal.title,
                                PasterI18n.text.dialog.removal.message,
                                source.parentElement.getElementById('dialogMsg').innerHTML);
                    });
                });


    }

});
      