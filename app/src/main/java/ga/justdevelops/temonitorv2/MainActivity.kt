package ga.justdevelops.temonitorv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ga.justdevelops.temonitorv2.ui.main.MainFragment
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

}
