package tech.codevil.tracne.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.ActivityMainBinding
import tech.codevil.tracne.db.EntryDao
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var entryDao: EntryDao

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(javaClass.simpleName, "Entry DAO = ${entryDao.hashCode()}")
    }
}