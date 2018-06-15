const express = require("express");
const bodyParser = require("body-parser");

const mysql = require("mysql");
const crypto = require('crypto');
const fs = require("fs");

const markdown = require('markdown-js');

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

const pool = mysql.createPool({
	host:	'127.0.0.1',
	port:	3306,
	user:	'jinsuoji',
	password:	'Jinsuoji.123',
	database:	'jinsuoji'
});

function md5(text) {
	return crypto.createHash('md5').update(text).digest('hex');
};

function randomWord(randomFlag, min, max){
	var str = "";
	var arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
	range = randomFlag ? Math.round(Math.random() * (max-min)) + min : min;
	for(var i=0; i<range; i++){
		pos = Math.round(Math.random() * (arr.length-1));
		str += arr[pos];
	}
	return str;
}

app.post("/register",  (req,res)=>{
	var params = req.body;
	console.log(params);
	if (params === undefined || params.username === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "username"});
		res.end();
		return;
	}
	if (params.storedPassword === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "storedPassword"});
		res.end();
		return;
	}
	if (/[A-Za-z0-9\u4e00-\u9fa5_-]+/.exec(params.username) === null) {
		res.statusCode = 400;
		res.json({error:"USERNAME_INVALID", data: params.username});
		res.end();
		return;
	}
	if (/[0-9a-f]{16}/.exec(params.storedPassword) === null) {
		res.statusCode = 400;
		res.json({error:"USERNAME_INVALID", data: params.username});
		res.end();
		return;
	}
	pool.query("INSERT INTO accounts(username, password) VALUES(?, ?)", [params.username, params.storedPassword], (error, results, fields)=>{
		if (error || results.affectedRows===0) {
			switch (error.code) {
				case "ER_DATA_TOO_LONG":
					res.statusCode = 413;
					res.json({error:"USERNAME_TOO_LONG", data: params.username});
					res.end();
					return;
				case "ER_DUP_ENTRY":
					res.statusCode = 409;
					res.json({error:"USER_EXISTS", data:params.username});
					res.end();
					return;
				default:
					console.log(error);
					res.statusCode = 400;
					res.json({error:"UNKNOWN", data:""});
					res.end();
					return;
			}
		}
		res.statusCode = 200;
		res.json("success");
		res.end();
		return;
	});
});

app.post("/login_salt", (req,res)=>{
	var params = req.body;
	if (params === undefined || params.username === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "username"});
		res.end();
		return;
	}
	var salt = randomWord(true, 10, 20);
	pool.query("UPDATE accounts SET salt=? WHERE username=?", [salt, params.username], (error, results, fields)=>{
		if (error) {
			console.log(error);
			res.statusCode = 500;
			res.end();
			return;
		}
		if (results.affectedRows === 0) {
			res.statusCode = 404;
			res.send({error: "USER_NOT_FOUND", data: params.username});
			res.end();
			return;
		}
		res.statusCode = 200;
		res.json({salt: salt});
		res.end();
		return;
	});
});

app.post("/login", (req,res)=>{
	var params = req.body;
	if (params === undefined || params.username === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "username"});
		res.end();
		return;
	}
	if (params.encrypted === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "encrypted"});
		res.end();
		return;
	}
	if (params.salt === undefined) {
		res.statusCode = 400;
		res.json({error: "MISSING_ARGS", data: "salt"});
		res.end();
		return;
	}
	pool.query("SELECT salt, password FROM accounts WHERE username=?", [params.username], (error, results, fields)=>{
		if (error) {
			res.statusCode = 404;
			res.json({error: "USER_NOT_FOUND", data: params.username});
			res.end();
			return;
		}
		var userinfo = results[0];
		if (userinfo.salt === "" || params.salt !== userinfo.salt) {
			res.statusCode = 400;
			res.json({error: "SALT_EXPIRED", data: params.salt});
			console.log("salt expired:");
			console.log("server userinfo:" + JSON.stringify(userinfo));
			console.log("client body.salt:" + params.salt);
			res.end();
			return;
		}
		var encrypted = md5(userinfo.salt + userinfo.password);
		pool.query("UPDATE accounts SET salt=null WHERE username=?", [params.username], (error, results, fields)=>{ if(error)console.log(error); });
		if (params.encrypted !== encrypted) {
			res.statusCode = 401;
			res.json({error: "AUTHENTICATION_FAILED", data: ""});
			console.log("authentication failed:");
			console.log("username:" + params.username);
			console.log("client encrypted:" + params.encrypted);
			console.log("server encrypted:" + encrypted);
			res.end();
			return;
		}
		res.statusCode = 200;
		if(params.req) {
			var token = randomWord(false, 32); 
			pool.query("REPLACE INTO tokens(username, token) VALUES(?, ?)", [params.username, token], (error)=>{
				if (error) console.log(error);
				res.json({token: token});
				res.end();
			});
		} else {
			res.json("success");
			res.end();
		}
	});
});

app.put("/sync", (req, res)=>{
	if (req === undefined || req.get("Token") === undefined) {
		res.statusCode = 401;
		res.json({error: "MISSING_ARGS", data: "token"});
		res.end();
		return;
	}
	pool.query("SELECT username FROM tokens WHERE token=?", [req.get("Token")], (error, results, fields)=>{
		if (error) {
			res.statusCode = 500;
			res.end();
			return;
		}
		if (results.length === 0) {
			res.statusCode = 401;
			res.json({error: "TOKEN_EXPIRED", data: req.get("Token")});
			res.end();
			return;
		}
		pool.query("DELETE FROM tokens WHERE token=?", [req.get("Token")], (error)=>{});
		var username = results[0].username;
		fs.writeFile("jinsuoji/" + username + ".json", JSON.stringify(req.body), (error)=>{
			if (error) {
				res.statusCode = 500;
				console.log(error);
				res.json({error: "SAVING_MIRROR_ERROR", data: error});
				res.end();
			} else {
				res.statusCode = 201;
				res.json({msg: "created"});
			}
		});
	});
});

app.get("/sync", (req, res)=>{
	if (req === undefined || req.get("Token") === undefined) {
		res.statusCode = 401;
		res.json({error: "MISSING_ARGS", data: "token"});
		res.end();
		return;
	}
	pool.query("SELECT username FROM tokens WHERE token=?", [req.get("Token")], (error, results, fields)=>{
		if (error) {
			res.statusCode = 500;
			res.end();
			return;
		}
		if (results.length === 0) {
			res.statusCode = 401;
			res.json({error: "TOKEN_EXPIRED", data: req.get("Token")});
			res.end();
			return;
		}
		pool.query("DELETE FROM tokens WHERE token=?", [req.get("Token")], (error)=>{});
		var username = results[0].username;
		fs.readFile("jinsuoji/" + username + ".json", (error,data)=>{
			if (error) {
				res.statusCode = 404;
				res.json({error: "FILE_NOT_FOUND", data: ""});
				res.end();
				return;
			}
			res.statusCode = 200;
			res.setHeader("Content-Type", "application/json; charset=utf-8");
			res.send(data.toString('utf8'));
			res.end();
			return;
		});
	});
});

app.get('/sync/example', (req, res)=>{
	fs.readFile('example.json', (error, data)=>{
		if (error) {
			res.statusCode = 404;
			res.json({error:"FILE_NOT_FOUND", data: "example"});
			res.end();
			return;
		}
		res.statusCode = 200;
		res.setHeader("Content-Type", "application/json; charset=utf-8");
		res.send(data.toString('utf8'));
		res.end();
		return;
	});
});

app.get('/static/style.css', (req, res)=>{
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/css');
    fs.readFile('views/styles.css', 'utf8', (err, str)=>{
        res.end(str);
    });
});

app.get('/doc', (req, res)=>{
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/html');
    fs.readFile('views/doc.md', 'utf8', (err,str)=>{
        str = '<!DOCTYPE html><html><head><meta charset="utf-8"/><title>API文档</title><link rel="stylesheet" href="/static/style.css"/></head><body class="markdown-body">' + markdown.parse(str).toString() + '</body></html>';
        res.end(str);
    });
});

app.get('/privacy', (req, res)=>{
	res.statusCode = 200;
	res.setHeader('Content-Type', 'text/html');
	fs.readFile('views/privacy.md', 'utf8', function(err, str){  
		str = '<!DOCTYPE html><html><head><meta charset="utf-8"/><title>今琐记·隐私政策</title><rel link=stylesheet href="/static/style.css"></head><body class="markdown-body">' + markdown.parse(str).toString() + '</body></html>';
		res.end(str);
	});
});

app.get('/feedback', (req, res)=>{
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/html');
    fs.readFile('views/feedback.html', 'utf8', function(err, str){
        res.end(str);
    });
});

app.post('/feedback', (req, res)=>{
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/html');
    console.log(req.body.feedback);
    fs.readFile('views/feedback.html', 'utf8', function(err, str){
        res.end(str);
    });
});

app.all('/*', (req, res)=>{
	console.log("not found trial:" + req.method + " " + req.originalUrl);
	res.statusCode = 400;
	res.json({error: "API_NOT_FOUND", data:""});
});

app.listen(80);
