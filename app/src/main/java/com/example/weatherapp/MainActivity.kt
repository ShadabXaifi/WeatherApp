package com.example.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
                         .addConverterFactory(GsonConverterFactory.create())
                         .baseUrl("https://api.openweathermap.org/data/2.5/")
                         .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "Api_Key", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(
                p0: Call<WeatherApp?>,
                p1: Response<WeatherApp?>
            ) {
                val responseBody = p1.body()
                if (p1.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = "$temperature"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.Day.text = dayName(System.currentTimeMillis())
                        binding.Date.text = date()
                        binding.cityName.text = "$cityName"

                    changeImagesAccordingToWeatherCondition(condition)

                }
            }

            private fun changeImagesAccordingToWeatherCondition(condition : String) {
                when (condition){
                    "Clear Sky", "Sunny", "Clear" -> {
                        binding.root.setBackgroundResource(R.drawable.sunny_background)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }

                    "Few Clouds", "Scattered Clouds", "Broken Clouds", "Partly Clouds", "Clouds", "Overcast Clouds", "Partly Cloudy" -> {
                        binding.root.setBackgroundResource(R.drawable.colud_background)
                        binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    }

                    "Mist", "Fog", "Haze", "Smoke", "Dust", "Sand", "Ash" -> {
                        binding.root.setBackgroundResource(R.drawable.colud_background)
                        binding.lottieAnimationView.setAnimation(R.raw.dust)
                    }

                    "Rain", "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Thunderstorm", "Thunderstorm with Rain" -> {
                        binding.root.setBackgroundResource(R.drawable.rain_background)
                        binding.lottieAnimationView.setAnimation(R.raw.rain)
                    }

                    "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Sleet", "Snow Showers" ->{
                        binding.root.setBackgroundResource(R.drawable.snow_background)
                        binding.lottieAnimationView.setAnimation(R.raw.snow)
                    }
                    else -> {
                        binding.root.setBackgroundResource(R.drawable.sunny_background)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }
                }

                binding.lottieAnimationView.playAnimation()
            }

            private fun date(): String{
                val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
                return sdf.format((Date()))
            }

            private fun time(timestamp: Long): String{
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                return sdf.format((Date(timestamp*1000)))
            }

            override fun onFailure(
                p0: Call<WeatherApp?>,
                p1: Throwable
            ) {
                TODO("Not yet implemented")
            }

        })
    }

    fun dayName(timestamp: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
