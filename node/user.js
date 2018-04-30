var express=require('express');
var app = express();
var ADODB = require('node-adodb');
const connection = ADODB.open('Provider=Microsoft.Jet.OLEDB.4.0; Data Source=monodb.mdb;');


app.get('/login/:username', function(req, res){
	var loginUsr=req.params.username;
	var isUser=false;
	var result="";
	var existusers;
	
	connection.query('SELECT * FROM userprofile where login="'+loginUsr+'"').then(users=>{
		
		if(users.length == 1){
			console.log("Has this user");
			result='{"user":"'+loginUsr+'","isUser":1}';
			//result=JSON.stringify(result);
			res.send(result);
		}else{
			console.log(users);
			console.log("No such user");
			result='{"user":"'+loginUsr+'","isUser":0}';
			//result=JSON.stringify(result);
			res.send(result);
		}
		}).catch(err=>{console.log(err);});
			
	console.log("/login:  " + "hello  "+loginUsr);
});

var server = app.listen(8888, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
});