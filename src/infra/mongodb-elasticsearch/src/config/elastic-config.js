const {Client} = require('@elastic/elasticsearch')
require("dotenv").config();

const client = new Client({
    node: 'http://' + process.env.ELASTIC_HOST + ':9200',
    auth: {
        username: process.env.ELASTIC_USER,
        password: process.env.ELASTIC_PASSWORD
      }
});

module.exports = client;