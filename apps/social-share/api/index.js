const express = require("express");
const router = express.Router();
const content = require("./content");
const playlist = require("./playlist");
const badges = require("./badges");
const goals = require("./goals");

router.use('/',content)
router.use('/',playlist)
router.use('/',badges)
router.use('/',goals)

module.exports = router;
