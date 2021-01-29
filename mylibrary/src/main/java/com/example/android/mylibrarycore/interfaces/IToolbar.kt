package com.example.android.mylibrarycore.interfaces

import androidx.appcompat.widget.Toolbar

interface IToolbar {
    fun toolbarToLoad(toolbar: Toolbar?)
    fun enableHomeDisplay(value: Boolean)
}