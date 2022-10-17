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

                elSpinner.insertAdjacentHTML('afterEnd', newPage);
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