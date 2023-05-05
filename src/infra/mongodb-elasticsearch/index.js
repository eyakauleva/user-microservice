const {getUpsertChangeStream, getDeleteChangeStream} = require("./src/listener");
const client = require("./src/config/elastic-config");
require("dotenv").config();

(async () => {
  const upsertChangeStream = await getUpsertChangeStream();
  upsertChangeStream.on("change", async change => {
    console.log("Pushing data to elasticsearch [id = ", change.fullDocument._id + "]");
    change.fullDocument.id = change.fullDocument._id;
    Reflect.deleteProperty(change.fullDocument, "_id");
    Reflect.deleteProperty(change.fullDocument, "_class");
    Reflect.deleteProperty(change.fullDocument, "isNew");
    const response = await client.index({
      "id": change.fullDocument.id,
      "index": process.env.ELASTIC_INDEX,
      "body": change.fullDocument
    });
    console.log("Uploaded successfully [status code = ", response.statusCode + "]");
  });
  
  upsertChangeStream.on("error", error => {
    console.error(error);
  });

  const deleteChangeStream = await getDeleteChangeStream();
  deleteChangeStream.on("change", async change => {
    console.log("Deleting data from elasticsearch [id = ", change.documentKey._id + "]");
    const response = await client.delete({
      "id": change.documentKey._id,
      "index": process.env.ELASTIC_INDEX
    });
    console.log("Deleted successfully [status code = ", response.statusCode + "]");
  });
  
  deleteChangeStream.on("error", error => {
    console.error(error);
  });

})();
