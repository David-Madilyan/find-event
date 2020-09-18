package com.madcryk.findevent.impl

import android.graphics.Bitmap
import com.madcryk.findevent.models.EventModel

interface ItemListener {
    fun onClickItem(item : EventModel, bitmap: Bitmap)
}