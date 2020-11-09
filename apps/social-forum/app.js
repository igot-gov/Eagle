/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
var express = require('express');
var helmet = require('helmet') // Helmet includes HSTS as a built-in header
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var healthcheck = require('express-healthcheck')


var followRouter = require('./routes/follow/index');
var authToolRouter = require('./routes/authTool/index')
var userActivityRouter = require('./routes/userActivity/index')
var postActivityRouter = require('./routes/post/index');
var searchActivityRouter = require('./routes/search/index');
var adminRouter = require('./routes/admin/index')
var catalogRouter = require('./routes/catalog/index')

var app = express();
app.use(helmet());
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(function(req,res,next){
    res.set({"Cache-Control": "no-store","Pragma":"no-cache"})
    next();
})

app.use('/healthcheck', healthcheck({
    healthy() {
      return { everything: 'is ok' }
    },
  }))
app.use('/', followRouter);
app.use('/authtool',authToolRouter)
app.use('/useractivity',userActivityRouter)
app.use('/post',postActivityRouter)
app.use('/search',searchActivityRouter)
app.use('/admin',adminRouter)
app.use('/catalog',catalogRouter)

// app.listen(3000,function(err){
//     if(err){
//         console.log("cannot connect to the port 3000",err)
//     }
//     console.log("listening to port 3000")
// })

module.exports = app;
