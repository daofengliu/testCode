/**
 * ==== 2. Callback ====
 * == 2.3. callback 補充 ==
 * _在 Java 處理非同步可以使用多執行緒
 * _而 JavaScript 是 single thread，它是使用 call stack 的方式執行程式碼
 *   _JavaScript 會先把 execution context 的內容執行完畢，接著再執行 event queue
 * _ps. 參考 Execution Context & Execution Stack
 *     1. Global Execution Context
 *     2. Functional Execution Context 
 *     3. Eval Function Execution Context 
 *
 * _若今天有一個功能為：step1 取得資料，step2 更新頁面，但是 step1 不知需要執行多久
 *   _如果使用同步，則 執行 step1 後，程式會被 block 住，待 step1 執行完畢才繼續執行之後的程式
 *   _但在 web 不應該在執行 step1 時停住，所以 step1 會改用非同步的方式執行 (使用 XMLHttpRequest, ... 非同步方式取得資料)
 *
 * _使用 callback 有兩個主要缺點：callback hell 和 Inversion of Control(控制權轉移)所造成的信任問題
 *   == 2.4.1 callback hell ==
 */
console.log("========== callback ==========");
// function step1() {
//     console.log("step1 - get data");
//     let data = null;
//     setTimeout( () => {
//             data = 'tony';
//             console.log("step1 - got data, value = " + data);
//         }, 100
//     );
//     return data;
// }
// function step2(data) {
//     console.log("step2 - update component, data = " + data);
// }
// let data = step1();
// step2(data);
// console.log("invoke other task...");
// -------------------------------------
// step1 - get data
// step2 - update component, data = null
// invoke other task...
// step1 - got data, value = tony
/****
 * 由此例子可知，javascript 的 function call 無法處理非同步
 * 執行 step1 後，可以執行其它 task，但 step1 還未完成就直接執行 step2，導致 step2 的 data 為 null
 ****/

function step1(callback) {
    console.log("step1 - get data");
    let data = null;
    /* 使用 setTimeout() 模擬取得資料花費的時間 */
    setTimeout( () => {
        data = 'tony';
        console.log("step1 - got data, value = " + data);
        callback(data);
    }, 500);
}
function step2(data) {
    console.log("step2 - update component, data = " + data);
}
step1(step2);
console.log("invoke other task");
// step1 - get data
// invoke other task
// step1 - got data, value = tony
// step2 - update component, data = tony
/****
 * 使用 callback 處理非同步，執行 step1 後，還能繼續處理其它 task，而 step1 執行完成後才接著執行 step2
 ****/


/*
 * ==== 3. Promises ====
 * The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value.
 * Starting with ECMAScript2015, JavaScript gains support for Promise objects allowing you to control the flow
 *   of deferred and asynchronous operations.
 * _Promise 物件代表一個即將完成，或失敗的非同步操作，以及它所產生的值
 * _ECMAScript2015 開始，JavaScript 支援 Promise 物件，對延遲操作或是非同步的流程控制
 *
 * == 3.1. Syntax ==
 *     new Promise( function(resolve, reject) { ... } );
 *     new Promise( (resolve, reject) => { ... } )
 *
 * The function passed to new Promise is called the executor.
 * When the promise is created, this executor function is executed immediately, passing resolve and reject functions
 *   (the executor is called before the Promise constructor even returns the created object).
 * The executor receives two arguments: resolve and reject, these functions are pre-defined by the JavaScript engine.
 * So we don’t need to create them.
 * Instead, we should write the executor to call them when ready.
 * The executor normally initiates some asynchronous work, and then, once that completes,
 *   either calls the resolve function to resolve the promise or else rejects it if an error occurred.
 * _傳入 Promise 的函式，稱為 executor
 * _executor 函式在 Promise 建構後，在 Promise 物件回傳之前，會立刻執行
 * _executor 函式會取得兩個引數，resolve 和 reject，是由 JavaScript 傳入的，無需我們自己建立
 * _需要為 executor 撰寫呼叫 resolve 或 reject
 *
 * == 3.2. Description ==
 * The resulting promise object has internal properties:
 *   1. state
 *     initially "pending", then changes to either "fulfilled" or "rejected"
 *   2. result
 *     an arbitrary value of your choosing, initially undefined
 *
 * When the executor finishes the job, it should call one of the functions that it gets as arguments:
 *   1. resolve(value) - to indicate that the job finished successfully:
 *     1.1. sets state to "fulfilled"
 *     1.2. sets result to value
 *
 *   2. reject(error) — to indicate that an error occurred:
 *     2.1. sets state to "rejected"
 *     2.2. sets result to error
 *
 * ex:
 *     let promise = new Promise(function(resolve, reject) {
 *         // ...
 *     });
 *
 *                                  resolve(value) state:  "fulfilled"
 *                               ↗                result: value
 *     <new Promise(executor)>
 *     state:  "pending"         ↘
 *     result: undefined            reject(error)  state:  "rejected"
 *                                                 result: error
 *
 * _Promise 的 executor 函式成功執行完成時
 *   1. 執行回呼函式 resolve(value)
 *   2. state 成為 fulfilled (已實現) 狀態
 *   3. value 通常為合法的 javascript 值
 *
 * _Promise 的 executor 函式發生錯誤時
 *   1. 執行回呼函式 reject(reason)
 *   2. state 成為 rejected (已實現) 狀態
 *   3. reason 通常為 Error 物件
 *
 * Promises have three possible mutually exclusive states: fulfilled, rejected, and pending.
 *   1. pending
 *     Initial state, not yet fulfilled or rejected.
 *     A promise is pending if it is neither fulfilled nor rejected.
 *     _等待中，為初始之狀態，即不是 fulfilled 也不是 rejected
 *
 *   2. fulfilled
 *     The operation completed successfully.
 *     A promise is fulfilled if promise.then(f) will call f "as soon as possible."
 *     _已實現，表示操作成功完成
 *
 *   3. rejected
 *     The operation failed.
 *     A promise is rejected if promise.then(undefined, r) will call r "as soon as possible."
 *     _已拒絕，表示操作失敗
 *
 * =============================================================================
 * There can be only a single result or an error
 * The executor should call only one resolve or one reject.
 * The promise’s state change is final.
 * All further calls of resolve and reject are ignored:
 * =============================================================================
 *
 * == 3.3. Consumers: then, catch, finally ==
 * A Promise object serves as a link between the executor and the consuming functions, which will receive the result or error.
 * Consuming functions can be registered (subscribed) using methods .then, .catch and .finally.
 *
 *   == 3.3.1 then ==
 *   The most important, fundamental one is .then
 *
 *       promise.then(
 *           function(result) { handle a successful result },
 *           function(error) { handle an error }
 *       );
 *   The 1st argument of .then is a function that runs when the Promise is resolved, and receives the result.
 *   The 2nd argument of .then is a function that runs when the Promise is rejected, and receives the error.
 *   _promise.then() 的第一個引數為函式，當 promise 為 resolved 後被執行
 *   _promise.then() 的第二個引數為函式，當 promise 為 rejected 後被執行
 *
 *   == 3.3.2 catch ==
 *   If we’re interested only in errors, then we can use null as the first argument: .then(null, errorHandlingFunction).
 *   Or we can use .catch(errorHandlingFunction), which is exactly the same
 *
 *   == 3.3.3 finally ==
 *   Just like there’s a finally clause in a regular try {...} catch {...}, there’s finally in promises.
 *   The call .finally(f) is similar to .then(f, f) in the sense that it always runs when the promise is settled: be it resolve or reject.
 *   It is a good handler to perform cleanup, e.g. to stop our loading indicators in finally, as they are not needed any more.
 *
 *     1. A finally handler has no arguments.
 *       In finally we don’t know whether the promise is successful or not.
 *       That’s all right, as our task is usually to perform "general" finalizing procedures.
 *
 *     2. Finally passes through results and errors to the next handler.
 *
 *     3. .finally(f) is a more convenient syntax than .then(f, f): no need to duplicate the function.
 *
 * As the Promise.prototype.then() and Promise.prototype.catch() methods return promises, they can be chained.
 * _由於 Promise.prototype.then() 以及 Promise.prototype.catch() 方法都回傳 新的 promise 物件，它們可以被串接使用
 *
 *                                                     async actions
 *                                    <settled>    ↗
 *                              .then(onFulfillment)
 *    <pending> ↗ <fulfill> ↗                       ↘  return  ↘  <pending>
 *     Promise                                                        Promise   --->  .then()  ---> ...
 *              ↘ <reject>  ↘        <settled>      ↗  return  ↗                  .catch()
 *                              .then(onRejection)
 *                              .catch(onRejection)↘
 *                                                     error handling
 *
 * == 3.4. Properties, Methods, Prototype ==
 *   == 3.4.1 Promise.length ==
 *   Length property whose value is always 1 (number of constructor arguments).
 *
 *   == 3.4.2. Promise.prototype ==
 *   Represents the prototype for the Promise constructor.
 *
 *   == 3.4.3. Promise.all (iterable) ==
 *   Returns a promise that either fulfills when all of the promises in the iterable argument have fulfilled
 *     or rejects as soon as one of the promises in the iterable argument rejects.
 *   If the returned promise fulfills, it is fulfilled with an array of the values from the fulfilled promises in same order as defined in the iterable.
 *   If the returned promise rejects, it is rejected with the reason from the first promise in the iterable that rejected.
 *   _回傳一個 promise，當引數 iterable 中所有 promises 都被實現時被實現，或在引數 iterable 中有一個 promise 被拒絕時立刻被拒絕
 *   _若回傳的 promise 被實現，它將以一個實現值的陣列被實現，其順序與 iterable 中的 promises 相同
 *   _若回傳的 promise 被拒絕，它將以失敗原因被拒絕，此訊息來自第一個在 iterable 中被拒絕的 promise
 *
 *   == 3.4.4. Promise.race (iterable) ==
 *   Returns a promise that fulfills or rejects as soon as one of the promises in the iterable fulfills or rejects, with the value or reason from that promise.
 *   _回傳一個被實現或拒絕的 promise，當 iterable 中有一個 promise 被實現或拒絕時
 *
 *   == 3.4.5. Promise.reject(reason) ==
 *   Returns a Promise object that is rejected with the given reason.
 *   _回傳一個以失敗訊息拒絕的 promise
 *
 *   == 3.4.6. Promise.resolve(value) ==
 *   Returns a Promise object that is resolved with the given value.
 *   If the value is a thenable (i.e. has a then method), the returned promise will "follow" that thenable,
 *     adopting its eventual state; otherwise the returned promise will be fulfilled with the value.
 *   Generally, if you don't know if a value is a promise or not, Promise.resolve(value) it instead and work with the return value as a promise.
 *   _回傳一個以 value 實現的 promise
 *   _若該值為 thenable(具有 then 方法)，回傳的 promise 將跟隨之，採用它的最終狀態； 在其他情形回傳的 promise 將以 value 被實現
 *   _一般來說在不知道 value 是否為 promise 時，使用 Promise.resolve(value)，將回傳值以 promise 作處理
 *
 *   == 3.4.7. Properties - Promise.prototype.constructor ==
 *   Returns the function that created an instance's prototype.
 *   This is the Promise function by default.
 *
 *   == 3.4.8. Promise.prototype.catch(onRejected) ==
 *   Appends a rejection handler callback to the promise, and returns a new promise resolving to the return
 *     value of the callback if it is called, or to its original fulfillment value if the promise is instead fulfilled.
 *
 *   == 3.4.9. Promise.prototype.then(onFulfilled, onRejected) ==
 *   Appends fulfillment and rejection handlers to the promise, and returns a new promise resolving to the
 *     return value of the called handler, or to its original settled value if the promise was not handled
 *     (i.e. if the relevant handler onFulfilled or onRejected is not a function).
 *
 * ==== 3.5. 補充 ====
 * https://eyesofkids.gitbooks.io/javascript-start-es6-promise/content/
 *
 * _Promise 這種異步執行結構的需求，在伺服器端(Node.js)遠遠大於瀏覽器端，因為瀏覽器通常只有會有一個使用者在操作
 *   _而且除了網路連線要求之類的 I/O(例如 AJAX)、DOM 事件處理、動畫流程處理(計時器)外，並沒有太多需要異步執行 I/O 處理的情況
 * _伺服器端(Node.js)所面臨到情況就很嚴峻，除了與外部資源 I/O 的處理情況到處都有之外，而且伺服器端(Node.js)是需要同時服務多人使用的情況
 *
 * _Promise 是一個強大的異步執行流程語法結構，在 ES6 Promise 標準中，實作內容只有一個建構函式與一個then方法、一個catch方法
 *   _再加上四個必需以 Promise 關鍵字呼叫的靜態函式
 *     Promise.resolve
 *     Promise.reject
 *     Promise.all
 *     Promise.race
 * _Promise 另一個會受到重視的原因，是因為新式的 HTML5 中的 Fetch API(用於取代舊有的 AJAX 語法結構)也是使用它
 *
 * _Promise 最大的效益在於它改變了異步處理的程式碼結構 (避免 callback hell)，而不在於增加了多少效能，以原本的 callback 結構(CPS 風格)與 Promise 相比來說：
 *   1. callback 是一種函式，傳入到另一個函式作為傳入參數值
 *   2. Promise 是一種函式的物件(代理 Proxy)，代表未決定的狀態值
 *
 */
setTimeout( () => console.log("\n========== promise =========="), 1000);
let promiseB11 = new Promise( (resolve, reject) => {
    /**** 使用 setTimeout() 模擬非同步執行處理邏輯 ****/
    setTimeout( () => {
        let isSuccess = true;
        if (isSuccess) {
            resolve('success');
            /***
             * 執行 resolve() 後，chrome DevTool 觀查 promiseB11 狀態
             *   [[PromiseStatus]]: "resolved"
             *   [[PromiseValue]]: "success"
             ****/
        } else {
            reject(new Error('fail'));
            /***
             * 執行 reject() 後，chrome DevTool 觀查 promiseB11 狀態
             *   [[PromiseStatus]]: "rejected"
             *   [[PromiseValue]]:  Error: fail at setTimeout
             ****/
        }

        /**** 已執行過 resolve() 或 reject()，所以這裡會忽略不執行 ****/
        resolve('success x2');
    }, 100);
});
/****
 * 執行 resolve() 或 reject() 之前，promiseB11 的狀態為
 *   [[PromiseStatus]]: "pending"
 *   [[PromiseValue]]:  undefined
 ****/

setTimeout( () => console.log("\n========== promise.then() =========="), 1100);
function funcB12() {
    return new Promise((resolve, reject) => {
        setTimeout( () => { resolve("funcB12() done"); }, 1200);
    });
}
// funcB12().then(
//     function(result) { console.log("funcB12, promise result = " + result); },
//     function(error) { console.log("funcB12, promise error = " + error); }
// );
funcB12().then(
    result => console.log("funcB12, promise result = " + result),
    error => console.log("funcB12, promise error = " + error)
); // funcB12, promise result = funcB12() done
/*----------------------------------------------------------------------------*/
function funcB13() {
    return new Promise((resolve, reject) => {
        setTimeout( () => { reject("funcB13() whoops"); }, 1300);
    });
}
// funcB13().then(
//     function(result) { console.log("funcB13, promise result = " + result); },
//     function(error) { console.log("funcB13, promise error = " + error); }
// );
funcB13().then(
    result => console.log("funcB13, promise result = " + result),
    error => console.log("funcB13, promise error = " + error)
); // funcB13, promise error = funcB13() whoops
/*----------------------------------------------------------------------------*/
let promiseB14 = new Promise(resolve => {
    setTimeout(() => resolve("promiseB14, done!"), 1400);
});
promiseB14.then(console.log); // promiseB14, done!
/**** 如果只需要處理 success，可以只傳入一個參數(該參數需為函式)給 then() ****/


setTimeout( () => console.log("\n========== promise.catch() =========="), 2000);
function funcB22() {
    return new Promise((resolve, reject) => {
        setTimeout( () => { reject("funcB22() whoops"); }, 2100);
    });
}
funcB22().catch(console.log); // funcB22() whoops


setTimeout( () => console.log("\n========== promise.finally() =========="), 3000);
let promiseB31 = new Promise((resolve, reject) => {
    setTimeout( () => { resolve("promiseB31 done"); }, 3120);
    setTimeout( () => { reject("promiseB31 fail"); }, 3110);
});
promiseB31
    .finally( () => console.log("promiseB31.then() finally") )
    .then(
        result => console.log("promiseB31.then() result = " + result),
        error => console.log("promiseB31.then() error = " + error)
    );
// promiseB31.then() finally
// promiseB31.then() error = promiseB31 fail
/**** promiseB31 的 reject 會先被執行，resolve 會被忽略不執行 ****/




//https://javascript.info/promise-basics
//https://eyesofkids.gitbooks.io/javascript-start-es6-promise/content/
//https://dotblogs.com.tw/wasichris/2017/08/15/021114
//https://wcc723.github.io/life/2017/05/25/promise/

// https://javascript.info/

/*
 * == 改寫回呼函式 ==
 * function(err, response) { ... }
 * -> 改用 Promise 的 .then()
 *
 * .then(function(response) { ... }).catch(function(err) { ... })
 *
 *
 * async1(function(){
 *     async2(function(){
 *         async3(function(){
 *             //....
 *         });
 *     });
 * });
 * -> 改為 Promise
 *
 * var task1 = async1();
 * var task2 = task1.then(async2);
 * var task3 = task2.then(async3);
 * task3.catch(function(){
 *     // 處理 task1, task2, task3 的例外
 * })
 *
 * -> 使用 Promise 連鎖語法
 *
 * async1(function(){..})
 *     .then(async2)
 *     .then(async3)
 *     .catch(function(){
 *         // 處理例外
 *     })
 */
