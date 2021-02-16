console.warn("*** Script loaded!")

/**
 * Code for the Websocket chat application
 */

const inputField = document.querySelector('#chat-input')
const chatArea = document.querySelector('#chat-area')

// open a websocket
const route = "ws://localhost:9000/chatSocket"
const socket = new WebSocket(route)
socket.onopen = function(e) {
  socket.send("New user connected.")
};


const sendsEvent = () => {
  socket.send(inputField.value)
  inputField.value = '';
}

inputField?.onkeydown = event => {
  if(event.key === 'Enter') {
    sendsEvent()
  }
}


socket.onmessage = event => {
  chatArea.value += `\n${event.data}`
}