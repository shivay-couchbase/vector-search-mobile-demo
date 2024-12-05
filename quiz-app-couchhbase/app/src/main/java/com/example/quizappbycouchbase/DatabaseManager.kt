package com.example.quizappbycouchbase

import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.StateSet.TAG
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableArray
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Parameters
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.ResultSet
import com.couchbase.lite.SelectResult
import com.couchbase.lite.VectorIndexConfigurationFactory
import com.couchbase.lite.newConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException


class DatabaseManager(private val context: Context) {
    var database: Database? = null
    private val defaultDatabase = "shivay123444"
    private val defaultImageCollection = "imageCollection123"
    var questionsDatabase: Database? = null
    var scoresDatabase: Database? = null
    var collection: Collection? = null
    var questionsCollection: Collection? = null
    var scoresCollection: Collection? = null
    var imagesCategoryCollection: Collection? = null
    private var INDEX_NAME = "image_index"
    private var query: Query? = null
    private val lock = Any()
    private var isImportingDataNeeded = false




    init {

        CouchbaseLite.init(context)
        try {
            CouchbaseLite.enableVectorSearch()
        } catch (e: CouchbaseLiteException) {
            throw java.lang.IllegalStateException("Could not enable vector search", e)
        }
        Log.i(TAG, "Database created: User")

        initializeDatabases()
    }

    fun initializeDatabases(){
//        val dbConfig = DatabaseConfigurationFactory.create(context.filesDir.toString())
        database = Database(defaultDatabase)
//        questionsDatabase = Database(defautQuestionsDatabase)
//        scoresDatabase = Database(defaultScoresDatabase)
//        var collection1: Collection? = userDatabase.createCollection("Verlaine")
//        val collectionName = "usercollection"
        if (database!!.getCollection(defaultImageCollection) == null){
            isImportingDataNeeded = true
        }
        collection = database!!.getCollection("user") ?: database!!.createCollection("user")
        questionsCollection = database!!.getCollection("questionssss") ?: database!!.createCollection("questionssss")
        scoresCollection = database!!.getCollection("scores") ?: database!!.createCollection("scores")
        imagesCategoryCollection = database!!.getCollection(defaultImageCollection) ?: database!!.createCollection(defaultImageCollection)
        GlobalScope.launch(Dispatchers.Main) {
            // Execute the database search operation in the background thread
            val result = withContext(Dispatchers.IO) {
                if (isImportingDataNeeded) {
                    importDataFromJson()
                }
            }
        }
    }


    fun closeDatabases() {
        try {
            database?.close()
            questionsDatabase?.close()
            scoresDatabase?.close()
        } catch (e: java.lang.Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }}

    fun importDataFromJson() {
        try {
            // Read JSON data from file
            val json_imageembeddings: String = loadJSONFromAsset("image_embeddings.json")
            val questions: String = loadJSONFromAsset("questions.json")

            // Parse JSON data
            val jsonArray_image = JSONArray(json_imageembeddings)
            val jsonArray_questions = JSONArray(questions)

            // Open or create the Couchbase Lite database
//            val database: Database = openOrCreateDatabase()
            Log.i("COUNT1",jsonArray_image.length().toString())
            // Store JSON data in the database
            for (i in 0 until jsonArray_image.length()) {
                val jsonObject = jsonArray_image.getJSONObject(i)
                val mutableDocument = MutableDocument()
                val imagevect_l2 = jsonObject.optJSONArray("imagevect_l2")
                if (imagevect_l2 != null) {
                    val doubleList = (0 until imagevect_l2.length()).mapNotNull { imagevect_l2.optDouble(it) }
                    Log.i("GOTARRAY", "GOTARRAY")
                    val mutableArray = MutableArray()
                    for (doubleValue in doubleList) {
                        mutableArray.addValue(doubleValue)
                    }
                    mutableDocument.setArray("imagevect_l2", mutableArray)
                }


                val name = jsonObject.optString("name")
                if (!name.isNullOrEmpty()) {
                    Log.i("GOTNAME", "GOTNAME")
                    mutableDocument.setString("name", name)
                }
                val unique_id = jsonObject.optInt("unique_id")
                mutableDocument.setInt("unique_id", unique_id)
                val category = jsonObject.optString("category")
                if (!category.isNullOrEmpty()) {
                    mutableDocument.setString("category", category)
                    Log.i("GOTCATEGORY", "GOTCATEGORY")
                }
                imagesCategoryCollection?.save(mutableDocument)
            }
            Log.i("JSON", "ALL JSON ADDED")

            imagesCategoryCollection?.createIndex(INDEX_NAME,
                            VectorIndexConfigurationFactory.newConfig("imagevect_l2", 3L, 100L))
                ?: throw IllegalStateException("No such collection: colors")

            Log.i("COUNT2",jsonArray_questions.length().toString())
            // Store JSON data in the database
            for (i in 0 until jsonArray_questions.length()) {
                val jsonObject = jsonArray_questions.getJSONObject(i)
                val mutableDocument = MutableDocument()
                val question = jsonObject.optString("question")
                if (!question.isNullOrEmpty()) {
                    Log.i("GOTQUESTION", "GOTQUESTION")
                    mutableDocument.setString("question", question)
                }
                val unique_id = jsonObject.optInt("unique_id")
                mutableDocument.setInt("unique_id", unique_id)
                val optionOne = jsonObject.optString("optionOne")
                if (!optionOne.isNullOrEmpty()) {
                    Log.i("GOTOPTIONONE", "GOTOPTIONONE")
                    mutableDocument.setString("optionOne", optionOne)
                }
                val optionTwo = jsonObject.optString("optionTwo")
                if (!optionTwo.isNullOrEmpty()) {
                    Log.i("GOTOPTIONONE", "GOTOPTIONONE")
                    mutableDocument.setString("optionTwo", optionTwo)
                }
                val optionThree = jsonObject.optString("optionThree")
                if (!optionThree.isNullOrEmpty()) {
                    Log.i("optionThree", "optionThree")
                    mutableDocument.setString("optionThree", optionThree)
                }
                val optionFour = jsonObject.optString("optionFour")
                if (!optionFour.isNullOrEmpty()) {
                    Log.i("optionFour", "optionFour")
                    mutableDocument.setString("optionFour", optionFour)
                }
                val category = jsonObject.optString("category")
                if (!category.isNullOrEmpty()) {
                    mutableDocument.setString("category", category)
                    Log.i("GOTCATEGORY", "GOTCATEGORY")
                }
                val correctAns = jsonObject.optInt("correctAns")
                mutableDocument.setInt("correctAns", correctAns)
                questionsCollection?.save(mutableDocument)
            }
            Log.i("JSON QUESTION", "ALL JSON ADDED")
//             Close the database after importing data
//            database.close()
        } catch (e: IOException) {
            Log.i("JSON", "Error importing data from JSON")
            Log.e(TAG, "Error importing data from JSON", e)
        } catch (e: JSONException) {
            Log.i("JSON", "Error importing data from JSON")
            Log.e(TAG, "Error importing data from JSON", e)
        }
    }

    private fun checkInitialized() {
        if (database == null) {
            throw IllegalStateException("Database is not ready")
        }
    }

    /** Search similar image. */

    fun search(image: List<Double>) : List<ImageObject> = synchronized(lock) {
        // Check if the database is initialized:
        checkInitialized()

        // Create the query object:
        if (query == null) {
            val sql = "SELECT unique_id, name, imagevect_l2, category " +
                    "FROM $defaultImageCollection " +
                    "ORDER BY APPROX_VECTOR_DISTANCE(imagevect_l2, \$vector)" +
                    "LIMIT 1";
            query = database!!.createQuery(sql)
        }

        // Set $vector parameter on the query:
        val params = Parameters()
        params.setArray("vector", MutableArray(image))
        query!!.parameters = params

        // Execute search and return result:
        val images = mutableListOf<ImageObject>()
        query!!.execute().use { rs ->
            for (result in rs) {
                val unique_id = result.getInt(0)
                val name = result.getString(1)
                val imagevectArray = result.getArray(2)
                val category = result.getString(3)

                // Check for null values and handle them gracefully:
                if (unique_id != null && name != null && imagevectArray != null && category != null) {
                    val imagevect = imagevectArray.toList() as List<Double>
                    images.add(ImageObject(unique_id, name, imagevect, category))
                } else {
                    // Log a warning if any of the required fields are null
                    Log.w("ImageSearch", "One or more fields are null for result: $result")
                }
            }
        }
        return images
    }


    @Throws(IOException::class)
    private fun loadJSONFromAsset(filename: String): String {
        var json: String
        context.assets.open(filename).use { `is` ->
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            json = String(buffer, charset("UTF-8"))
        }
        return json
    }


    fun insertUser(user: User) {

        try {
            // Create a new MutableDocument to represent the user
            val userDocument = MutableDocument()
                .setString("type", "user") // Optionally, you can add a type field to distinguish user documents
                .setString("username", user.username)
                .setDate("dateCreated",user.dateCreated)
            // Add more fields as needed

            // Save the document to the user database
            collection?.save(userDocument)

            // Optionally, you can log a success message
            Log.i("Database", "User document inserted successfully.")
        } catch (e: Exception) {
            // Handle any exceptions that occur during the insertion process
            Log.e("Database", "Error inserting user document: ${e.message}")
        }
    }

    fun insertUserScore(score: Scores) {
        try {
            // Create a new MutableDocument to represent the user
            val userDocument = MutableDocument()
                .setString("type", "user") // Optionally, you can add a type field to distinguish user documents
                .setString("username", score.username)
                .setInt("score",score.score)
                .setString("category",score.category)
            // Add more fields as needed

            // Save the document to the user database
            scoresCollection?.save(userDocument)

            // Optionally, you can log a success message
            Log.i("Database", "Score document inserted successfully.")
        } catch (e: Exception) {
            // Handle any exceptions that occur during the insertion process
            Log.e("Database", "Error inserting user document: ${e.message}")
        }
    }

    fun getAllScores(): ArrayList<Scores>{
        val scores = ArrayList<Scores>()
        try {
            val query = QueryBuilder.select(
                SelectResult.property("username"),
                SelectResult.property("score"),
                SelectResult.property("category")
            )
                .from(DataSource.collection(scoresCollection!!))
            val result: ResultSet = query.execute()
            for (row in result) {
                val score = row.getInt("score")
                val username = row.getString("username") ?: ""
                val category = row.getString("category") ?: ""
                val scoreObject = Scores(username,score,category)
                scores.add(scoreObject)
            }
        }catch (e: Exception) {
            Log.e("TAG", "Error retrieving documents: ${e.message}")
        }
        return scores
    }

    fun getQuestions(): ArrayList<Questions>{
        val questions = ArrayList<Questions>()
        try {
            val query = QueryBuilder.select(
                SelectResult.property("unique_id"),
                SelectResult.property("question"),
                SelectResult.property("optionOne"),
                SelectResult.property("optionTwo"),
                SelectResult.property("optionThree"),
                SelectResult.property("optionFour"),
                SelectResult.property("correctAns"),
                SelectResult.property("category")
            )
                .from(DataSource.collection(questionsCollection!!))
            val result: ResultSet = query.execute()
            for (row in result) {
                val id = row.getInt("unique_id")
                val question = row.getString("question") ?: ""
                val optionOne = row.getString("optionOne") ?: ""
                val optionTwo = row.getString("optionTwo") ?: ""
                val optionThree = row.getString("optionThree") ?: ""
                val optionFour = row.getString("optionFour") ?: ""
                val correctAns = row.getInt("correctAns")
                val category = row.getString("category") ?: ""
                val questionObject = Questions(id, question, optionOne, optionTwo, optionThree, optionFour, correctAns,category)
                questions.add(questionObject)
            }
        }catch (e: Exception) {
            Log.e("TAG", "Error retrieving documents: ${e.message}")
        }
        return questions
    }

    fun insertQuestion(question: Questions){
        try {
            // Create a new MutableDocument to represent the user
            val questionDocument = MutableDocument()
                .setString("type", "question") // Optionally, you can add a type field to distinguish user documents
                .setInt("id", question.id)
                .setString("question",question.question)
                .setString("optionOne",question.optionOne)
                .setString("optionTwo",question.optionTwo)
                .setString("optionThree",question.optionThree)
                .setString("optionFour",question.optionFour)
                .setInt("correctAns",question.correctAns)


            // Add more fields as needed

            // Save the document to the user database
            questionsCollection?.save(questionDocument)

            // Optionally, you can log a success message
            Log.i("Database", "question document inserted successfully.")
        } catch (e: Exception) {
            // Handle any exceptions that occur during the insertion process
            Log.e("Database", "Error inserting question document: ${e.message}")
        }
    }

    protected fun finalize() {
        database?.close()
    }

    fun deleteDatabases() {
        try {
            closeDatabases()
            Database.delete(defaultDatabase, context.filesDir)
        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }
}
