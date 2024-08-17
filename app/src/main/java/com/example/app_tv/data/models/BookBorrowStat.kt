package com.example.app_tv.data.models

import android.os.Parcel
import android.os.Parcelable

data class BookBorrowStat(
    val bookId: Int,
    val bookName: String,
    val bookImage: String? = null,
    val bookQuantity: Int,
    val borrowCount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bookId)
        parcel.writeString(bookName)
        parcel.writeString(bookImage)
        parcel.writeInt(bookQuantity)
        parcel.writeInt(borrowCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookBorrowStat> {
        override fun createFromParcel(parcel: Parcel): BookBorrowStat {
            return BookBorrowStat(parcel)
        }

        override fun newArray(size: Int): Array<BookBorrowStat?> {
            return arrayOfNulls(size)
        }
    }
}
