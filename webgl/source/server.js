// npm install connect serve-static
// then run in terminal
// node server.js
var connect = require('connect');
var serveStatic = require('serve-static');

connect()
    .use(serveStatic(__dirname))
    .listen(8080, () => console.log('Server running on 8080...'));