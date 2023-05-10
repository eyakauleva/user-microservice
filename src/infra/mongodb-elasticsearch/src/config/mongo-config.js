const mongodbClient = require("mongodb").MongoClient;
require("dotenv").config();

const connectionString = "mongodb://" + process.env.MONGODB_USER + ":" + process.env.MONGODB_PASSWORD
    + "@" + process.env.MONGODB_DOMAIN + ":27017/users?authSource=admin&replicaSet=rs0"

let client;

async function getDb() {
  if (!client || !client.isConnected()) {
    client = await mongodbClient.connect(connectionString, {"useNewUrlParser": true, "useUnifiedTopology": true});
    console.log("Connected successfully");
  }
  return client.db();
}

async function getCollection(collectionName) {
  const db = await getDb();
  return db.collection(collectionName);
}

module.exports = {
  getCollection
};
