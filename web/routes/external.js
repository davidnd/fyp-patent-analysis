var express = require('express');
var router = express.Router();
var exec = require('child_process').exec;
var cmd = "git pull;npm install";
router.post('/gitpull', function(req, res, next){
    var body = req.body;
    var secret = body.secret;
    console.log("updating repo....");
    exec(cmd, function(err, stdout, stderr){
        if(err){
            console.log("Failed to update repo");
        }
        else{
            res.sendStatus(200);
        }
    });
});

module.exports = router;