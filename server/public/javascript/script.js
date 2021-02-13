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
  socket.send("text message")
};
