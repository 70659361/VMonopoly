var express=require('express');
var app = express();
var ADODB = require('node-adodb');
const connection = ADODB.open('Provider=Microsoft.Jet.OLEDB.4.0; Data Source=monodb.mdb;');


app.get('/login/:username', function(req, res){
	
	
	var loginUsr=req.params.username;
	var result="";
	
	console.log('/login/'+loginUsr);
	
	var querySQL = 'SELECT userprofile.login, usercoins.coins FROM userprofile INNER JOIN usercoins ON userprofile.ID = usercoins.userid WHERE login="'+loginUsr+'"';
	console.log(querySQL);
	
	connection.query(querySQL).then(users=>{
		console.log(users);
		if(users.length == 1){
			result=JSON.stringify(users[0]);
			console.log(result);
			res.send(result);
		}else{
			res.send("");
		}
	}).catch(err=>{console.log(err);});
});

app.get('/coins/:username', function(req, res){
	var loginUsr=req.params.username;
	console.log('/coins/'+loginUsr);

		
	var querySQL = 'SELECT userprofile.login, usercoins.coins FROM userprofile INNER JOIN usercoins ON userprofile.ID = usercoins.userid WHERE login="'+loginUsr+'"';
	console.log(querySQL);
	
	
	connection.query(querySQL).then(users=>{
		console.log(users);
		if(users.length == 1){
			result=JSON.stringify(users[0]);
			console.log(result);
			res.send(result);
		}else{
			res.send("");
		}
	}).catch(err=>{console.log(err);});
});
	
var server = app.listen(8888, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
});