var express = require('express');
var router = express.Router();
var IndexController = require('../controllers/IndexController');
var TreeMapController = require('../controllers/TreeMapController');
var ClassificationController = require('../controllers/ClassificationController');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Patent analysis' });
});
router.get('/sections/:id', IndexController.getSectionDetails);
router.get('/classes/:id', IndexController.getClassDetails);
router.get('/sections', IndexController.getSections);
router.get('/subsections', IndexController.getSubsections);
router.get('/classes', IndexController.getClasses);
router.get('/subclasses', IndexController.getSubclasses);
router.get('/patents', IndexController.getPatents);
// router.get('/subsections/:id', IndexController.getSubsectionDetails);
router.get('/treemap', TreeMapController.getTreeMapData);
router.get('/classification', function(req, res, next){
    res.render('classification');
});
router.post('/classification', ClassificationController.classify);

module.exports = router;