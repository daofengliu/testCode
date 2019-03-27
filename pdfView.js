/**
 * the PDF viewer require pdf.js
 * @see https://mozilla.github.io/pdf.js/
 * @see https://github.com/mozilla/pdf.js
 *
 * == notice ==
 * 1. Uncaught ReferenceError: pdfjsLib is not defined
 *    -> it needs to link the pdf.js (<script src=".../pdf.min.js">)
 *
 *
 * == ps ==
 * variable start with _ is reference to the HTML Element
 *
 * ex:
 *   _container -> _container = document.getElementById(elemId);
 */
// let pdfViewer = (function(window, document) {
var pdfViewer = (function(window, document) {
    pdfjsLib.disableWorker = true;
    // pdfjsLib.workerSrc = './pdf.worker.min.js';
    // pdfjsLib.cMapUrl = './cmaps';
    // pdfjsLib.cMapPacked = true;
    pdfjsLib.disableRange = true;
    // pdfjsLib.disableStream = true;


    /* PDF container */
    let _container = '';
    let _pdfMsg = '';
    let _mask = '';

    /* for multi page */
    let pdfDoc = null;
    let pageNum = 1;
    let pageRendering = false;
    let pageNumPending = null;
    let pageScale = 1.5;
    let _canvas = '';
    let _ctx = '';
    let _btnPrev = '';
    let _pageBtnNext = '';
    let _btnGoto = '';
    let _spanPageNum = '';
    let _spanPageCnt = '';
    let _iptPage = '';

    /**
     * ==== usage ====
     * == 1. ==
     * <script type="text/javascript" src=".../pdf.min.js"></script>
     * <script type="text/javascript" src=".../pdf.worker.min.js"></script>
     * <script type="text/javascript" src=".../pdfViewer.js"></script>
     *
     * == 2. ==
     * pdfViewer.load(url [, containerID] [, isPaging]);
     *
     * ex:
     *   1. only PDF URL
     *   pdfViewer.load('.../xxx.pdf');
     *
     *   2. PDF URL & containerID & isPaging
     *   <div id="pdf_container"></div
     *   pdfViewer.load('.../xxx.pdf', 'pdf_container');
     *   pdfViewer.load('.../xxx.pdf', 'pdf_container', true);
     *
     *   3. PDF URL & isPaging
     *   pdfViewer.load('.../xxx.pdf', true);
     *
     * @param url - <required>
     *   PDF URL
     *
     * @param containerId - <optional>
     *   the PDF container <div> id, ex: <div id="pdf_container"></div>
     *
     * @param isPaging - <optional>
     */
    let load = function(url, containerId, isPaging) {
        /*
         * no 3rd argument, and the type of 2nd argument is boolean
         * -> set 2nd argument as 'isPaging'
         */
        if (isPaging === undefined && typeof containerId === 'boolean') {
            isPaging = containerId;
            containerId = ''
        }
        initContainer(containerId);

        if (isPaging) {
            multiPage(url);
        } else {
            singlePage(url);
        }
    };

    /**
     * 1. initialize/create PDF container
     * 2. create PDF message element
     * 3. create blocking screen element & css
     */
    function initContainer(elemId) {
        /* 1. */
        if (!elemId) {
            elemId = 'pdf_container';
        }
        _container = document.getElementById(elemId);

        if (!_container) {
            _container = document.createElement('div');
            _container.id = 'pdf_container';
            // _container.style = 'border:1px solid black;';
            document.body.appendChild(_container);
        }

        /* 2. */
        _pdfMsg = document.createElement('div');
        _pdfMsg.style = 'color:red;';
        _container.appendChild(_pdfMsg);

        /* 3. */
        var css = document.createElement("style");
        css.type = "text/css";
        css.innerHTML =
            ".loading-overlay { " +
                "position: fixed; " +
                "top: 0; " +
                "left: 0; " +
                "width: 100%; " +
                "height: 100%; " +
                "background: rgba(0, 0, 0, .6); " +
            "} " +
            ".loading { " +
                "font-size: 20px; " +
                "text-align: center; " +
                "color: #FFF; " +
                "position: absolute; " +
                "top: 50%; " +
                "left: 0; " +
                "width: 100%; " +
            "}";
        document.body.appendChild(css);

        _mask = document.createElement('div');
        _mask.className = 'loading-overlay';
        let _maskText = document.createElement('div');
        _maskText.className = 'loading';
        _maskText.textContent = 'loading...';
        _mask.appendChild(_maskText);
        document.body.appendChild(_mask);
        blockScreen();
    }

    /**
     * PDF viewer of full page
     */
    let singlePage = function(url) {
        /* asynchronous download of PDF */
        let loadingTask = pdfjsLib.getDocument(url);

        loadingTask.promise.then(function(pdf) {
            // console.log('PDF loaded');

            /* fetch pdf pages */
            for (let pageNum = 1; pageNum < pdf.numPages; ++pageNum) {
                pdf.getPage(pageNum).then(function(page) {
                    // console.log('Page loaded');
                    let scale = 1.5;
                    let viewport = page.getViewport(scale);

                    /* prepare canvas using PDF page dimensions */
                    let canvas = document.createElement('canvas');
                    let context = canvas.getContext('2d');
                    canvas.height = viewport.height;
                    canvas.width = viewport.width;

                    /* render PDF page into canvas context */
                    var renderContext = {
                        canvasContext: context,
                        viewport: viewport
                    };
                    let renderTask = page.render(renderContext);
                    renderTask.then(function() {
                        // console.log('Page rendered');
                        _container.appendChild(canvas);

                        unBlockScreen();
                    });
                });
            }
        }, function (err) {
            setPdfViewerMsg("load PDF (" + url + ") fail!");
            unBlockScreen();
        });
    };

    /**
     * PDF viewer by paging
     *
     * 1. create paging element
     * 2. declaration as variable & bind events
     * 3. load PDF
     */
    let multiPage = function(url) {
        /* 1. */
        let containerId = _container.id;
        let canvasId = containerId + '_canvas';
        let msgId = containerId + '_msg';
        let pageHtml =
            '<div id="' + msgId + '" style="color:red;"></div>' +
            '<div>' +
                '<button id="__prev">Previous</button>&nbsp;' +
                '<button id="__next">Next</button>&nbsp;&nbsp;' +
                    '<span>Page:' +
                        '<span id="__page_num"></span> /' +
                        '<span id="__page_count"></span>' +
                    '</span>&nbsp;&nbsp;' +
                '<button id="__goto">Go To</button>&nbsp;' +
                '<input id="__ipt_page" type="text" size="3" />' +
            '</div>' +
            '<br/>' +
            '<canvas id="' + canvasId + '" style="border: 1px solid black;"></canvas>';
        _container.innerHTML += pageHtml;

        /* 2. */
        _canvas = document.getElementById(canvasId);
        _ctx = _canvas.getContext('2d');
        _btnPrev = document.getElementById('__prev');
        _pageBtnNext = document.getElementById('__next');
        _btnGoto = document.getElementById('__goto');
        _spanPageNum = document.getElementById('__page_num');
        _spanPageCnt = document.getElementById('__page_count');
        _iptPage = document.getElementById('__ipt_page');
        _pdfMsg = document.getElementById(msgId);
        _btnPrev.addEventListener('click', onPrevPage);
        _pageBtnNext.addEventListener('click', onNextPage);
        _btnGoto.addEventListener('click', toPage);

        /* 3. asynchronously downloads PDF. */
        pdfjsLib.getDocument(url).then(function (_pdfDoc) {
            pdfDoc = _pdfDoc;
            _spanPageCnt.textContent = pdfDoc.numPages;
            /* render first page */
            renderPage(pageNum);
        }, function (err) {
            setPdfViewerMsg("load PDF (" + url + ") fail!");
            unBlockScreen();
        });
    };


    /** get page info from document, resize canvas accordingly, and render page. */
    function renderPage(pageNum) {
        // blockScreen();
        pageRendering = true;

        /* using promise to fetch the page */
        pdfDoc.getPage(pageNum).then(function(page) {
            var viewport = page.getViewport(pageScale);
            _canvas.height = viewport.height;
            _canvas.width = viewport.width;

            /* render PDF page into canvas context */
            var renderContext = {
                canvasContext: _ctx,
                viewport: viewport
            };
            var renderTask = page.render(renderContext);

            /* wait for rendering to finish */
            renderTask.promise.then(function() {
                pageRendering = false;
                if (pageNumPending !== null) {
                    /* new page rendering is pending */
                    renderPage(pageNumPending);
                    pageNumPending = null;
                }

                unBlockScreen();
            });
        }, function (err) {
            setPdfViewerMsg("render page number " + pageNum + " fail!");
        });

        /* update page counters */
        _spanPageNum.textContent = pageNum;
    }

    /**
     * if another page rendering in progress, waits until the rendering is finished.
     * otherwise, executes rendering immediately.
     */
    function queueRenderPage(num) {
        if (pageRendering) {
            pageNumPending = num;
        } else {
            renderPage(num);
        }
    }

    /** displays previous page. */
    function onPrevPage() {
        if (pageNum <= 1) {
            return;
        }
        pageNum--;
        queueRenderPage(pageNum);
    }

    /** displays next page. */
    function onNextPage() {
        if (pageNum >= pdfDoc.numPages) {
            return;
        }
        pageNum++;
        queueRenderPage(pageNum);
    }

    /** go to page */
    function toPage() {
        let num = parseInt(_iptPage.value, 10);
        if (!isInt(num)) {
            alert("please input integer!");
            _iptPage.value = '';
            return false;
        }
        if (num < 1 || num > pdfDoc.numPages) {
            alert("invalid page number!");
            _iptPage.value = '';
            return;
        }
        pageNum = num;
        queueRenderPage(pageNum);
    }

    return {
        "load": load
    };

    function setPdfViewerMsg(msg) {
        // _pdfMsg.innerText = msg;
        _pdfMsg.textContent = msg;
    }
    function isInt(value) {
        return !isNaN(value) && parseInt(Number(value)) == value && !isNaN(parseInt(value, 10));
    }
    function blockScreen() {
        _mask.style.display = ''
    }
    function unBlockScreen() {
        _mask.style.display = 'none';
    }
})(window, document);
