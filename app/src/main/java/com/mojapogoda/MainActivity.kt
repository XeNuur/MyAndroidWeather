package com.mojapogoda

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mojapogoda.ui.theme.MojaPogodaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MojaPogodaTheme {
                Greeting()
            }
        }
    }

    @Composable
    fun Greeting() {
        var cityName by remember {mutableStateOf("")}
        var cityList by remember { mutableStateOf(emptyList<ApiCity>())}
        val ctx = LocalContext.current

        Scaffold (
            bottomBar = {
                Column (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = cityName,
                        onValueChange = {cityName = it},
                        label = {Text("City")}
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (cityName.isBlank())
                                return@Button

                            val api = ApiMethod()
                            api.getCityList(cityName, { list ->
                                if (list.isEmpty()) {
                                    runOnUiThread { Toast.makeText(ctx, "Invalid city name!", Toast.LENGTH_SHORT).show() }
                                    return@getCityList
                                }
                                cityList = list
                            }, {e -> runOnUiThread { Toast.makeText(ctx, "(${e.code}) ${e.msg}", Toast.LENGTH_SHORT).show() } })
                        }
                    )
                    {
                        Text("Search")
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(innerPadding)
            ){
                items(cityList) {item -> CityElement(Modifier, item) }
            }
        }
    }

    @Composable
    fun CityElement(modifier: Modifier, city: ApiCity) {
        val ctx = LocalContext.current
        Row (
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Text(city.cityName)
                Text(city.country)
            }

            Button( onClick = {
                val intent = Intent(ctx, TempActivity::class.java)
                intent.putExtra("city", city.cityName)
                intent.putExtra("country", city.country)
                intent.putExtra("lat", city.latitude)
                intent.putExtra("lon", city.longitude)
                ctx.startActivity(intent)
            })
            { Text("Select!") }
        }
    }
}
