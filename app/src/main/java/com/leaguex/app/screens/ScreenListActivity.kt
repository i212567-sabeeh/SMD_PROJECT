package com.leaguex.app.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.Extras
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityScreenListBinding
import com.leaguex.app.databinding.ItemScreenRowBinding
import com.leaguex.app.databinding.ItemScreenSectionBinding

/**
 * "LeagueX screens": where the app opens. "Start the app" first, then every Figma frame under its section's
 * title, then "Style kit". Opened with [Extras.OPEN] set to a frame id, it shows that frame straight away.
 */
class ScreenListActivity : LeagueXActivity() {

    private lateinit var binding: ActivityScreenListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.title, bottom = binding.list)
        binding.list.adapter = RowAdapter(rows())

        if (savedInstanceState == null) {
            intent.getStringExtra(Extras.OPEN)?.let(Frames::byId)?.let { frame ->
                startActivity(frame.open(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }

    private fun rows(): List<Row> = buildList {
        // Shown but disabled until the Splash screen exists.
        add(Row.Item(getString(R.string.screens_start), open = null))
        for (section in Frames.sections) {
            add(Row.Section(section.title))
            section.frames.forEach { frame -> add(Row.Item(frame.name) { startActivity(frame.open(this@ScreenListActivity)) }) }
        }
        add(Row.Item(getString(R.string.screens_style_kit)) { startActivity(Intent(this@ScreenListActivity, StyleKitActivity::class.java)) })
    }

    private sealed interface Row {
        data class Section(val title: String) : Row
        /** A row that opens a screen; disabled while [open] is null. */
        data class Item(val name: String, val open: (() -> Unit)?) : Row
    }

    private class RowAdapter(private val rows: List<Row>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private class SectionHolder(val binding: ItemScreenSectionBinding) : RecyclerView.ViewHolder(binding.root)
        private class ItemHolder(val binding: ItemScreenRowBinding) : RecyclerView.ViewHolder(binding.root)

        override fun getItemCount() = rows.size

        override fun getItemViewType(position: Int) = when (rows[position]) {
            is Row.Section -> TYPE_SECTION
            is Row.Item -> TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_SECTION) {
                SectionHolder(ItemScreenSectionBinding.inflate(inflater, parent, false))
            } else {
                ItemHolder(ItemScreenRowBinding.inflate(inflater, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val row = rows[position]) {
                is Row.Section -> (holder as SectionHolder).binding.root.text = row.title
                is Row.Item -> with((holder as ItemHolder).binding.root) {
                    text = row.name
                    val open = row.open
                    if (open != null) {
                        setOnClickListener { open() }
                    } else {
                        setOnClickListener(null)
                        isClickable = false
                    }
                    isEnabled = open != null
                }
            }
        }

        private companion object {
            const val TYPE_SECTION = 0
            const val TYPE_ITEM = 1
        }
    }
}
