
package com.dirror.music.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.dirror.music.R
import com.dirror.music.manager.User

/**
 * MyFragment 页 User 适配器
 */
class MyFragmentUserAdapter(
    val baseClickListener: () -> Unit
): RecyclerView.Adapter<MyFragmentUserAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clBase: ConstraintLayout = view.findViewById(R.id.clBase)

        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val ivCVip: ImageView = view.findViewById(R.id.ivCVip)
        val tvNickname: TextView = view.findViewById(R.id.tvNickname)
        val tvLevel: TextView = view.findViewById(R.id.tvLevel)

        init {
            clBase.setOnClickListener {
                baseClickListener()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_fragment_my_user, parent, false).apply {
            return ViewHolder(this)
        }
    }

    var adapterUser: AdapterUser? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            adapterUser?.let {
                if (!User.isVip()) {
                    ivCVip.visibility = View.GONE
                }
                ivCover.load(it.cover + "?param=100y100") {
                    transformations(CircleCropTransformation())
                    crossfade(true)
                }

                tvLevel.text = "Lv.${it.level}"
            }
            tvNickname.text = User.dsoUser.nickname
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    data class AdapterUser(
        var cover: String?,
        var nickname: String?,
        var level: Int?
    )

}