var models = require('../models');
var request = require('request');
exports.classify = function(req, res, next){
    var body = req.body;
    console.log(body.text);
    if(body.text !== undefined && body.text !== null){
        request.post(
        {
            url: "http://localhost:8000/classify",
            form: {text: body.text}
        },
        function (err, reponse, resbody) {
            if(err){
                res.status(500).send("Server error!");
            }
            else {
                var resJson = JSON.parse(resbody);
                res.status(200).send(resJson);
            }
        }
        );
    }
    else{
        res.status(400).send("Empty patent text");
    }    
};

