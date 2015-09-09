var models = require('../models');

exports.getSections = function(req, res, next){
	models.Section.findAll().then(function(sections){
		res.send(sections);
	}).catch(function(err){
		res.render('error', {
	      message: err.message,
	      error: err
    	});
	});
};
exports.getSubsections = function(req, res, next){
	models.Subsection.findAll({
		order: [
			[
				'count', 'DESC'
			],
		],
		limit: 10
	}).then(function(subs){
		res.send(subs);
	}).catch(function(err){
		res.render('error', {
	      message: err.message,
	      error: err
    	});
	});
};

exports.getClasses = function(req, res, next){
	models.Class.findAll({
		order: [
			[
				'count', 'DESC'
			],
		],
		limit: 10
	}).then(function(subs){
		res.send(subs);
	}).catch(function(err){
		res.render('error', {
	      message: err.message,
	      error: err
    	});
	});
};

exports.getSubclasses = function(req, res, next){
	models.Subclass.findAll({
		order: [
			[
				'count', 'DESC'
			],
		],
		limit: 10
	}).then(function(subs){
		res.send(subs);
	}).catch(function(err){
		res.render('error', {
	      message: err.message,
	      error: err
    	});
	});
}