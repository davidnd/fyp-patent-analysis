var express = require('express');
var router = express.Router();
var IndexController = require('../controllers/IndexController');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});
router.get('/sections', IndexController.getSections);
router.get('/subs', IndexController.getSubsections);

module.exports = router;
