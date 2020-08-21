package ro.code4.casefile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ro.code4.casefile.R
import java.util.Locale

class LanguageAdapter(context: Context, private val languages: List<Locale>) :
    ArrayAdapter<Locale>(context, 0, languages) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_spinner, parent, false)

        val locale = languages[position]
        (view as TextView).text = locale.getDisplayLanguage(locale).capitalize()
        return view
    }
}
