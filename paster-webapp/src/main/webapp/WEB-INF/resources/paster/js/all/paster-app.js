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


class PasterApp {
    initialize() {
        this.growl = undefined;
        this.modalDlg = undefined;
    }
    appInit() {
        this.growl = new Growler.init();
        this.bindDeleteDlg(document.body);

    }
    showNotify(message) {
        this.growl.notify(message);
    }
    showModal(dlg, redirectUrl, action, title, message) {

        if (title !== null) {
            dlg.getElementById('dialogTitle').set('text', title);
        }

        if (action !== null) {
            dlg.getElementById('dialogAction').set('text', action).set('href', redirectUrl);
        }

        dlg.getElementById('dialogMessage').set('html', message);

        if (!this.modalDlg) {
            this.modalDlg = new Bootstrap.Popup(dlg, { animate: false, closeOnEsc: true });
        }
        this.modalDlg.show();
    }
    bindDeleteDlg(parent) {

        var $paster = this;


        Array.from(parent.getElementsByClassName('deleteBtn')).forEach(
            function (el, i, array) {
                el.addEventListener("click", function (e) {
                    e.preventDefault();
                    var source = e.target || e.srcElement;
                    $paster.showModal(document.getElementById('deletePopup'), source.parentElement.href,
                        PasterI18n.text.dialog.removal.title,
                        PasterI18n.text.dialog.removal.message,
                        source.parentElement.getElementById('dialogMsg').innerHTML);
                });
            });


    }
    takeScreenshot(source, onComplete) {

        html2canvas(source, {
            allowTaint: true,
            taintTest: false,
            onrendered: function (canvas) {
                var img = document.createElement("canvas");
                img.width = canvas.width;
                img.height = canvas.height;

                window.pica.resizeCanvas(canvas, img, {
                    quality: 3,
                    alpha: true,
                    unsharpAmount: 150,
                    unsharpRadius: 0.7,
                    unsharpThreshold: 245,
                    transferable: true
                }, function (err) {
                    // console.log(err);
                });

                const img2 = Canvas2Image.saveAsPNG(img, true, 300, 200);
                onComplete(img2);
            }
        });
    }

};
