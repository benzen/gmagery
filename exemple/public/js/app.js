const components = MageryCompiler.compile('template')

//window.APP.initialState is defined in index.html
const initialState = window.APP.initialState

const increment = (state, action) => Object.assign({}, state, {counter: state.counter + 1})
const decrement = (state, action) => Object.assign({}, state, {counter: state.counter - 1})
const toggleDone = (state, action) => {
  const newTodos = state.todos.slice(0)
  const index = newTodos.findIndex( (item) => item.id == action.todoId )
  const oldTodo = newTodos[index]
  const newTodo = Object.assign({}, oldTodo, {done: !oldTodo.done})
  newTodos[index] = newTodo
  return Object.assign({}, state, {todos: newTodos})
}
const changeNewInput = (state, action) => Object.assign({}, state, {"new-todo": event.target.value})
const saveNewTodo = (state, action) => {
  const newTodo = {
    name: state["new-todo"],
    done: false,
    id: state.todos.length + 1
   }
   const newTodos = state.todos.slice(0)
   newTodos.push(newTodo)
   return Object.assign({}, state, {todos: newTodos, "new-todo": ""})
}
const toggleShowDoneTodos = (state, action) => Object.assign({}, state, {"show-done-todos": !state["show-done-todos"]})
const reducer = (state, action) => {
  switch (action.type) {
   case 'increment': return increment(state, action)
   case 'decrement': return decrement(state, action)
   case "toggleDone": return toggleDone(state, action)
   case "changeNewInput": return changeNewInput(state, action)
   case "saveNewTodo": return saveNewTodo(state, action)
   case "toggleShowDoneTodos": return toggleShowDoneTodos(state, action)
   default: return state
  }
}
const store = Redux.createStore(function (state, action) {
    if (typeof state === 'undefined') {
      return initialState
    }
    return reducer(state, action)
 });

 var target = document.querySelector('app-root')
 var handlers = {};

 // add event handlers using Magery
const dispatch = store.dispatch
handlers.increment = () =>  dispatch({type: 'increment'})
handlers.decrement = () => dispatch({type: 'decrement'})
handlers.toggleDone = (todoId) => dispatch({type: "toggleDone", todoId: todoId})
handlers.changeNewInput = (event) => dispatch({type: "changeNewInput", event: event})
handlers.saveNewTodo = (event) => {
  if(event.key == "Enter"){
    dispatch({type: "saveNewTodo"})
  }
}
handlers.toggleShowDoneTodos = () => dispatch({type: "toggleShowDoneTodos"})
 const render = function(){
    components['app-root'](target, store.getState(), handlers)
 }

 // update the page when the store changes
 store.subscribe(render)

 // initial render
 render()
