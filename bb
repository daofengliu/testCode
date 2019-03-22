<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>asynchronous</title>
</head>
<body>

    <input type="button" id="btnA" value="Button A" />

</body>

<script type="text/javascript">
/**
 * https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous/Introducing
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises
 * Eloquent JavaScript 3rd edition Chapter 11 Asynchronous Programming
 * Exploring ES6 24. Asynchronous programming
 *
 * ==== 1. Asynchronous ====
 * In a synchronous programming model, things happen one at a time.
 * When you call a function that performs a long-running action, it returns only when the action has finished and it can return the result.
 * This stops your program for the time the action takes.
 * _在同步的執行環境，程式同一時間只做一件事
 * _當呼叫一個函式執行長時間任務，一直到完成後才會回傳結果，然後程式才結束
 *
 * An asynchronous model allows multiple things to happen at the same time.
 * When you start an action, your program continues to run.
 * When the action finishes, the program is informed and gets access to the result (for example, the data read from disk).
 * _非同步的執行環境，允許同一時間做多件事
 *
 * In the following diagram, the thick lines represent time the program spends running normally, and the thin lines represent time spent waiting for the network.
 * In the synchronous model, the time taken by the network is part of the timeline for a given thread of control.
 * In the asynchronous model, starting a network action conceptually causes a split in the timeline.
 * The program that initiated the action continues running, and the action happens alongside it, notifying the program when it is finished.
 * _下面的圖示，雙線表示程式執行花費的時間，單線表示等待網路的時間
 * _在 synchronous 範例，
 *
 *   1. synchronous, single thread of control
 *   ==O----------==O--------------------====
 *
 *   2. synchronous, two threads of control
 *   ==O------    ====
 *   ==O----------==
 *
 *   3. asynchronous
 *        |--------------|
 *   ==O==       ==      =====
 *     |---------|
 *
 * _JavaScript 引擎是單執行緒執行的,瀏覽器無論在什麼時候都只且只有一個執行緒在執行 js 程式
 * _瀏覽器的核心是多執行緒的，它們在核心控制下相互配合以保持同步，一個瀏覽器至少實現三個常駐執行緒： JavaScript引擎執行緒, GUI渲染執行緒, 瀏覽器事件觸發執行緒
 *   1. _JavaScript 引擎是基於事件驅動單執行緒執行的, JavaScript 引擎一直等待著任務佇列中任務的到來，然後加以處理
 *      _瀏覽器無論什麼時候都只有一個 JavaScript 執行緒在執行 JavaScript 程式
 *   2. _GUI渲染執行緒負責渲染瀏覽器介面,當介面需要重繪(Repaint)或由於某種操作引發迴流(Reflow)時，該執行緒就會執行
 *      _但需要注意，GUI 渲染執行緒與 JavaScript 引擎是互斥的，當 JavaScript 引擎執行時 GUI 執行緒會被掛起，GUI更新會被儲存在一個佇列中等到 JavaScript 引擎空閒時立即被執行
 *   3. _事件觸發執行緒，當一個事件被觸發時該執行緒會把事件新增到待處理佇列的隊尾，等待 JavaScript 引擎的處理
 *      _這些事件可來自 JavaScript 引擎當前執行的程式碼塊如 setTimeout、也可來自瀏覽器核心的其他執行緒如滑鼠點選、Ajax 非同步請求等
 *      _但由於 JavaScript 的單執行緒關係所有這些事件都得排隊等待 JavaScript 引擎處理(當執行緒中沒有執行任何同步程式碼的前提下才會執行非同步程式碼)
 *
 *
 * Two common patterns for receiving results asynchronously are: events and callbacks.
 * _ES 6 之前，有兩種非同步取得執行結果的方式，events 和 callbacks
 *
 * == 1.1. asynchronous results via events ==
 * In this pattern for asynchronously receiving results, you create an object for each request
 *   and register event handlers with it: one for a successful computation, another one for handling errors.
 * _非同步使用 event，會在建立的物件，註冊處理成功，和失敗的 event handler
 *
 * == 1.2. Asynchronous results via callbacks ==
 * If you handle asynchronous results via callbacks, you pass callback functions as trailing parameters to asynchronous function or method calls.
 * Async callbacks are functions that are passed as parameters to other functions to be executed when a previous operation has returned.
 * _回呼函式，傳入另一個函式的參數，當該函式執行完成後，此回呼函式接著被執行
 *
 *
 */
console.log("\n========== asynchronous results via events & callbacks ==========");
/**** reqA1 註冊 onload 和 onerror 的 event handler ****/
let reqA1 = new XMLHttpRequest();
reqA1.open('GET', 'http://worldclockapi.com/api/json/est/now');
reqA1.onload = function() {
    if (reqA1.status == 200) {
        // {"$id":"1","currentDateTime":"2019-03-22T04:52-04:00","utcOffset":"-04:00:00","isDayLightSavingsTime":true,"dayOfTheWeek":"Friday","timeZoneName":"Eastern Standard Time","currentFileTime":131977039536639261,"ordinalDate":"2019-81","serviceResponse":null}
        console.log("reqA1, now is " + JSON.parse(reqA1.response).currentDateTime);
    } else {
        console.log('reqA1, ERROR', reqA1.statusText);
    }
};
reqA1.onerror = function() { console.log('reqAq, Network Error'); };
/* Add request to task queue */
reqA1.send();

/**** arrow function 做為 setTimeout() 的參數，在 setTimeout() 執行完後，接著被執行 ****/
function funcA11() {
    console.log("funcA11() invoked");
    setTimeout( () => {
        console.log("setTimeout() callback invoked");
    }, 100);
}
funcA11(); // funcA11() invoked
           // setTimeout() callback invoked

/**** arrow function 做為 addEventListener() 的參數，在 button 觸發 click event 後，接著被執行 ****/
let btnA1 = document.getElementById("btnA");
btnA1.addEventListener('click', () => {
    alert('click Button A');
    let pElem = document.createElement('p');
    pElem.textContent = 'This is a newly-added paragraph.';
    document.body.appendChild(pElem);
});


// Output: A E B D C

function funcA12(input, callback) {
    setTimeout(function() {
        callback(input);
    }, 100);
}
console.log('A');
funcA12('B',
    function callbackA121(result2) {
        console.log(result2);
        funcA12('C',
            function callbackA122(result3) {
                console.log(result3);
            }
        );
        console.log('D');
    }
);
console.log('E');
// A E B D C

https://ithelp.ithome.com.tw/articles/10192739
http://eloquentjavascript.net/11_async.html
http://exploringjs.com/es6/ch_async.html


/* ==== 1. Promises ====
 * The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value.
 * Starting with ECMAScript2015, JavaScript gains support for Promise objects allowing you to control the flow
 *   of deferred and asynchronous operations.
 * _Promise 物件代表一個即將完成，或失敗的非同步操作，以及它所產生的值
 * _ECMAScript2015 開始，JavaScript 支援 Promise 物件，對延遲操作或是非同步的流程控制
 *
 * == 1.1. Syntax ==
 *   new Promise( /* executor * / function(resolve, reject) { ... } );
 *
 *   @param executor
 *     A function that is passed with the arguments resolve and reject.
 *     The executor function is executed immediately by the Promise implementation, passing resolve and
 *       reject functions (the executor is called before the Promise constructor even returns the created object).
 *     The resolve and reject functions, when called, resolve or reject the promise, respectively.
 *     The executor normally initiates some asynchronous work, and then, once that completes, either calls the
 *       resolve function to resolve the promise or else rejects it if an error occurred.
 *     _一個有 resolve 和 reject 參數的函式
 *     _這個 executor 函式會在 Promise 建構式回傳物件"之前"執行，並且會傳入 resolve 和 reject 函式
 *
 *     If an error is thrown in the executor function, the promise is rejected.
 *     The return value of the executor is ignored.
 *     _在 executor 函式執行時若發生錯誤，Promise 物件會是 rejected 狀態
 *     _同時 executor 函式的回傳值會被忽略
 *
 * == 1.2. Description ==
 * A Promise is a proxy for a value not necessarily known when the promise is created.
 * It allows you to associate handlers with an asynchronous action's eventual success value or failure reason.
 * This lets asynchronous methods return values like synchronous methods: instead of immediately returning the
 *   final value, the asynchronous method returns a promise to supply the value at some point in the future.
 * _Promise 是一個，建立時不需知道它的值，的代理物件
 * _允許繫結一個，非同步處理後的成功值(success value)，或失敗原因(failure reason)的處理函式
 * _這個非同步方法回傳值的方式，很像同步方法，但它不是回傳最後結果，而是回傳一個 promise 物件，做為未來某個時間點的值
 *
 * Promises have three possible mutually exclusive states: fulfilled, rejected, and pending.
 *   == 1.2.1 pending ==
 *   Initial state, not yet fulfilled or rejected.
 *   A promise is pending if it is neither fulfilled nor rejected.
 *   _等待中，為初始之狀態，即不是 fulfilled 也不是 rejected
 *
 *   == 1.2.2 fulfilled ==
 *   The operation completed successfully.
 *   A promise is fulfilled if promise.then(f) will call f "as soon as possible."
 *   _已實現，表示操作成功完成
 *
 *   == 1.2.3. rejected: ==
 *   The operation failed.
 *   A promise is rejected if promise.then(undefined, r) will call r "as soon as possible."
 *   _已拒絕，表示操作失敗
 *
 * A pending promise can either be fulfilled with a value, or rejected with a reason (error).
 * When either of these options happen, the associated handlers queued up by a promise's then method are called.
 * (If the promise has already been fulfilled or rejected when a corresponding handler is attached, the handler
 *   will be called, so there is no race condition between an asynchronous operation completing and its handlers being attached.)
 * _一個處於擱置狀態的 promise 物件能以一個值被實現(fulfilled)，或是以一個原因或錯誤而被拒絕(rejected)
 * _當上述任一狀態發生時，那些透過 then 方法繫結的處理函式佇列就會依序被執行
 * _若一個 promise 已被實現或拒絕，繫結(attached)於它的處理函式將立即被呼叫，因此完成非同步操作與繫結處理函式之間不存在競爭條件
 *
 * As the Promise.prototype.then() and Promise.prototype.catch() methods return promises, they can be chained.
 * _由於 Promise.prototype.then() 以及 Promise.prototype.catch() 方法都回傳 promise，它們可以被串接。
 *
 *                                                     async actions
 *                                    <settled>    ↗
 *                              .then(onFulfillment)
 *    <pending> ↗ [fulfill] ↗                       ↘  [return] ↘  <pending>
 *     Promise                                                        Promise   --->  .then()  ---> ...
 *              ↘ [reject]  ↘                       ↗  [return] ↗                  .catch()
 *                              .then(onRejection)
 *                              .catch(onRejection)↘
 *                                                     error handling
 *
 * == 1.3. Properties ==
 *   == 1.3.1 Promise.length ==
 *   Length property whose value is always 1 (number of constructor arguments).
 *
 *   == 1.3.2. Promise.prototype ==
 *   Represents the prototype for the Promise constructor.
 *
 * == 1.4. Methods ==
 *   == 1.4.1. Promise.all (iterable) ==
 *   Returns a promise that either fulfills when all of the promises in the iterable argument have fulfilled
 *     or rejects as soon as one of the promises in the iterable argument rejects.
 *   If the returned promise fulfills, it is fulfilled with an array of the values from the fulfilled promises in same order as defined in the iterable.
 *   If the returned promise rejects, it is rejected with the reason from the first promise in the iterable that rejected.
 *   _回傳一個 promise，當引數 iterable 中所有 promises 都被實現時被實現，或在引數 iterable 中有一個 promise 被拒絕時立刻被拒絕
 *   _若回傳的 promise 被實現，它將以一個實現值的陣列被實現，其順序與 iterable 中的 promises 相同
 *   _若回傳的 promise 被拒絕，它將以失敗原因被拒絕，此訊息來自第一個在 iterable 中被拒絕的 promise
 *
 *   == 1.4.2. Promise.race (iterable) ==
 *   Returns a promise that fulfills or rejects as soon as one of the promises in the iterable fulfills or rejects, with the value or reason from that promise.
 *   _回傳一個被實現或拒絕的 promise，當 iterable 中有一個 promise 被實現或拒絕時
 *
 *   == 1.4.3. Promise.reject(reason) ==
 *   Returns a Promise object that is rejected with the given reason.
 *   _回傳一個以失敗訊息拒絕的 promise
 *
 *   == 1.4.4. Promise.resolve(value) ==
 *   Returns a Promise object that is resolved with the given value.
 *   If the value is a thenable (i.e. has a then method), the returned promise will "follow" that thenable,
 *     adopting its eventual state; otherwise the returned promise will be fulfilled with the value.
 *   Generally, if you don't know if a value is a promise or not, Promise.resolve(value) it instead and work with the return value as a promise.
 *   _回傳一個以 value 實現的 promise
 *   _若該值為 thenable(具有 then 方法)，回傳的 promise 將跟隨之，採用它的最終狀態； 在其他情形回傳的 promise 將以 value 被實現
 *   _一般來說在不知道 value 是否為 promise 時，使用 Promise.resolve(value)，將回傳值以 promise 作處理
 *
 * == 1.5. Promise prototype ==
 *   == 1.5.1. Properties - Promise.prototype.constructor ==
 *   Returns the function that created an instance's prototype.
 *   This is the Promise function by default.
 *
 *   == 1.5.2. Methods ==
 *     == 1.5.2.1 Promise.prototype.catch(onRejected) ==
 *     Appends a rejection handler callback to the promise, and returns a new promise resolving to the return
 *       value of the callback if it is called, or to its original fulfillment value if the promise is instead fulfilled.
 *
 *     == 1.5.2.2. Promise.prototype.then(onFulfilled, onRejected) ==
 *     Appends fulfillment and rejection handlers to the promise, and returns a new promise resolving to the
 *       return value of the called handler, or to its original settled value if the promise was not handled
 *       (i.e. if the relevant handler onFulfilled or onRejected is not a function).
 *
 */
</script>

</html>
