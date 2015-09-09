var models = require('../models');

exports.getSections = function(req, res, next){
	models.Section.findAll().then(function(sections){
		res.send(sections);
	})
}