const express = require("express");
const router = express.Router();
const content = require("./content");
const playlist = require("./playlist");
const badges = require("./badges");

router.use('/',content)
router.use('/',playlist)
router.use('/',badges)

module.exports = router;
