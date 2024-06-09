package com.mojapogoda

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojapogoda.ui.theme.MojaPogodaTheme

class TempActivity : ComponentActivity() {
    lateinit var cityData: ApiCity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityData = ApiCity(
            intent.extras?.getString("city")!!,
            intent.extras?.getString("country")!!,
            intent.extras?.getDouble("lat", -1.0)!!,
            intent.extras?.getDouble("lot", -1.0)!!,
        )

        enableEdgeToEdge()
        setContent {
            var cityTemp by remember { mutableDoubleStateOf(0.0) }
            val ctx = LocalContext.current
            val update = {
                val api = ApiMethod()
                api.getCityCelsius(cityData,
                    { temp -> cityTemp = temp },
                    { e -> runOnUiThread { Toast.makeText(ctx, "${e.code} ${e.msg}", Toast.LENGTH_SHORT).show() } })
            }
            update()

            MojaPogodaTheme {
                Scaffold(
                    bottomBar = {
                        Column (
                            modifier = Modifier.fillMaxWidth()
                                .padding(32.dp)
                        ){
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = update) {
                                Text("Update")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column (modifier = Modifier.padding(innerPadding)) {
                        Text(text = cityData.cityName, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text(cityData.country, fontSize = 20.sp)

                        Row (
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$cityTemp °C", fontSize = 42.sp)
                        }
                    }

                }

            }
        }
    }
}
