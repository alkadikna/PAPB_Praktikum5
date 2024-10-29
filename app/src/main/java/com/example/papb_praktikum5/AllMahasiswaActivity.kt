package com.example.papb_praktikum5

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.papb_praktikum5.api.ApiConfig
import com.example.papb_praktikum5.model.Mahasiswa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllMahasiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AllMahasiswaScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMahasiswaScreen() {
    var mahasiswaList by remember { mutableStateOf<List<Mahasiswa>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Fetch all Mahasiswa
    LaunchedEffect(Unit) {
        isLoading = true
        mahasiswaList = getAllMahasiswa()
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "All Data Mahasiswa", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as Activity).finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    mahasiswaList?.let { list ->
                        items(list) { mahasiswa ->
                            MahasiswaRow(
                                mahasiswa = mahasiswa,
                                onDelete = { isMahasiswa ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val resultMessage = deleteMahasiswa(isMahasiswa.id.toString())
                                        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                                        if (resultMessage.contains("successfully")) {
                                            mahasiswaList = mahasiswaList?.filterNot { it.id == isMahasiswa.id }
                                        }
                                    }
                                },
                                onUpdate = { mahasiswa ->
                                    val intent = Intent(context, UpdateMahasiswaActivity::class.java).apply {
                                        putExtra("MAHASISWA_ID", mahasiswa.id)
                                        putExtra("MAHASISWA_NRP", mahasiswa.nrp)
                                        putExtra("MAHASISWA_NAMA", mahasiswa.nama)
                                        putExtra("MAHASISWA_EMAIL", mahasiswa.email)
                                        putExtra("MAHASISWA_JURUSAN", mahasiswa.jurusan)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun getAllMahasiswa(): List<Mahasiswa>? {
    return withContext(Dispatchers.IO) {
        try {
            val response = ApiConfig.apiService.getAllMahasiswa()
            if (response.isSuccessful && response.body() != null) {
                val allMahasiswaResponse = response.body()
                if (allMahasiswaResponse != null && allMahasiswaResponse.status) {
                    allMahasiswaResponse.data
                } else {
                    Log.e("API_ERROR", "Failed to fetch Mahasiswa: ${allMahasiswaResponse?.status}")
                    null
                }
            } else {
                Log.e("API_ERROR", "Error fetching data: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred: ${e.message}", e)
            null
        }
    }
}





@Preview(showBackground = true)
@Composable
fun AddMahasiswaScreenPreview() {
    AllMahasiswaScreen()
}