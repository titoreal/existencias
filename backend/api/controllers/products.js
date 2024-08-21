const mongoose = require("mongoose");
const Order = require('../models/order');
const Product = require('../models/product');

exports.products_get_all = (req, res, next) => {
  Product.find()
      .select('name price _id productImage')
      .exec()
      .then(docs => {
          const response = {
              count: docs.length,
              products: docs.map(doc => {
                  let imageUrl = null;
                  if (doc.productImage) {
                      // Extraer solo el nombre del archivo de la ruta completa
                      const fileName = doc.productImage.split('\\').pop().split('/').pop();
                      imageUrl = `http://192.168.100.81:3000/uploads/${fileName}`;
                  }
                  return {
                      name: doc.name,
                      price: doc.price,
                      productImage: imageUrl,
                      id: doc._id,
                      request: {
                          type: 'GET',
                          url: `http://192.168.100.81:3000/products/${doc._id}`
                      }
                  }
              })
          };
          res.status(200).json(response);
      })
      .catch(err => {
          console.log(err);
          res.status(500).json({
              error: err
          });
      });
};

exports.products_create_product = (req, res, next) => {
    const product = new Product({
      _id: new mongoose.Types.ObjectId(),
      name: req.body.name,
      price: req.body.price,
      productImage: req.file.path
    });
    product
      .save()
      .then(result => {
        console.log(result);
        res.status(201).json({
          message: "Objeto creado exitósamente en  /products",
          createdProduct: {
            name: result.name,
            price: result.price,
            id: result._id,
            request: {
              type: 'GET',
              url: 'http://localhost:3000/products/' + result._id
            }
          }
        });
      })
      .catch(err => {
        console.log(err);
        res.status(500).json({
          error: err
        });
      });
  };
  exports.products_get_product = (req, res, next) => {
    const id = req.params.productId;
    Product.findById(id)
    .select('name price _id productImage')
      .exec()
      .then(doc => {
        console.log("Desde la base de datos", doc);
        if (doc) {
          res.status(200).json({
            product: doc,
            request:{
                type: 'GET',                
                url: 'http://localhost:3000/products/'
            }
         });
        } else {
          res
            .status(404)
            .json({ message: "No hay entrada que se ajuste a la ID" });
        }
      })
      .catch(err => {
        console.log(err);
        res.status(500).json({ error: err });
      });
  };
  
  exports.products_update_product = (req, res, next) => {
    const id = req.params.productId;
    const updateOps = {};
    for (const ops of req.body) {
        updateOps[ops.propName] = ops.value;
    }
    Product.findByIdAndUpdate(id, { $set: updateOps }, { new: true })
        .then(result => {
                 res.status(200).json({
                    message: "Producto actualizado exitosamente",
                    request:{
                        type: 'GET',                
                        url: 'http://localhost:3000/products/'
                    }            
        });
    })
        .catch(err => {
            console.log(err);
            res.status(500).json({
                error: err
            });
        });
};
exports.products_delete = (req, res, next) => {
    const id = req.params.productId;
    Product.findByIdAndDelete(id)
        .then(result => {
            if (result) {
                res.status(200).json({
                    message: "Producto eliminado exitosamente",
                   request:{
                    type: 'POST',
                    url: 'http://localhost:3000/products/',
                    body: {name: 'String', price: 'Number'}
                   }
                });
            } else {
                res.status(404).json({
                    message: "No se encontró un producto con ese ID"
                });
            }
        })
        .catch(err => {
            console.log(err);
            res.status(500).json({
                error: err
            });
        });
};