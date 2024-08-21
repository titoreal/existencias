const express = require('express');
const router = express.Router();
const checkAuth = require ('../middleware/check-auth');
const OrdersController = require('../controllers/orders');

// GET all orders
router.get("/",  checkAuth, OrdersController.orders_get_all);
// POST create a new order
router.post("/", checkAuth, OrdersController.orders_create_order);

// GET a single order by ID
router.get("/:orderId", checkAuth, OrdersController.orders_get_order);

// DELETE an order by ID
router.delete("/:orderId", checkAuth, OrdersController.orders_delete_order);

module.exports = router;
