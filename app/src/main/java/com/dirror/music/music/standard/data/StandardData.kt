package com.dirror.music.music.standard.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

const val SOURCE_LOCAL = 1
const val SOURCE_NETEASE = 2



const val SOURCE_NETEASE_CLOUD = 6


val EMPTY_STANDARD_SONG = StandardSongData(
    SOURCE_LOCAL,
    "-1",
    "",
    "",
    ArrayList(),
    null,
    null,
    null
)

@Keep
@Parcelize
data class StandardLocalPlaylistData(
    val playlists: ArrayList<StandardPlaylistData>
) : Parcelable



