var express = require('express');
var fs = require('fs');
var http = require('http');
var app = express();

app.use(app.router);
app.use(express.methodOverride());

app.use(express.bodyParser());

app.get('/ping', function (req, res) {
    res.send({ping: true});
})

getJSON = function (options, onResult) {

    var prot = options.port == 443 ? https : http;
    var req = prot.request(options, function (res) {
        var output = '';
        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            output += chunk;
        });

        res.on('end', function () {
            var obj = JSON.parse(output);
            onResult(res.statusCode, obj);
        });
    });

    req.on('error', function (err) {
        //res.send('error: ' + err.message);
    });

    req.end();
};


app.get('/exercise/v1/root', function (req, res) {
    console.log("hit");
    res.jsonp({
        subjects: [
            {
                title: "语文 初一上",
                subject: "chinese",
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
                                summary: "",
                                title: "语文第一章第一课"
                            },
                            {
                                id: "10111",
                                ts: "1",
                                title: "语文第一章第二课",
                                summary: "课文写的是“我”童年时一次“脱险”的经历，其中蕴含着生活的哲理。在人生道路上常常会遇到意想不到的困难，“我”的脱险对你也会有宝贵的启示。",
                                requirements: ["05be6bf8-bc78-11e2-9d5a-00163e011797"]
                            },
                            {
                                id: "1_Unit_1（起始卡）",
                                ts: "1",
                                summary: "Unit 1重点听力练习",
                                title: "Unit 1 - Listening"
                            }
                        ]
                    }
                    /*	{
                     id: "1012",
                     ts: "1",
                     title: "语文第二章"
                     },
                     {
                     id: "1013",
                     ts: "1",
                     title: "语文第三章"
                     },
                     {
                     id: "1014",
                     ts: "1",
                     title: "语文第四章"
                     }*/
                ]
            },
            {
                title: "数学 初一上",
                subject: "math",
                id: "102",
                ts: "1",
                chapters: [
                    {
                        id: "1021",
                        ts: "1",
                        title: "数学第一章",
                        enter_lesson: "10211",
                        exit_lesson: "1024",
                        lessons: [
                            {
                                id: "认识负数（教学）",
                                ts: "1",
                                summary: "来看看负数是什么以及如何使用负数吧！",
                                title: "认识负数"
                            }
                        ]
                    }
                ]
            },
            {
                title: "英语 初一上",
                subject: "english",
                id: "103",
                ts: "1"
            }
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
    if ((req.param("act") == "cache" || req.param("act") == "status")) {
        res.jsonp({
            is_cached: true,
            progress: 100,
            manifest: [
                    "/media/123.mp4"
            ]
        });
    } else {
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
    if (req.param("id") == "1011" && req.param("ts") == "1") {
        res.jsonp({
            manifest: [
                
                    "/123.mp4"
                
            ]
        });
    }
});

var walk = function (dir, done) {
    var results = [];
    fs.readdir(dir, function (err, list) {
        if (err) return done(err);
        var pending = list.length;
        if (!pending) return done(null, results);
        list.forEach(function (file) {
            file = dir + '/' + file;
            fs.stat(file, function (err, stat) {
                if (stat && stat.isDirectory()) {
                    walk(file, function (err, res) {
                        results = results.concat(res);
                        if (!--pending) done(null, results);
                    });
                } else {
                    results.push(file.slice(1));
                    if (!--pending) done(null, results);
                }
            });
        });
    });
};

app.get('/exercise/v1/resources', function (req, res) {
    if (req.param("ts") == "1") {
        var dir = './resources';

        walk(dir, function (err, result) {
            if (err) {
                throw err;
            }
            var list = [];
            for (var i = 0; i < result.length; i++) {
                if (result[i].indexOf(".DS_Store") == -1) {
                    list.push("/exercise/v1"+result[i]);
                }
            }
            res.jsonp({
                is_cached: true,
                progress: 100,
                manifest: list
            });
        });
    }
});

app.get('/exercise/v1/resources/*.*', function(req, res){
    res.sendfile(__dirname + req.url.slice(12));
})

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
     var output = '';
     response.setEncoding('utf8');

     response.on('data', function (chunk) {
     output += chunk;
     });

     response.on('end', function() {
     var obj = JSON.parse(output);
     res.jsonp(obj);
     });
     }).
     on("error", function(err){
     console.log("error: " + err);
     });


     getJSON(option,
     function(statusCode, result)
     {
     // I could work with the result html/json here.  I could also just return it
     res.statusCode = statusCode;
     res.jsonp(result);
     });
     */

    fs.readFile(
        __dirname + '/data/' + req.param("id") + '/lesson.json',
        'utf8',
        function (err, data) {
            if (err) {
                res.jsonp(404, {error: "no such lesson"});
            } else {
                res.jsonp(JSON.parse(data));
            }
        })

})

/*app.get('/exercise/v1/lessons/:id/:fname', function (req, res) {
 console.log("lesson files");
 res.send("a mp3 file");
 });*/

app.get('/exercise/v1/lessons/:id/:fname', function (req, res) {
    res.sendfile(__dirname + "/data/multimedia/" + req.param("fname"));
});

app.get('/exercise/v1/user_data/lessons/:id', function (req, res) {
    //res.jsonp(user_data[req.param("id")]);
    res.jsonp({});
});

user_data = {};

app.post('/exercise/v1/user_data/lessons/:id', function (req, res) {
    console.log("id:" + req.param("id") + "," + req.param("data"));
    console.log("body:" + req.body);
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

