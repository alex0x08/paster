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
  
    appInit() {
//        this.growl = new Growler.init();
        this.bindDeleteDlg(document.body);
        this.resizer = window.pica({features: ['all']
        });

    }
    showNotify(message) {
       // this.growl.notify(message);
       console.log('notify ',message)
    }
    showModal(dlg, redirectUrl, action, title, message) {

        if (title) {
            dlg.querySelector('#dialogTitle').text = title;
        }

        if (action) {
            const el = dlg.querySelector('#dialogAction');
            el.text = action;
            el.href = redirectUrl;
        }

        dlg.querySelector('#dialogMessage').innerHTML =  message;

        if (!this.modalDlg) {
            this.modalDlg = new bootstrap.Modal(dlg, { animate: false, closeOnEsc: true });
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
                        source.parentElement.querySelector('#dialogMsg').innerHTML);
                });
            });


    }
    takeScreenshot(source, onComplete) {

        var $paster = this;

        console.log('taking screenshot for ',source)
        html2canvas(source, {
                    allowTaint: true,
                    taintTest: false                 
                }).then(function(canvas) {
                    console.log('rendered..',canvas)
                    const img = document.createElement("canvas");
                    img.width = canvas.width;
                    img.height = canvas.height;

                    $paster.resizer.resize(canvas, img, {
                        quality: 3,
                        alpha: true,
                        unsharpAmount: 150,
                        unsharpRadius: 0.7,
                        unsharpThreshold: 245,
                        transferable: true
                    }, function (err) {
                         console.log( 'error on creating img', err);
                    }).then(result => {
                        console.log('resized..');
                        const img2 = Canvas2Image.convertToPNG(result, 300, 200);
    
                        onComplete(img2);
    
                    });

                 
                });

    }

};
