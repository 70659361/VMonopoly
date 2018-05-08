var express=require('express');
var app = express();
var ADODB = require('node-adodb');
const bodyParser=require('body-parser');
const connection = ADODB.open('Provider=Microsoft.Jet.OLEDB.4.0; Data Source=C:/workspace/03_Innovation/VMonopoly/node/monodb.mdb;');


app.get('/login/:username', function(req, res){
	
	
	var loginUsr=req.params.username;
	var result="";
	
	console.log('GET /login/'+loginUsr);
	
	var querySQL = 'SELECT login, coin FROM user WHERE login="'+loginUsr+'"';
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
	console.log('GET /coins/'+loginUsr);

		
	var querySQL = 'SELECT login, coinFROM user WHERE login="'+loginUsr+'"';
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
	
	var querySQL = 'UPDATE user SET coin=' +newCoins+ ' WHERE login="'+loginUsr+'"';
	console.log(querySQL);
	
	connection.execute(querySQL);
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
	
	var querySQL = 'SELECT ID from user where login="'+loginUsr+'"';
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

