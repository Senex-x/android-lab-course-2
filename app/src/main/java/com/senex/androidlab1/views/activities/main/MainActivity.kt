package com.senex.androidlab1.views.activities.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.senex.androidlab1.R
import com.senex.androidlab1.database.AppDatabaseMain
import com.senex.androidlab1.models.Note
import com.senex.androidlab1.utils.log
import io.github.serpro69.kfaker.Faker
import java.util.*
import kotlin.random.Random

// TODO: add binding to dev
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        AppDatabaseMain.init(applicationContext)

        //AppDatabaseMain.database.noteDao().deleteAll()
        //createRandomNotes(10)

        log("Database snapshot: " +
                AppDatabaseMain.database.noteDao().getAll().toString()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_delete_all -> {
            mainViewModel.removeAll()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun createRandomNotes(amount: Int) {
        val notes = mutableListOf<Note>()
        val faker = Faker()

        for (i in 1..amount) {
            notes.add(Note(
                null,
                faker.quote.famousLastWords(),
                faker.harryPotter.quotes() + " " + faker.harryPotter.quotes(),
                Calendar.getInstance(),
                if (Random(i).nextBoolean())
                    Calendar.getInstance().apply {
                        set(2021, Random.nextInt(1, 13), Random.nextInt(1, 29))
                    }
                else null,
                if (Random(i).nextBoolean()) Random.nextDouble(180.0) else null,
                if (Random(i).nextBoolean()) Random.nextDouble(180.0) else null
            ))
        }

        AppDatabaseMain.database.noteDao().insertAll(*notes.toTypedArray())
    }
}

