var express = require('express');
var router = express.Router();
var tscontroller = require('../controllers/TimeseriesController');

router.get('/sections', tscontroller.getSectionsTS);

module.exports = router;