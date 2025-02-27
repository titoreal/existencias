// app.js
const express = require('express');
const app = express();
const morgan = require('morgan');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const productRoutes = require('./api/routes/products');
const orderRoutes = require('./api/routes/orders');
const userRoutes = require('./api/routes/user');
const path = require('path');

mongoose.connect('mongodb+srv://titoalejandroreal:' + process.env.MONGO_TITIN_PW +
    '@clustertitin.srwu4jy.mongodb.net/?retryWrites=true&w=majority&appName=ClusterTitin')
    .then(() => console.log('Conectado a MongoDB'))
    .catch((error) => console.error('Error conectando a MongoDB:', error));
mongoose.Promise = global.Promise;

app.use('/uploads', express.static('uploads', {
    setHeaders: (res, path, stat) => {
      if (path.endsWith('.jpg') || path.endsWith('.jpeg')) {
        res.set('Content-Type', 'image/jpeg');
      } else if (path.endsWith('.png')) {
        res.set('Content-Type', 'image/png');
      }
    }
  }));
app.use(morgan('dev'));
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers",
         "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    if (req.method === 'OPTIONS') {
        res.header('Access-Control-Allow-Methods', 'PUT, POST, PATCH, DELETE, GET');
        return res.status(200).json({});
    }
    next();
});

// Asume que estas líneas están más abajo en tu archivo
app.use('/products', productRoutes);
app.use('/orders', orderRoutes);
app.use('/user', userRoutes);

 
app.use((req, res, next)=>{
    const error = new Error('Not found');
    error.status  = 404;
    next(error);
})

app.use((error, req, res, next) =>{
    res.status(error.status || 500);
    res.json({
        error: {
            message : error.message
        }
    });
});

module.exports = app;
