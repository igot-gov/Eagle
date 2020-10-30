const express = require("express");
const router = express.Router();
const content = require("./content");
const playlist = require("./playlist");

router.use('/',content)
router.use('/',playlist)

module.exports = router;
