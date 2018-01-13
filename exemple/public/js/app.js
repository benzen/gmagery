var components = MageryCompiler.compile('template');

// create a store
var store = Redux.createStore(function (state, action) {
    if (typeof state === 'undefined') {
      return {
        key: "value", 
        title: "Sake", 
        counter: 0
      };
     }
     switch (action.type) {
      case 'changeCounter':
        return {count: state.counter + action.change};
      // case 'DECREMENT':
      //   return {count: state.counter - 1};
      default:
        return state;
     }
 });

 var target = document.querySelector('app-root');
 var handlers = {};

 function render() {
   console.log(store.getState())
    var component = components['app-root']
    component(target, store.getState(), handlers);
 }

 // add event handlers using Magery
 handlers.buttonHandler = function (change) {
     store.dispatch({type: 'changeCounter', change: change});
 };
 // handlers.decrement = function () {
 //     store.dispatch({type: 'DECREMENT'});
 // };

 // update the page when the store changes
 store.subscribe(render);

 // initial render
 render();
