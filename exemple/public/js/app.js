var components = MageryCompiler.compile('template');

// create a store
var store = Redux.createStore(function (state, action) {
    if (typeof state === 'undefined') {
      return {
        title: "fuck mennn",
        counter: 0
      };
     }
     switch (action.type) {
      case 'INCREMENT':
        return {count: state.counter + 1};
      case 'DECREMENT':
        return {count: state.counter - 1};
      default:
        return state;
     }
 });

 var target = document.querySelector('app-title');
 var handlers = {};

 function render() {
    console.log(store.getState())
    var component = components['app-title']
    component(target, store.getState(), handlers);
 }

 // add event handlers using Magery
 handlers.increment = function () {
     store.dispatch({type: 'INCREMENT'});
 };
 handlers.decrement = function () {
     store.dispatch({type: 'DECREMENT'});
 };

 // update the page when the store changes
 store.subscribe(render);

 // initial render
 render();
