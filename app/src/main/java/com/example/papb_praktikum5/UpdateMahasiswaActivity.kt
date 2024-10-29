package com.example.papb_praktikum5

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateMahasiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idMahasiswa = intent.getStringExtra("MAHASISWA_ID") ?: ""
        val nrpMahasiswa = intent.getStringExtra("MAHASISWA_NRP") ?: ""
        val namaMahasiswa = intent.getStringExtra("MAHASISWA_NAMA") ?: ""
        val emailMahasiswa = intent.getStringExtra("MAHASISWA_EMAIL") ?: ""
        val jurusanMahasiswa = intent.getStringExtra("MAHASISWA_JURUSAN") ?: ""

        setContent {
            UpdateMahasiswaScreen(
                initialId = idMahasiswa,
                initialNrp = nrpMahasiswa,
                initialNama = namaMahasiswa,
                initialEmail = emailMahasiswa,
                initialJurusan = jurusanMahasiswa
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateMahasiswaScreen(
    initialId: String,
    initialNrp: String,
    initialNama: String,
    initialEmail: String,
    initialJurusan: String
) {
    var id by remember { mutableStateOf(initialId) }
    var nrp by remember { mutableStateOf(initialNrp) }
    var nama by remember { mutableStateOf(initialNama) }
    var email by remember { mutableStateOf(initialEmail) }
    var jurusan by remember { mutableStateOf(initialJurusan) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Update Data Mahasiswa", fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Update data dengan id: $id")

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nrp,
                onValueChange = { nrp = it },
                label = { Text("NRP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = jurusan,
                onValueChange = { jurusan = it },
                label = { Text("Jurusan") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        isLoading = true
                        val (isUpdated, message) = updateMahasiswa(id, nrp, nama, email, jurusan, initialNrp, initialNama, initialEmail, initialJurusan)
                        isLoading = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}



suspend fun updateMahasiswa(
    id: String,
    nrp: String,
    nama: String,
    email: String,
    jurusan: String,
    initialNrp: String,
    initialNama: String,
    initialEmail: String,
    initialJurusan: String
): Pair<Boolean, String> {
    return withContext(Dispatchers.IO) {
        // Cek jika input sama dengan initialValue atau kosong
        if (nrp.isBlank() || nama.isBlank() || email.isBlank() || jurusan.isBlank()) {
            // Input tidak boleh kosong
            return@withContext Pair(false, "Semua kolom harus diisi.")
        } else if (nrp == initialNrp && nama == initialNama && email == initialEmail && jurusan == initialJurusan) {
            // Tidak ada perubahan data
            return@withContext Pair(false, "Tidak ada perubahan pada data.")
        }

        try {
            Log.d("API_CALL", "Updating Mahasiswa with ID: $id")
            val response = ApiConfig.apiService.updateMahasiswa(id, nrp, nama, email, jurusan)

            if (response.isSuccessful && response.body() != null) {
                val updateResponse = response.body()
                if (updateResponse?.isStatus == true) {
                    Log.d("API_SUCCESS", "Mahasiswa updated successfully")
                    Pair(true, "Mahasiswa berhasil diperbarui.")
                } else {
                    Log.e("API_ERROR", "Failed to update Mahasiswa: ${updateResponse?.message}")
                    Pair(false, "Gagal memperbarui Mahasiswa: ${updateResponse?.message}")
                }
            } else {
                Log.e("API_ERROR", "Error: ${response.message()} (Code: ${response.code()}) Response: ${response.errorBody()?.string()}")
                Pair(false, "Error: ${response.message()} (Code: ${response.code()})")
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred: ${e.message}", e)
            Pair(false, "Terjadi kesalahan: ${e.message}")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UpdateMahasiswaScreenPreview() {
    UpdateMahasiswaScreen(initialId = "initial",
        initialNrp = "initial",
        initialNama = "initial",
        initialEmail = "initial",
        initialJurusan = "initial"
    )
}