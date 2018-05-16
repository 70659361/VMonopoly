var express=require('express');
var app = express();
var ADODB = require('node-adodb');
const bodyParser=require('body-parser');
const connection = ADODB.open('Provider=Microsoft.Jet.OLEDB.4.0; Data Source=./node/monodb.mdb;');


app.get('/login/:username', function(req, res){
	
	
	var loginUsr=req.params.username;
	var result="";
	
	console.log('GET /login/'+loginUsr);
	
	var querySQL = 'SELECT * FROM users WHERE login="'+loginUsr+'"';
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

app.get('/user/:uid', function(req, res){
	var uid=req.params.uid;
	console.log('GET /user/'+uid);
	var querySQL = 'SELECT* FROM users WHERE id='+uid;
	console.log(querySQL);
	
	connection.query(querySQL).then(user=>{
		result=JSON.stringify(user[0]);
		res.send(result);
	}).catch(err=>{
		console.log(err);
		res.send("");
	});
});

app.get('/poi/:pid', function(req, res){
	var pid=req.params.pid;
	console.log('GET /poi/'+pid);
	var querySQL = 'SELECT* FROM poi WHERE poiid="'+pid+'"';
	console.log(querySQL);
	
	connection.query(querySQL).then(poi=>{
		result=JSON.stringify(poi[0]);
		res.send(result);
	}).catch(err=>{
		console.log(err);
		res.send("");
	});
});

app.get('/pois/:uid', function(req, res){
	var uid=req.params.uid;
	console.log('GET /pois/'+uid);
	
	var querySQL = 'SELECT* FROM poi WHERE poiowner='+uid;
	console.log(querySQL);
	
	connection.query(querySQL).then(pois=>{
		result=JSON.stringify(pois);
		res.send(result);
	}).catch(err=>{
		console.log(err);
		res.send("");
	});
});
	
app.get('/coins/:username', function(req, res){
	var loginUsr=req.params.username;
	console.log('GET /coins/'+loginUsr);

		
	var querySQL = 'SELECT login, coinFROM users WHERE login="'+loginUsr+'"';
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

app.post('/coins/:username/:coins', function(req, res){
	var loginUsr=req.params.username;
	var newCoins=req.params.coins;
	console.log('POST /coins/'+loginUsr);
	
	var querySQL = 'UPDATE users SET coin=' +newCoins+ ' WHERE login="'+loginUsr+'"';
	console.log(querySQL);
	
	connection.execute(querySQL).catch(err=>{console.log(err);});

	res.send("OKs");

});

app.use('/pois', require('body-parser').json(), function(req, res){
	console.log(req.body);
	var n = req.body.id.length;
	var ids = new Array();
	var querySQL = 'SELECT * from poi WHERE poiid IN (';
	for(var i=0;i<n-1;i++){
		querySQL +="\""+ (req.body.id[i])+'\",';
	}
	querySQL += "\""+(req.body.id[n-1])+'\")';
	console.log(querySQL);
	var result="";
	connection.query(querySQL).then(pois=>{
		console.log(pois);
		result=JSON.stringify(pois);
		res.send(result);
	}).catch(err=>{
		res.send(result);
		console.log(err);
	});	
});

app.post('/buy/:username/:poiid/:price', function(req, res){
	var loginUsr=req.params.username;
	var poiid=req.params.poiid;
	var price=req.params.price;
	
	var querySQL = 'SELECT ID FROM users where login="'+loginUsr+'"';
	var uid="";
	connection.query(querySQL).then(user=>{
		console.log(user);
		if(user != "[]"){
			uid=user[0]["ID"];
			console.log(uid);
			
			try{
				querySQL = 'INSERT INTO poi (poiid, poiowner, poiprice ) VALUES("'+poiid+'",'+uid+', '+price+')';
				console.log(querySQL);
				connection.execute(querySQL);
				
				querySQL = 'UPDATE usercoins set coins=coins-'+price+' WHERE userid='+uid;
				console.log(querySQL);
				connection.execute(querySQL);
			}catch(e){
			}
		}
	}).catch(err=>{
		console.log(err);
	});	
		
	res.send("");
});

var server = app.listen(8888, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
});

