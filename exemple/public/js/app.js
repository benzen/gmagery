var components = MageryCompiler.compile('template');

//window.APP.initialState is defined in index.html
var initialState = window.APP.initialState;
var store = Redux.createStore(function (state, action) {
    if (typeof state === 'undefined') {
      return initialState
     }
     switch (action.type) {
      case 'INCREMENT':
        return Object.assign({}, state, {counter: state.counter + 1});
      case 'DECREMENT':
        return Object.assign({}, state, {counter: state.counter - 1});
      default:
        return state;
     }
 });

 var target = document.querySelector('app-root')
 var handlers = {};


 // add event handlers using Magery
 handlers.increment = function () {
     store.dispatch({type: 'INCREMENT'});
 };
 handlers.decrement = function () {
     store.dispatch({type: 'DECREMENT'});
 };

 var render = function(){
    components['app-root'](target, store.getState(), handlers);
 }

 // update the page when the store changes
 store.subscribe(render);

 // initial render
 render();
