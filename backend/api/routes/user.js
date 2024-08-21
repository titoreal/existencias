const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const bcrypt = require("bcrypt");
const jwt = require('jsonwebtoken');
const checkAuth = require ('../middleware/check-auth');


const User = require("../models/user");
const UsersController = require('../controllers/users');

router.post("/signup", UsersController.user_signup);

router.post("/login", UsersController.user_login);

router.delete("/:userId", checkAuth, UsersController.user_delete);

module.exports = router;