package com.example.scanime.navigation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.navigation.NavType
import com.example.scanime.model.UiMangaModel
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

@RequiresApi(Build.VERSION_CODES.O)
val mangaNavType: NavType<UiMangaModel> = object : NavType<UiMangaModel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): UiMangaModel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            bundle.getParcelable(key, UiMangaModel::class.java)
        else
            bundle.getParcelable(key)
    }

    override fun parseValue(value: String): UiMangaModel {
        val decoded = URLDecoder.decode(value, "UTF-8")
        return Json.decodeFromString(UiMangaModel.serializer(), decoded)
    }

    override fun serializeAsValue(value: UiMangaModel): String {
        val json = Json.encodeToString(UiMangaModel.serializer(), value)
        return URLEncoder.encode(json, "UTF-8")
    }

    override fun put(bundle: Bundle, key: String, value: UiMangaModel) {
        bundle.putParcelable(key, value)
    }
}
