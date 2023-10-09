package com.example.game_bug

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.game_bug.databinding.ActivityRecordsBinding


@Suppress("DEPRECATION")
class RecordsActivity : BaseActivity() {
    private var st: StoredData? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            val binding: ActivityRecordsBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_records)
            st = StoredData()
            val recordItems: ArrayList<RecordItem?> = st!!.getAllRecords(this)
            val records_table = findViewById<TableLayout>(R.id.records_table)
            var row: TableRow
            var t1: TextView
            var t2: TextView
            var t3: TextView
            val dip =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
                    .toInt()
            for (current in recordItems.indices) {
                val recordItem = recordItems[current]
                row = TableRow(this)
                t1 = TextView(this)
                t2 = TextView(this)
                t3 = TextView(this)
                if (recordItem != null) {
                    t1.text = if (recordItem.Name != null) recordItem.Name else ""
                }
                if (recordItem != null) {
                    t2.text = recordItem.KillCount.toString()
                }
                if (recordItem != null) {
                    t3.text = if (recordItem.CreatedAt != null) recordItem.CreatedAt else ""
                }
                t1.gravity = Gravity.START
                t2.gravity = Gravity.START
                t3.gravity = Gravity.START
                t1.textSize = 15f
                t2.textSize = 15f
                t3.textSize = 15f
                t1.setPadding(5 * dip, 0, 0, 0)
                t2.setPadding(5 * dip, 0, 0, 0)
                t3.setPadding(5 * dip, 0, 0, 0)
                row.addView(t1)
                row.addView(t2)
                row.addView(t3)
                records_table.addView(
                    row,
                    TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
            binding.buttonRemoveAllRecords.setOnClickListener {
                st!!.deleteAllRecords(this@RecordsActivity)
                val intent = Intent(this@RecordsActivity, MainActivity::class.java)
                startActivity(intent)
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            return when (item.itemId) {
                R.id.activity_main -> {
                    onBackPressed()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        try {
            val intent = Intent(this@RecordsActivity, MainActivity::class.java)
            startActivity(intent)
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onPause() {
        try {
            super.onPause()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.pauseMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onResume() {
        try {
            super.onResume()
            if (ApplicationHolder.soundService == null) BindSoundService()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.resumeMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onDestroy() {
        try {
            super.onDestroy()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.stopMusic()
            if (ApplicationHolder.soundService != null) UnbindSoundService()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }
}