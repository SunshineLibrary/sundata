var express = require('express');
var fs = require('fs');
var app = express();

app.get('/exercise/v1/chapter/:id', function (req, res) {
	res.send({
		lessons: [
			{
				id: "lesson1"
			},
			{
				id: "lesson2",
				requirements: ["lesson1"]
			},
			{
				id: "lesson3",
				requirements: ["lesson1"]
			},
			{
				id: "lesson4",
				requirements: ["lesson2", "lesson3"]
			}
		]
	});
});

app.get('/exercise/v1/lesson/:id.json', function (req, res) {
	fs.readFile(
		__dirname + '/data/' + req.param("id") + '/lesson.json',
		'utf8',
		function (err, data) {
			if (err) {
				res.send(404, {error: "no such lesson"});
			} else {
				res.jsonp(JSON.parse(data));
			}
		})
});

app.get('/exercise/v1/lesson/:id/:fname', function (req, res) {
	console.log("lesson files");
	res.send("a mp3 file");
});

app.listen(3000);
console.log('Listening on port 3000');

