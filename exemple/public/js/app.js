const components = MageryCompiler.compile('template')

//window.APP.initialState is defined in index.html
const initialState = window.APP.initialState

const increment = (state, action) => {return {...state, counter: state.counter + 1}}
const decrement = (state, action) => {return {...state, counter: state.counter - 1}}
const toggleDone = (state, action) => {
  const newTodos = state.todos.slice(0)
  const index = newTodos.findIndex( (item) => item.id == action.todoId )
  const oldTodo = newTodos[index]
  const newTodo = Object.assign({}, oldTodo, {done: !oldTodo.done})
  newTodos[index] = newTodo
  return Object.assign({}, state, {todos: newTodos})
}
const changeNewInput = (state, action) => {
  return {
    ...state,
    "newtodo": action.event.target.value
  }
}
const saveNewTodo = (state, action) => {
  const newTodo = {
    name: state["newtodo"],
    done: false,
    id: state.todos.length + 1
   }
   const newState = {
     ...state,
     todos: [...state.todos, newTodo],
     "newtodo":""
   }
   return newState
}
const toggleShowDoneTodos = (state, action) => {
  return {
    ...state,
    "showdonetodos": !state.showdonetodos
  }
}

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
    const newState = reducer(state, action)
    console.log(newState)
    return newState
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
