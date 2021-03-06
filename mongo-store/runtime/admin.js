/**
 * Administrative commands reference. 
 * Connect to database using mongo command line client and run.
 */

// Run on mongo CLI after loading the test books data

// to create text index on "content.page" field in the books collection
use readaloud;
db.books.ensureIndex( { "content.page": "text" } );

// recreate all indexes in the books collection, including the one on _id
db.books.reIndex();

// drop the database
use readaloud;
db.dropDatabase();