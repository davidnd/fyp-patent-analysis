var express = require('express');
var router = express.Router();
var IndexController = require('../controllers/IndexController');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});
router.get('/sections', IndexController.getSections);
router.get('/subsections', IndexController.getSubsections);
router.get('/classes', IndexController.getClasses);
router.get('/subclasses', IndexController.getSubclasses);
router.get('/patents', IndexController.getPatents);
router.get('/sections/details/:id', IndexController.getSectionDetails);
// router.get('/subsections/:id', IndexController.getSubsectionDetails);
module.exports = router;
