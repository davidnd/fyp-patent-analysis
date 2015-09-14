var express = require('express');
var router = express.Router();
var tscontroller = require('../controllers/TimeseriesController');

router.get('/subclasses', tscontroller.getSubclassTS);
router.get('/classes', tscontroller.getClassTS);
router.get('/sections', tscontroller.getSectionTS);
router.get('/subsections', tscontroller.getSubsectionTS);

module.exports = router;