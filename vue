<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Vue component</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/vue/2.6.10/vue.js"></script>
    <!--<script src="https://cdnjs.cloudflare.com/ajax/libs/vue/2.6.10/vue.min.js"></script>-->
</head>
<body>

<!--
_前兩個 button 點擊後，對應的 span 內的次數都增加了，因為使用相同的 Vue data 屬性 count
_要讓兩個計數器各自顯示，則在 Vue instance 的 data 屬性，必須宣告 count1, count2
_若要再新增更多計數器時，則也必須在 Vue instance 的 data 屬性，宣告更多的 countN...
-->
<div id="myApp1">
    <input type="button" value="click-1" v-on:click="count++" />
    <span>click-1 had clicked {{count}} times.</span>
    <br/>
    <input type="button" value="click-2" v-on:click="count++" />
    <span>click-2 had clicked {{count}} times.</span>
    <br/>
    <input type="button" value="click-3" v-on:click="count2++" />
    <span>click-3 had clicked {{count2}} times.</span>
</div>
<script>
    let vm1 = new Vue({
        el: '#myApp1',
        data: {
            count: 0,
            count2: 0
        }
    });
</script>

<hr/>
<h2>Vue component</h2>
<!--
[Vue 錯誤訊息]: Component template should contain exactly one root element.
               If you are using v-if on multiple elements, use v-else-if to chain them instead.
_Vue 的 template 只允許一個 element，解法為使用一個 <div> 包住
--------------------------------------------------------------------------------
<script type="text/x-template" id="counter-template">
    <input type="button" value="click" v-on:click="count++" />
    <span>click-{{idx}} had clicked {{count}} times.</span>
</script>
--------------------------------------------------------------------------------

_上面的 template 為 X-Templates

_Vue 有七種定義 component template 的方式
  1. Strings
  2. Template Literals
  3. X-Templates
  4. Inline Templates
  5. Render Functions
  6. JSX
  7. Single File Components
-->
<script type="text/x-template" id="counter-template">
    <div>
        <input type="button" value="click" v-on:click="count++" />
        <span>click-{{idx}} had clicked {{count}} times.</span>
    </div>
</script>


<div id="myApp2">
    <!--
    Unknown custom element: <count-component> - did you register the component correctly?
    _未註冊的 Vue component tag
    -->
    <!--<count-component></count-component>-->

    <counter-component></counter-component>
</div>
<hr/>

<h4>Reusing Components</h4>
<div id="myApp3">
    <counter-component></counter-component>
    <counter-component></counter-component>
    <counter-component></counter-component>
</div>

<script>
/**
 * ==== Components Basics ====
 * Components are reusable Vue instances with a name.
 * Since components are reusable Vue instances, they accept the same options as new Vue, such as data, computed, watch, methods, and lifecycle hooks.
 * The only exceptions are a few root-specific options like el.
 * _component 是可以重用的 Vue instance
 * _因為 component 也是 Vue instance，所以和 new Vue() 接受相同的參數
 * _除了某些 root 層級的參數，例如 "el"
 *
 *   == data Must Be a Function ==
 *   A component’s data option must be a function, so that each instance can maintain an independent copy of the returned data object
 *   _在 Vue Instance 中，data 可以是 object 或 function，但 component 的 data 只能是 function，這是因為元件內會各自擁有自己的 data，而非共用的關係
 *   _每個 component 會有一份各自的 data 物件
 */
Vue.component('counter-component', {
    template: '#counter-template',
    /*
     * [Vue 錯誤訊息]: The "data" option should be a function that returns a per-instance value in component definitions.
     * _Vue 的 component 必須維護自己的資料，所以 component 的 data 屬性只能是函式，不能是物件
     * _Vue instance 才能是物件
     */
    // data: {
    //     idx: 0,
    //     count: 0
    // }
    data: function () {
        return {
            idx: 0,
            count: 0
        }
    }
});

let vm2 = new Vue({
    el: '#myApp2'
});

let vm3 = new Vue({
    el: '#myApp3'
});

/**
 * ==== Organizing Components ====
 * To use these components in templates, they must be registered so that Vue knows about them.
 * There are two types of component registration: global and local.
 * _要在 template 使用 component，必須先註冊 component 讓 Vue 能識別
 * _有兩種註冊類型： global 和 local
 *
 *   == Global Registration ==
 *   Globally registered components can be used in the template of any root Vue instance (new Vue) created afterwards
 *     and even inside all sub-components of that Vue instance’s component tree.
 *
 *       Vue.component(${component-name}, { options })
 *
 *   == Local Registration ==
 *
 *       var componentA = { ... }
 *       var componentB = { ... }
 *
 *       new Vue({
 *           el: '#app',
 *           components: {
 *               'component-a': componentA,
 *               'component-b': componentB
 *           }
 *       })
 *
 *   For each property in the components object, the key will be the name of the custom element,
 *     while the value will contain the options object for the component.
 *
 *   == Passing Data to Child Components with Props ==
 *
 *       Vue.component('blog-post', {
 *           props: ['title'],
 *           template: '<h3>{{title}}</h3>'
 *       })
 *
 *       <blog-post title="My journey with Vue"></blog-post>
 *       <blog-post title="Blogging with Vue"></blog-post>
 *       <blog-post title="Why Vue is so fun"></blog-post>
 */
</script>

</body>
</html>
