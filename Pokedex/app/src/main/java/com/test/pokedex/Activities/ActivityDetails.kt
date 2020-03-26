package com.test.pokedex.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.Adapters.AdapterList
import com.test.pokedex.R
import kotlinx.android.synthetic.main.activity_list.*
import java.util.*

class ActivityDetails : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter:AdapterList

    private lateinit var data: JsonObject

    private var pokemonNumber: String = "0"

    private lateinit var pokemonImg: ImageView
    private lateinit var pokemonName: TextView
    private lateinit var pokemonTypes: TextView
    private lateinit var pokemonMovements: TextView
    private lateinit var pokemonStats: TextView

    private var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        manageIntent()
        initializeComponents()
        initializeData()

    }

    private fun manageIntent()
    {
        if (intent != null) {
            pokemonNumber = intent.getStringExtra("Number")
        }
    }

    private fun initializeComponents()
    {
        // placeholders
        pokemonImg = findViewById<ImageView>(R.id.img_details)
        pokemonName = findViewById<TextView>(R.id.txt_details_name)
        pokemonTypes = findViewById<TextView>(R.id.txt_types_content)
        pokemonMovements = findViewById<TextView>(R.id.txt_movements_content)
        pokemonStats = findViewById<TextView>(R.id.txt_stats_content)
    }

    private fun initializeData()
    {
        Ion.with(context)
            .load("https://pokeapi.co/api/v2/pokemon/$pokemonNumber/")
            .asJsonObject()
            .done { e, result ->
                data = result
                if (e == null)
                {
                    // If it finds any sprite data
                    if (!data.get("sprites").isJsonNull)
                    {
                        // And that data is not null
                        if (data.get("sprites").asJsonObject.get("front_default") != null)
                        {
                            // Loads the sprite
                            Glide
                                .with(context)
                                .load(data.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .into(pokemonImg)
                        }
                    }
                    // If there is no sprite data
                    else
                    {
                        // Load the logo in its place and logs an error
                        pokemonImg.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.pokemon_logo_min)
                        )
                        Log.e("Json Error", "No information gathered")
                    }


                    // If it finds any pokemon name data
                    if (!data.get("name").isJsonNull) {
                        // It stores the information in nameAux
                        val nameAux = "#$pokemonNumber: " +
                                (this.data.get("name").toString().replace("\"", "").toUpperCase())
                        // Then it assigns that information to the text field
                        pokemonName.text = nameAux

                    }
                    // If there's no pokemon name data
                    else
                    {
                        // It logs an error and sets the pokemon name text view to give feedback
                        pokemonName.text = "#$pokemonNumber: No hemos encontrado esta información del pokémon"
                        Log.e("Json Error", "No information gathered")
                    }

                    // If it finds any pokemon types data
                    if (!data.get("types").isJsonNull)
                    {
                        // It stores the information in typeAux
                        val typeAux = data.get("types").asJsonArray
                        var typesAnswer: String = ""

                        // Get every type gathered
                        for (i in 0.until(typeAux.size()))
                        {
                            // Saves the item and gets the type name
                            val typeItem = typeAux.get(i).asJsonObject.get("type").asJsonObject
                            val typeName = typeItem.get("name").toString().replace("\"", "").capitalize()
                            // Appends that information to the result
                            typesAnswer += "$typeName \n"
                        }
                        // Sets the pokemon text view to the result
                        pokemonTypes.text = typesAnswer
                    }
                    // If there is no pokemon type information
                    else
                    {
                        // It logs an error and sets the pokemon type text view to give feedback
                        pokemonTypes.text = "No hemos encontrado esta información del pokémon"
                        Log.e("Json Error", "No information gathered")
                    }

                    // If it finds any pokemon movements data
                    if (!data.get("moves").isJsonNull)
                    {
                        // It stores the information in movesAux
                        val movesAux = data.get("moves").asJsonArray
                        var movesAnswer: String = ""

                        // Get every movement gathered
                        for (i in 0.until(movesAux.size()))
                        {
                            // Saves the item and gets the move name
                            val moveItem = movesAux.get(i).asJsonObject.get("move").asJsonObject
                            val moveName = moveItem.get("name").toString().replace("\"", "").capitalize()

                            // Appends that information to the result
                            movesAnswer += "$moveName \n"
                        }
                        // Sets the pokemon text view to the result
                        pokemonMovements.text = movesAnswer
                    }
                    // If there is no pokemon movements information
                    else
                    {
                        // It logs an error and sets the pokemon movements text view to give feedback
                        pokemonMovements.text = "No hemos encontrado esta información del pokémon"
                        Log.e("Json Error", "No information gathered")
                    }


                    // If it finds any pokemon stats data
                    if (!data.get("stats").isJsonNull)
                    {
                        // It stores the information in statsAux
                        val statsAux = data.get("stats").asJsonArray
                        var statsAnswer = ""

                        // Get every stats gathered
                        for (i in 0.until(statsAux.size()))
                        {
                            // Saves the item and gets the stats name
                            val statsItem = statsAux.get(i).asJsonObject.get("stat").asJsonObject
                            val statsName = statsItem.get("name").toString().replace("\"", "").capitalize()
                            val statsValue = statsAux.get(i).asJsonObject.get("base_stat")

                            // Appends that information to the result
                            statsAnswer += "$statsName: $statsValue\n"
                        }
                        // Sets the pokemon text view to the result
                        pokemonStats.text = statsAnswer
                    }
                    // If there is no pokemon stats information
                    else
                    {
                        // It logs an error and sets the pokemon stats text view to give feedback
                        pokemonStats.text = "No hemos encontrado esta información del pokémon"
                        Log.e("Json Error", "No information gathered")
                    }
                }
                initializeList()
            }
    }

    fun initializeList(){
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        adapter = AdapterList()

        recycler_view_list.layoutManager = linearLayoutManager
        recycler_view_list.adapter = adapter
        recycler_view_list.setHasFixedSize(true)
        recycler_view_list.itemAnimator = DefaultItemAnimator()
    }
}
