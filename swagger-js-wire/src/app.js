var express = require('express');
var app = express();
var proxy = require('express-http-proxy');
 
app.use('/api', proxy('http://192.168.99.100:8088', {
	proxyReqPathResolver: function(req){
		var path = require('url').parse(req.url).path;

		console.log(req.url, path);

		return "/api" + path;
	}
}));  /* https://www.npmjs.com/package/express-http-proxy */
app.use(express.static('src/public'));

app.get('/', function (req, res){
  res.send('Hello World!');
});

app.listen(3000, function (){
  console.log('Listen 3000.');
});
