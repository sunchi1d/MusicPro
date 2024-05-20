package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.App.Companion.mmkv
import com.dirror.music.R
import com.dirror.music.adapter.*
import com.dirror.music.data.SearchType
import com.dirror.music.databinding.ActivitySearchBinding
import com.dirror.music.music.standard.data.StandardAlbum
import com.dirror.music.music.standard.data.StandardPlaylist
import com.dirror.music.music.standard.data.StandardSinger
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.playlist.SongPlaylistActivity
import com.dirror.music.ui.playlist.TAG_KUWO
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.ui.viewmodel.SearchViewModel
import com.dirror.music.util.*
import com.dirror.music.util.asDrawable
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 搜索界面
 */
class SearchActivity : BaseActivity() {

    companion object {
        private val TAG = "SearchActivity"
    }

    private lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()

    private var realKeyWord = ""

    private var searchType: SearchType

    init {
        val typeStr = mmkv.decodeString(Config.SEARCH_TYPE, SearchType.SINGLE.toString())!!
        searchType = SearchType.valueOf(typeStr)
    }

    override fun initBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        // 获取焦点
        binding.etSearch.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        // 获取推荐关键词
        App.cloudMusicManager.getSearchDefault {
            runOnMainThread {
                // toast(it)
                binding.etSearch.hint = it.data.showKeyword
                realKeyWord = it.data.realkeyword
            }
        }
        // 获取热搜
        App.cloudMusicManager.getSearchHot {
            runOnMainThread {
                binding.rvSearchHot.layoutManager = LinearLayoutManager(this)
                val searchHotAdapter = SearchHotAdapter(it)
                searchHotAdapter.setOnItemClick(object : SearchHotAdapter.OnItemClick {
                    override fun onItemClick(view: View?, position: Int) {
                        val searchWord = it.data[position].searchWord
                        binding.etSearch.setText(searchWord)
                        binding.etSearch.setSelection(searchWord.length)
                        search()
                    }
                })
                binding.rvSearchHot.adapter = searchHotAdapter
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {
        binding.apply {
            // ivBack
            ivBack.setOnClickListener {
                if (clPanel.visibility == View.VISIBLE) {
                    finish()
                } else {
                    clPanel.visibility = View.VISIBLE
                }
            }
            // 搜索
            btnSearch.setOnClickListener { search() }


            searchTypeView.setMainFabClosedDrawable(
                resources.getDrawable(
                    SearchType.getIconRes(
                        searchType
                    )
                )
            )

            searchTypeView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.search_type_single,
                    R.drawable.ic_baseline_music_single_24
                ).setLabel("单曲").create()
            )
            searchTypeView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.search_type_album,
                    R.drawable.ic_baseline_album_24
                ).setLabel("专辑").create()
            )
            searchTypeView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.search_type_playlist,
                    R.drawable.ic_baseline_playlist_24
                ).setLabel("歌单").create()
            )
            searchTypeView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.search_type_singer,
                    R.drawable.ic_baseline_singer_24
                ).setLabel("歌手").create()
            )

            searchTypeView.setOnActionSelectedListener { item ->
                searchTypeView.setMainFabClosedDrawable(item.getFabImageDrawable(this@SearchActivity))
                searchType = SearchType.getSearchType(item.id)
                mmkv.encode(Config.SEARCH_TYPE, searchType.toString())
                searchTypeView.close()
                search()
                return@setOnActionSelectedListener true
            }
        }

        // 搜索框
        binding.etSearch.apply {
            setOnEditorActionListener { _, p1, _ ->
                if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
                    search()
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (binding.etSearch.text.toString() != "") {
                        binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
                    } else {
                        binding.ivClear.visibility = View.INVISIBLE // 隐藏
                    }
                }
            })
        }


        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
        }

    }


    /**
     * 搜索音乐
     */
    private fun search() {
        // 关闭软键盘
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)

        var keywords = binding.etSearch.text.toString()

        if (keywords == "") {
            keywords = realKeyWord
            binding.etSearch.setText(keywords)
            binding.etSearch.setSelection(keywords.length)
        }
        if (keywords != "") {
            when (searchViewModel.searchEngine.value) {
                SearchViewModel.ENGINE_NETEASE -> {
                    GlobalScope.launch {
                        val result = Api.searchMusic(keywords, searchType)
                        if (result != null) {
                            withContext(Dispatchers.Main) {
                                when (searchType) {
                                    SearchType.SINGLE -> initRecycleView(result.songs)
                                    SearchType.PLAYLIST -> initPlaylist(result.playlist, TAG_NETEASE)
                                    SearchType.ALBUM -> initAlbums(result.albums)
                                    SearchType.SINGER -> initSingers(result.singers)
                                }
                            }
                        }
                    }
                }
//
            }
            binding.clPanel.visibility = View.GONE
        }
    }

    private fun initSingers(singers: List<StandardSinger>) {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = SingerAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            intent.putExtra(SongPlaylistActivity.EXTRA_TYPE, SearchType.SINGER)
            startActivity(intent)
        }.apply {
            submitList(singers)
        }
    }

    private fun initRecycleView(songList: List<StandardSongData>) {
        runOnMainThread {
            binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
            binding.rvPlaylist.adapter = SongAdapter() {
                SongMenuDialog(this, this, it) {
                    toast("不支持删除")
                }.show()
            }.apply {
                submitList(songList)
            }
        }
    }

    private fun initPlaylist(playlists: List<StandardPlaylist>,tag:Int) {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = PlaylistAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, tag)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            startActivity(intent)
        }.apply {
            submitList(playlists)
        }
    }

    private fun initAlbums(albums: List<StandardAlbum>) {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylist.adapter = AlbumAdapter {
            val intent = Intent(this@SearchActivity, SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_ID, it.id.toString())
            intent.putExtra(SongPlaylistActivity.EXTRA_TYPE, SearchType.ALBUM)
            startActivity(intent)
        }.apply {
            submitList(albums)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 保存搜索引擎
        mmkv.encode(
            Config.SEARCH_ENGINE,
            searchViewModel.searchEngine.value ?: SearchViewModel.ENGINE_NETEASE
        )

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_alpha_exit
        )
    }


    override fun onBackPressed() {
        if (binding.clPanel.visibility == View.VISIBLE) {
            super.onBackPressed()
        } else {
            binding.clPanel.visibility = View.VISIBLE
        }
    }



}