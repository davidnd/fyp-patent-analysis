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

exports.getSectionDetails = function(req, res) {
	var sectionId = req.params.id;
	models.sequelize.query("SELECT subclasses.description, subclasses.count, subclasses.id FROM patent.subclasses\
	left join patent.classes on classes.id = subclasses.class_id\
	left join patent.subsections on subsections.id = classes.subsection_id\
	left join patent.sections on subsections.section_id = sections.id\
	where sections.id = " + sectionId + 
	" order by subclasses.count desc limit 10;", {type: models.sequelize.QueryTypes.SELECT})
	.then(function(result){
		res.send(result);
	}).catch(function(err) {
		res.sendStatus(500);
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
};
exports.getPatents = function (req, res, next) {
	models.Patent.findAll({
		limit:1000
	}).then(function (patents) {
		res.send(patents);
	}).catch(function (err) {
		res.render('error', {
			message:err.message,
			error:err
		});
	});
}