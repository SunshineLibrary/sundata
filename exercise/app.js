var express = require('express');
var fs = require('fs');
var app = express();

app.get('/root', function (req, res) {
	res.send({
		subjects: [
			{
				name: "语文",
				id: "101",
				ts: "1",
				chapter: [
					{
						id: "1011",
						ts: "1",
						name: "语文第一章",
						enter_lesson: "10111",
						exit_lesson: "1014",
						lessons: [
							{
								id: "10111",
								ts: "1",
								title: "语文第一章第一课"
							},
							{
								id: "10112",
								ts: "1",
								title: "语文第一章第二课",
								requirements: ["10111"]
							},
							{
								id: "10113",
								ts: "1",
								title: "语文第一章第三课",
								requirements: ["10111"]
							},
							{
								id: "1014",
								ts: "4",
								title: "语文第一章第四课",
								requirements: ["10112", "10113"]
							}
						]
					},
					{
						id: "1012",
						ts: "2",
						name: "语文第二章",
						enter_lesson: "10121",
						exit_lesson: "10124",
						lessons: [
							{
								id: "10121",
								title: "语文第二章第一课"
							},
							{
								id: "10122",
								title: "语文第二章第二课",
								requirements: ["10121"]
							},
							{
								id: "10123",
								title: "语文第二章第三课",
								requirements: ["10121"]
							},
							{
								id: "10124",
								title: "语文第二章第四课",
								requirements: ["10122", "10123"]
							}
						]
					}
				]
			},
			{
				name: "数学",
				id: "102",
				ts: "1"
			},
			{
				name: "英语",
				id: "103",
				ts: "1"
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

app.get('/exercise/v1/lesson/:id/:fname', function (req, res) {
	res.sendfile(__dirname + "/data/" + req.param("id") + '/' + req.param("fname"));
});


var currentUser = {};

app.get('/userinfo', function (req, res) {
	console.log("get userinfo:" + currentUser);
	res.send(currentUser);
});

app.post('/userinfo', function (req, res) {
	currentUser = req.param("data");
	console.log("currentUser=" + currentUser);
	res.send("complete");
});

var reqCount = 0;
app.get('/exercise/v1/achievements', function (req, res) {
	console.log(req.param("ts"));
	console.log(req.param("act"));
	if (typeof req.param("ts") != "undefined" && typeof req.param("act") == "undefined") {
		fs.readFile(__dirname + "/data/achievements.json", 'utf-8', function (err, data) {
			res.jsonp(JSON.parse(data));
		});
	} else if (typeof req.param("ts") == "undefined" && typeof req.param("act") == "undefined") {
		console.log("hit");
		fs.readFile(__dirname + "/data/achievements.json", 'utf-8', function (err, data) {
			res.jsonp(JSON.parse(data));
		});
	} else if (typeof req.param("ts") != "undefined" && req.param("ts") == "123" && req.param("act") == "status") {
		reqCount++;
		if (reqCount <= 10) {
			res.jsonp({
				cached: reqCount,
				total: 10,
				manifest: [
					{
						url: "123.mp4",
						progress: reqCount * 10
					}
				]
			});
		} else {
			reqCount = 0;
		}

	}
})


app.listen(3000);
console.log('Listening on port 3000');

