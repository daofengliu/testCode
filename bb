/* 
 * ==== 2. Promises ====
 * The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value.
 * Starting with ECMAScript2015, JavaScript gains support for Promise objects allowing you to control the flow
 *   of deferred and asynchronous operations.
 * _Promise 物件代表一個即將完成，或失敗的非同步操作，以及它所產生的值
 * _ECMAScript2015 開始，JavaScript 支援 Promise 物件，對延遲操作或是非同步的流程控制
 *
 * == 2.1. Syntax ==
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
 * == 2.2. Description ==
 * A Promise is a proxy for a value not necessarily known when the promise is created.
 * It allows you to associate handlers with an asynchronous action's eventual success value or failure reason.
 * This lets asynchronous methods return values like synchronous methods: instead of immediately returning the
 *   final value, the asynchronous method returns a promise to supply the value at some point in the future.
 * _Promise 是一個，建立時不需知道它的值，的代理物件
 * _允許繫結一個，非同步處理後的成功值(success value)，或失敗原因(failure reason)的處理函式
 * _這個非同步方法回傳值的方式，很像同步方法，但它不是回傳最後結果，而是回傳一個 promise 物件，做為未來某個時間點的值
 *
 * Promises have three possible mutually exclusive states: fulfilled, rejected, and pending.
 *   == 2.2.1 pending ==
 *   Initial state, not yet fulfilled or rejected.
 *   A promise is pending if it is neither fulfilled nor rejected.
 *   _等待中，為初始之狀態，即不是 fulfilled 也不是 rejected
 *
 *   == 2.2.2 fulfilled ==
 *   The operation completed successfully.
 *   A promise is fulfilled if promise.then(f) will call f "as soon as possible."
 *   _已實現，表示操作成功完成
 *
 *   == 2.2.3. rejected: ==
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
let varB11 = new Promise(function(resolve, reject) {
    // the function is executed automatically when the promise is constructed

    // after 1 second signal that the job is done with the result "done!"
    setTimeout(() => resolve("done!"), 1000);
});

https://javascript.info/promise-basics
https://dotblogs.com.tw/wasichris/2017/08/15/021114
