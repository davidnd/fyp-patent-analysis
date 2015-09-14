var express = require('express');
var router = express.Router();
var tscontroller = require('../controllers/TimeseriesController');

router.get('/subclasses', tscontroller.getSubclassTS);
router.get('/sections', tscontroller.getSectionTS);

module.exports = router;