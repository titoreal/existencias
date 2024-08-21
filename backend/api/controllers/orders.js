const mongoose = require("mongoose");
const Order = require('../models/order');
const Product = require('../models/product');



exports.orders_get_all = async (req, res, next) => {
    try {
        const docs = await Order.find()
            .select("product quantity _id")
            .populate('product', 'name')
            .exec();

        res.status(200).json({
            count: docs.length,
            orders: docs.map(doc => ({
                id: doc._id,
                product: doc.product,
                quantity: doc.quantity,
                request: {
                    type: "GET",
                    url: "http://localhost:3000/orders/" + doc._id
                }
            }))
        });
    } catch (err) {
        res.status(500).json({ error: err });
    }
};

exports.orders_create_order = async (req, res, next) => {
    try {
      const product = await Product.findById(req.body.productId).exec();
      if (!product) {
        return res.status(404).json({ message: "Producto inexistente" });
      }
  
      const order = new Order({
        _id: new mongoose.Types.ObjectId(),
        quantity: req.body.quantity,
        product: req.body.productId
      });
  
      const result = await order.save();
      res.status(201).json({
        message: "Orden guardada",
        createdOrder: {
          _id: result._id,
          product: result.product,
          quantity: result.quantity
        },
        request: {
          type: "GET",
          url: "http://localhost:3000/orders/" + result._id
        }
      });
    } catch (err) {
      res.status(500).json({ error: err });
    }
};

exports.orders_get_order = async (req, res, next) => {
    try {
      const order = await Order.findById(req.params.orderId).populate('product', 'name').exec();
      if (!order) {
        return res.status(404).json({ message: "Orden no encontrada" });
      }
      res.status(200).json({
        order: order,
        request: {
          type: "GET",
          url: "http://localhost:3000/orders"
        }
      });
    } catch (err) {
      res.status(500).json({ error: err });
    }
  };

  exports.orders_delete_order =async (req, res, next) => {
    try {
      const result = await Order.deleteOne({ _id: req.params.orderId }).exec();
      res.status(200).json({
        message: "Orden eliminada",
        request: {
          type: "POST",
          url: "http://localhost:3000/orders",
          body: { productId: "ID", quantity: "Number" }
        }
      });
    } catch (err) {
      res.status(500).json({ error: err });
    }
  };