const jwt = require('jsonwebtoken');

module.exports = (req, res, next) => {
    try {
        console.log('Headers:', req.headers);
        const token = req.headers.authorization?.split(" ")[1];
        console.log('Token:', token);
        if (!token) {
            throw new Error('No token provided');
        }
        const decoded = jwt.verify(token, process.env.JWT_KEY);
        console.log('Decoded:', decoded);
        req.userData = decoded;
        next();
    } catch (error) {
        console.log('Auth Error:', error.message);
        return res.status(401).json({
            message: 'Auth failed: ' + error.message
        });
    }
};