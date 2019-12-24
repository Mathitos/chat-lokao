package dev.mathitos.chatlokao.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dev.mathitos.chatlokao.R
import dev.mathitos.chatlokao.message.MessageEntity

class MessageAdapter(context: Context, var messages: MutableList<MessageEntity>) : ArrayAdapter<MessageEntity>(context, 0, messages) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.message, parent, false)
        }

        val message = messages[position]

        view!!.findViewById<TextView>(R.id.name).text = message.name
        view.findViewById<TextView>(R.id.content).text = message.text


        return view
    }

}