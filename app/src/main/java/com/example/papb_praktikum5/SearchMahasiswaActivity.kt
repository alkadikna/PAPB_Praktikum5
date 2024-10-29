package com.example.papb_praktikum5

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.papb_praktikum5.api.ApiConfig
import com.example.papb_praktikum5.model.Mahasiswa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SearchMahasiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchMahasiswaScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMahasiswaScreen() {
    var nrp by remember { mutableStateOf("") }
    var mahasiswa by remember { mutableStateOf<Mahasiswa?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Search Data Mahasiswa", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = nrp,
                onValueChange = { nrp = it },
                label = { Text("NRP") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = {
                    if (nrp.isEmpty()) {
                        Toast.makeText(context, "Silakan isi NRP terlebih dahulu", Toast.LENGTH_SHORT).show()
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            isLoading = true
                            mahasiswa = searchMahasiswa(nrp)
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Cari Data")
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(vertical = 16.dp),
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    item {
                        mahasiswa?.let { currentMahasiswa ->
                            MahasiswaRow(
                                mahasiswa = currentMahasiswa,
                                onDelete = { isMahasiswa ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading = true
                                        val resultMessage = deleteMahasiswa(isMahasiswa.id.toString())
                                        isLoading = false
                                        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                                        if (resultMessage.contains("successfully")) {
                                            mahasiswa = null
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
                                    Toast.makeText(context, "Update Mahasiswa: ${mahasiswa.nama}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } ?: run {
                            Text(text = "Data tidak ditemukan")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MahasiswaRow(
    mahasiswa: Mahasiswa,
    onDelete: (Mahasiswa) -> Unit,
    onUpdate: (Mahasiswa) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,

        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "NRP: ${mahasiswa.nrp}")
                    Text(text = "Nama: ${mahasiswa.nama}")
                    Text(text = "Email: ${mahasiswa.email}")
                    Text(text = "Jurusan: ${mahasiswa.jurusan}")
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown icon",
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .size(24.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onUpdate(mahasiswa) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
                    ) {
                        Text(text = "Update")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onDelete(mahasiswa) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Red
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}


suspend fun searchMahasiswa(nrp: String): Mahasiswa? {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("API_CALL", "Searching for NRP: $nrp")
            val response = ApiConfig.apiService.getMahasiswa(nrp)

            if (response.isSuccessful && response.body() != null) {
                val mahasiswaResponse = response.body()
                if (mahasiswaResponse?.status == true) {
                    mahasiswaResponse.data?.firstOrNull()
                } else {
                    Log.e("API_ERROR", "Invalid status in response: ${mahasiswaResponse?.status}")
                    null
                }
            } else {
                Log.e("API_ERROR", "Error: ${response.message()} (Code: ${response.code()}) Response: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred: ${e.message}", e)
            null
        }
    }
}

suspend fun deleteMahasiswa(id: String): String {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("API_CALL", "Deleting Mahasiswa with ID: $id")
            val response = ApiConfig.apiService.deleteMahasiswa(id)

            if (response.isSuccessful && response.body() != null) {
                val deleteResponse = response.body()
                return@withContext if (deleteResponse?.isStatus == true) {
                    Log.d("API_SUCCESS", "Mahasiswa deleted successfully")
                    "Mahasiswa deleted successfully."
                } else {
                    Log.e("API_ERROR", "Failed to delete Mahasiswa: ${deleteResponse?.message}")
                    "Failed to delete Mahasiswa: ${deleteResponse?.message ?: "Unknown error."}"
                }
            } else {
                Log.e("API_ERROR", "Error: ${response.message()} (Code: ${response.code()}) Response: ${response.errorBody()?.string()}")
                "Error: ${response.message()} (Code: ${response.code()})"
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred: ${e.message}", e)
            "Exception occurred: ${e.message}"
        }
    }
}






@Preview(showBackground = true)
@Composable
fun SearchMahasiswaScreenPreview() {
    SearchMahasiswaScreen()
}