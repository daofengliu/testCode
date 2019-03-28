/*
 * ==== 2. Promises ====
 * The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value.
 * Starting with ECMAScript2015, JavaScript gains support for Promise objects allowing you to control the flow
 *   of deferred and asynchronous operations.
 * _Promise 物件代表一個即將完成，或失敗的非同步操作，以及它所產生的值
 * _ECMAScript2015 開始，JavaScript 支援 Promise 物件，對延遲操作或是非同步的流程控制
 *
 * == 2.1. Syntax ==
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
 * == 2.2. Description ==
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
 *
 * == 2.3. Consumers: then, catch, finally ==
 * A Promise object serves as a link between the executor and the consuming functions, which will receive the result or error.
 * Consuming functions can be registered (subscribed) using methods .then, .catch and .finally.
 *
 *   == 2.3.1 then ==
 *     promise.then(
 *         function(result) { handle a successful result },
 *         function(error) { handle an error }
 *     );
 *   The 1st argument of .then is a function that runs when the Promise is resolved, and receives the result.
 *   The 2nd argument of .then is a function that runs when the Promise is rejected, and receives the error.
 *   _promise.then() 的第一個引數為函式，當 promise 為 resolved 後被執行
 *   _promise.then() 的第二個引數為函式，當 promise 為 rejected 後被執行
 *
 *   == 2.3.2 catch ==
 *   If we’re interested only in errors, then we can use null as the first argument: .then(null, errorHandlingFunction).
 *   Or we can use .catch(errorHandlingFunction), which is exactly the same
 *
 *   == 2.3.3 finally ==
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
 *
 *
 *
 * As the Promise.prototype.then() and Promise.prototype.catch() methods return promises, they can be chained.
 * _由於 Promise.prototype.then() 以及 Promise.prototype.catch() 方法都回傳 promise，它們可以被串接
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
 * == 2.3. Properties ==
 *   == 2.3.1 Promise.length ==
 *   Length property whose value is always 1 (number of constructor arguments).
 *
 *   == 2.3.2. Promise.prototype ==
 *   Represents the prototype for the Promise constructor.
 *
 * == 2.4. Methods ==
 *   == 2.4.1. Promise.all (iterable) ==
 *   Returns a promise that either fulfills when all of the promises in the iterable argument have fulfilled
 *     or rejects as soon as one of the promises in the iterable argument rejects.
 *   If the returned promise fulfills, it is fulfilled with an array of the values from the fulfilled promises in same order as defined in the iterable.
 *   If the returned promise rejects, it is rejected with the reason from the first promise in the iterable that rejected.
 *   _回傳一個 promise，當引數 iterable 中所有 promises 都被實現時被實現，或在引數 iterable 中有一個 promise 被拒絕時立刻被拒絕
 *   _若回傳的 promise 被實現，它將以一個實現值的陣列被實現，其順序與 iterable 中的 promises 相同
 *   _若回傳的 promise 被拒絕，它將以失敗原因被拒絕，此訊息來自第一個在 iterable 中被拒絕的 promise
 *
 *   == 2.4.2. Promise.race (iterable) ==
 *   Returns a promise that fulfills or rejects as soon as one of the promises in the iterable fulfills or rejects, with the value or reason from that promise.
 *   _回傳一個被實現或拒絕的 promise，當 iterable 中有一個 promise 被實現或拒絕時
 *
 *   == 2.4.3. Promise.reject(reason) ==
 *   Returns a Promise object that is rejected with the given reason.
 *   _回傳一個以失敗訊息拒絕的 promise
 *
 *   == 2.4.4. Promise.resolve(value) ==
 *   Returns a Promise object that is resolved with the given value.
 *   If the value is a thenable (i.e. has a then method), the returned promise will "follow" that thenable,
 *     adopting its eventual state; otherwise the returned promise will be fulfilled with the value.
 *   Generally, if you don't know if a value is a promise or not, Promise.resolve(value) it instead and work with the return value as a promise.
 *   _回傳一個以 value 實現的 promise
 *   _若該值為 thenable(具有 then 方法)，回傳的 promise 將跟隨之，採用它的最終狀態； 在其他情形回傳的 promise 將以 value 被實現
 *   _一般來說在不知道 value 是否為 promise 時，使用 Promise.resolve(value)，將回傳值以 promise 作處理
 *
 * == 2.5. Promise prototype ==
 *   == 1.5.1. Properties - Promise.prototype.constructor ==
 *   Returns the function that created an instance's prototype.
 *   This is the Promise function by default.
 *
 *   == 2.5.2. Methods ==
 *     == 2.5.2.1 Promise.prototype.catch(onRejected) ==
 *     Appends a rejection handler callback to the promise, and returns a new promise resolving to the return
 *       value of the callback if it is called, or to its original fulfillment value if the promise is instead fulfilled.
 *
 *     == 2.5.2.2. Promise.prototype.then(onFulfilled, onRejected) ==
 *     Appends fulfillment and rejection handlers to the promise, and returns a new promise resolving to the
 *       return value of the called handler, or to its original settled value if the promise was not handled
 *       (i.e. if the relevant handler onFulfilled or onRejected is not a function).
 */
console.log("\n========== promise ==========");
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

setTimeout( () => console.log("\n========== promise.then() =========="), 1000);
function funcB12() {
    return new Promise((resolve, reject) => {
        setTimeout( () => { resolve("funcB12() done"); }, 1100);
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
        setTimeout( () => { reject("funcB13() whoops"); }, 1200);
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
    setTimeout(() => resolve("promiseB14, done!"), 1300);
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
