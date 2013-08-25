var express = require('express');
var fs = require('fs');
var http = require('http');
var app = express();

app.use(express.bodyParser());

getJSON = function(options, onResult)
{
    console.log("rest::getJSON");

    var prot = options.port == 443 ? https : http;
    var req = prot.request(options, function(res)
    {
        var output = '';
        console.log(options.host + ':' + res.statusCode);
        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            output += chunk;
        });

        res.on('end', function() {
            var obj = JSON.parse(output);
            onResult(res.statusCode, obj);
        });
    });

    req.on('error', function(err) {
        //res.send('error: ' + err.message);
    });

    req.end();
};


app.get('/exercise/v1/root', function (req, res) {
	console.log("hit");
	res.jsonp({
		subjects: [
			{
				title: "语文",
				id: "101",
				ts: "1",
				chapters: [
					{
						id: "1011",
						ts: "1",
						title: "语文第一章",
						enter_lesson: "10111",
						exit_lesson: "1014",
						lessons: [
							{
								id: "05be6bf8-bc78-11e2-9d5a-00163e011797",
								ts: "1",
								title: "语文第一章第一课"
							}
						]
					}
				]
			}
		/*	{
				title: "数学",
				id: "102",
				ts: "1"
			},
			{
				title: "英语",
				id: "103",
				ts: "1"
			}*/
		],
		userinfo: {
			ts: "1"
		},
		resources: {
			ts: "1"
		},
		achievements: {
			ts: "1"
		}
	});
});

app.get('/exercise/v1/achievements', function (req, res) {
	if((req.param("act") == "cache"||req.param("act")=="status")){
		res.jsonp({
			manifest: [
				{
					url: "/media/123.mp4" 
				}
			]
		});	
	}else{		
		fs.readFile(
			__dirname + '/data/achievements.json',
			'utf8',
			function (err, data) {
				if (err) {
					res.jsonp(404, {error: "no such lesson"});
				} else {
					res.jsonp(JSON.parse(data));
			}
		})
	}
});

app.get('/exercise/v1/chapters/:id', function (req, res) {
	if(req.param("id") == "1011" && req.param("ts") == "1"){
		res.jsonp({
			manifest: [
				{
					url: "/123.mp4" 
				}
			]
		});	
	}
});

app.get('/exercise/v1/resources', function (req, res){
	if(req.param("ts") == "1"){
		res.jsonp({
			manifest: [
				{
					url: "click.wma"
				}
			]
		});
	}
});

app.get('/exercise/v1/lessons/:id', function (req, res) {
	var option = {
		host: "192.168.3.23",
		port: 9000,
		path: "/lesson?lessonId=" + req.param("id"),
		method: "GET",
		headers: {
       			'Content-Type': 'application/json'
   		}
	};
/*
	http.request(option, function(response){
		console.log("status: " + response.statusCode);		
		var output = '';
       		 response.setEncoding('utf8');

       		 response.on('data', function (chunk) {
            		output += chunk;
        	});

        	response.on('end', function() {
            		var obj = JSON.parse(output);
            		res.send(obj);
        	});
	}).
	on("error", function(err){
		console.log("error: " + err);
	});
	
*/
    getJSON(option,
        function(statusCode, result)
        {
            // I could work with the result html/json here.  I could also just return it
            console.log("onResult: (" + statusCode + ")" + JSON.stringify(result));
            res.statusCode = statusCode;
            res.send(result);
        });


/*fs.readFile(
		__dirname + '/data/' + req.param("id") + '/lesson.json',
		'utf8',
		function (err, data) {
			if (err) {
				res.jsonp(404, {error: "no such lesson"});
			} else {
				res.jsonp(JSON.parse(data));
			}
		})*/
})

/*app.get('/exercise/v1/lessons/:id/:fname', function (req, res) {
	console.log("lesson files");
	res.send("a mp3 file");
});*/

app.get('/exercise/v1/lessons/:id/:fname', function (req, res) {
	res.sendfile(__dirname + "/data/multimedia/" + req.param("fname"));
});

app.get('/exercise/v1/user_data/lessons/:id', function (req, res) {
	res.jsonp({});
});

var currentUser = {
	name: "小明",
	age: "12",
	achievements: {
		badges: {},
		awards: {}
	}
};

app.get('/exercise/v1/user_info', function (req, res) {
	console.log("get userinfo:" + currentUser);
	res.jsonp(currentUser);
});

app.post('/userinfo', function (req, res) {
	currentUser = req.param("data");
	console.log("currentUser=" + currentUser);
	res.send("complete");
});

var reqCount = 0;
/*app.get('/exercise/v1/achievements', function (req, res) {
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
*/

app.use('/media', express.static(__dirname + '/data/media'));

app.listen(3000);
console.log('Listening on port 3000');

