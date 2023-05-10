const {getCollection} = require("./config/mongo-config");
require("dotenv").config();

async function getUpsertChangeStream() {
  const changeStream = (await getCollection(process.env.MONGODB_COLLECTION)).watch([
    {
      "$match": {
        "operationType": {
          "$in": ["insert", "update", "replace"]
        }
      }
    },
    {
      "$project": {
        "documentKey": false
      }
    }
  ]);

  return changeStream;
}

async function getDeleteChangeStream() {
  const changeStream = (await getCollection(process.env.MONGODB_COLLECTION)).watch([
    {
      "$match": {
        "operationType": {
          "$in": ["delete"]
        }
      }
    },
    {
      "$project": {
        "documentKey": true
      }
    }
  ]);

  return changeStream;
}

module.exports = {
  getUpsertChangeStream,
  getDeleteChangeStream
};

