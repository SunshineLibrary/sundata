var express = require('express');
var fs = require('fs');
var app = express();
app.use(express.bodyParser());

app.get('/exercise/v1/chapter/1011', function (req, res) {
	res.send({
		manifest:[
		{
			url:"/exercise/v1/lesson/10111/1.mp3",
			ts:234
		},
		{
			url:"/exercise/v1/lesson/10111/2.mp4",
			ts:234
		}
		]
	});
}

app.listen(3001);
console.log('Listening on port 3001');
