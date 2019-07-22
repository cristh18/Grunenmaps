package com.leinaro.grunenthal.models

import android.os.Parcel
import android.os.Parcelable

data class SearchParameters(val franchise: String,
                            val channel: Int = 0) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(franchise)
        writeInt(channel)
    }

    override fun toString(): String {
        return "SearchParameters(franchise=$franchise, channel=$channel)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SearchParameters> = object : Parcelable.Creator<SearchParameters> {
            override fun createFromParcel(source: Parcel): SearchParameters = SearchParameters(source)
            override fun newArray(size: Int): Array<SearchParameters?> = arrayOfNulls(size)
        }
    }
}