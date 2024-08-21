const http = require('http');
const app = require('./app');
const port = process.env.PORT || 3000;

const server = http.createServer(app);

server.listen(port, '0.0.0.0', () => {
  console.log(`Servidor corriendo en el puerto ${port}`);
  console.log(`Accesible localmente en: http://localhost:${port}`);
  console.log(`Accesible en la red local en: http://<tu-ip-local>:${port}`);
});