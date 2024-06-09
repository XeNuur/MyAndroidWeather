package com.mojapogoda

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ApiTrap(val code: Int, val msg: String) : Exception(msg)

class ApiCity(
    val cityName: String,
    val country: String,
    val longitude: Double,
    val latitude:  Double,
)

class ApiMethod {
    private val WEATHER_URL_FORMAT = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric"
    private val GEOCODE_URL_FORMAT = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s"
    private val APP_KEY = "74c7b6d44d86f67c3bfaafbb9cc9ddf2"

    fun getCityList(
        cityName: String,
        callback: (MutableList<ApiCity>) -> Unit,
        trapCb: (ApiTrap) -> Unit
    ) {
        var ret = emptyList<ApiCity>().toMutableList()
        prepReqInner(
            {
                msg -> callback(parseResp(msg))
            },
            trapCb,
            GEOCODE_URL_FORMAT,
            cityName
        )
    }
    fun getCityCelsius(
        city: ApiCity,
        callback: (Double) -> Unit,
        trapCb: (ApiTrap) -> Unit
    ) {
        prepReqInner(
            { msg ->
                try {
                    val rootObj = JSONObject(msg)
                    callback(rootObj.getJSONObject("main").getDouble("temp"))
                }
                catch (e: Exception) {
                    trapCb(ApiTrap(-1, e.toString()))
                }
            },
            trapCb,
            WEATHER_URL_FORMAT,
            city.latitude.toString(),
            city.longitude.toString()
        )
    }

    private fun prepReqInner(callback: (String) -> Unit, callbackTrap: (ApiTrap) -> Unit, fmtUrl: String, vararg data: String) {
        val fixedUrl = String.format(fmtUrl, *data, APP_KEY)
        val client = OkHttpClient()
        val req = Request.Builder().url(fixedUrl).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                callback(response.body!!.string())
            }
            override fun onFailure(call: Call, e: IOException) {
                callbackTrap(ApiTrap(1, e.toString()))
            }
        })
    }

    private fun parseResp(mess: String): MutableList<ApiCity> {
        val ret = emptyList<ApiCity>().toMutableList()
        try {
            val rootArr = JSONArray(mess)
            for (i in 0 until rootArr.length()) {
                val item = rootArr.getJSONObject(i)
                ret += parseRespEntry(item)
            }
        } catch(e: Exception) {
            throw ApiTrap(-1, e.toString())
        }
        return ret
    }

    private fun parseRespEntry(ent: JSONObject): ApiCity {
        try {
            val city = ent.getString("name")
            val country = ent.getString("country")
            val lon = ent.getDouble("lon")
            val lat = ent.getDouble("lat")

            return ApiCity(city, country, lon, lat)
        } catch(e: Exception) {
            throw ApiTrap(-1, e.toString())
        }
    }

    private fun parseTrap(mess: String): ApiTrap {
        try {
            val obj = JSONObject(mess)
            val code = obj.getInt("cod")
            val msg = obj.getString("message")
            return ApiTrap(code, msg)

        } catch (e: Exception) {
            return ApiTrap(-1, e.toString())
        }
    }
}
